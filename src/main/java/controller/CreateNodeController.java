package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Edge;
import model.Node;
import service.ResourceLoader;
import service.StageManager;

import java.util.ArrayList;

public class CreateNodeController extends Controller {

    @FXML
    AnchorPane anchor_pane;
    @FXML
    JFXButton cancel_btn, next_btn;
    @FXML
    ScrollPane map_scrollpane;
    @FXML
    Slider zoom_slider;
    @FXML
    Label instruction_label;
    @FXML
    VBox node_info_vbox;
    @FXML
    JFXTextField floor_field, type_field, short_field, long_field;

    private final Color DEFAULT_NODE_COLOR = Color.BLACK;
    private final Color SELECTED_NODE_COLOR = Color.RED;
    private final Color EDGE_COLOR = Color.RED;

    // global group for all map entities (to be scaled on zoom)
    Group zoomGroup;
    // State iteration for seperate interaction
    // 0 - select new node location (Start)
    // 1 - add node info select connected nodes
    // 2 - select connected nodes (End)
    int stateIterator;
    // Circle to represent location of New Node
    Circle displayCircle;
    // Node data
    Node myCreatedNode;
    // Node info
    String node_floor = "";
    String node_type = "";
    String node_short = "";
    String node_long = "";
    ArrayList<Edge> edges;

    @FXML
    void initialize() {
        // start state at 0
        stateIterator = 0;

        // remove texfields to SHOW MAP
        hideTextFields();

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);

        // Setting View Scrolling
        zoom_slider.setMin(0.3);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.3);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.3);

        // render the state
        renderState();
    }

    @FXML
    void cancelAction(ActionEvent e) throws Exception {
        showHome();
    }

    @FXML
    void nextAction(ActionEvent e) {
        System.out.println("just clicked next");
        nextState();
        renderState();
    }

    @FXML
    void mapClickedHandler(MouseEvent e) {
        // only need this for the first state
        // (selecting initial node location)
        if (stateIterator == 0) {
            System.out.println("x: " + e.getX());
            System.out.println("y: " + e.getY());
            double mouseX = e.getX();
            double mouseY = e.getY();
            // create circle
            Circle myNewNode = new Circle();
            myNewNode.setCenterX(mouseX);
            myNewNode.setCenterY(mouseY);
            myNewNode.setRadius(20);
            myNewNode.setFill(Color.BLUE);
            // remove last drawn circle
            if (zoomGroup.getChildren().contains(displayCircle)) {
                zoomGroup.getChildren().remove(displayCircle);
            }
            // set circle as global instance
            displayCircle = myNewNode;
            // draw circle on the screen
            zoomGroup.getChildren().add(displayCircle);
            // enable submit button
            next_btn.setDisable(false);
            // Ask DB for new
            // create new node with x & y
            myCreatedNode = new Node("PLACEHOLDER_ID", (int) mouseX, (int) mouseY);
        }
    }

    // This function is bound to the generated node circles
    // It will run every time you click a node to add as edge
    @FXML
    void handleNodeClicked(MouseEvent e, Node selectedNode) {
        Circle nodeCircle = (Circle) e.getSource();
        // find if the node was already clicked
        if (nodeCircle.getFill().equals(DEFAULT_NODE_COLOR)) {
            // add node to edge list

//            edges.add();
        } else {
            // already selected
            // remove this as an edge

        }
    }

    // iterate to next state
    void nextState() {
        if (stateIterator == 2) {
            // SUBMIT AND REDIRECT
            // the node is fully created,
            // we are done here
        } else {
            stateIterator++;
        }
    }

    // render specific window configurations based on state
    void renderState() {
        switch (stateIterator) {
            case 0:
                nodeLocation();
                break;
            case 1:
                addNodeInfo();
                break;
            case 2:
                connectEdges();
                break;
        }
    }

    // STATE: find the location of new node on map
    void nodeLocation() {
        // show instructions
        instruction_label.setText("Click a location on the map for the new node");
        next_btn.setDisable(true);  // initialy disabled (gets enabled in mapClickedHandler)
        next_btn.setText("Confirm Location");
    }

    // STATE: populate node information
    void addNodeInfo() {
        // setup info hooks
        addTextFieldHooks();
        // display TextFields
        showTextFields();
        // show instructions
        instruction_label.setText("Specify node information");
        next_btn.setDisable(true); // re-enables at checkAllFields (when validation passes)
        next_btn.setText("Add Info");
    }

    // STATE: select all reachable nodes from newly created node
    void connectEdges() {
        // create node
        buildNode();
        // re-show map
        hideTextFields();
        // create edges list
        edges = new ArrayList<Edge>();
        // display the nodes on the map
        showAllNodes();
        // show instructions
        instruction_label.setText("Select all nodes that are reachable");
        next_btn.setDisable(true);
        next_btn.setText("Set Edges");
    }

    // remove textfields from screen
    void hideTextFields() {
        anchor_pane.getChildren().remove(node_info_vbox);
        if (!anchor_pane.getChildren().contains(map_scrollpane)) {
            anchor_pane.getChildren().add(map_scrollpane);
            anchor_pane.getChildren().add(zoom_slider);
        }
    }

    // shows textfields on screen
    void showTextFields() {
        anchor_pane.getChildren().remove(map_scrollpane);
        anchor_pane.getChildren().remove(zoom_slider);
        if (!anchor_pane.getChildren().contains(node_info_vbox)) {
            anchor_pane.getChildren().add(node_info_vbox);
        }
    }

    // takes new text changes and adds them to info variables
    void addTextFieldHooks() {

        floor_field.textProperty().addListener((observable, oldValue, newValue) -> {
            node_floor = newValue;
            checkAllFields();
        });
        type_field.textProperty().addListener((observable, oldValue, newValue) -> {
            node_type = newValue;
            checkAllFields();
        });
        long_field.textProperty().addListener((observable, oldValue, newValue) -> {
            node_long = newValue;
            checkAllFields();
        });
        short_field.textProperty().addListener((observable, oldValue, newValue) -> {
            node_short = newValue;
            checkAllFields();
        });
    }

    // will allow next button to be clicked if valid fields
    void checkAllFields() {
        if (isValid()) {
            // all nodes are passing
            next_btn.setDisable(false);
        } else {
            // validation fails
            next_btn.setDisable(true);
        }

    }

    // create node from existing node info
    void buildNode() {
        // nodeID key:
        String nodeID = "X" + node_type + genNodeNumber();
    }

    String genNodeNumber() {
        String str = "";
//        dbs.get
        return str;
    }

    // validation for node info
    boolean isValid() {
        return (!node_floor.isEmpty() && !node_type.isEmpty() && !node_short.isEmpty() && !node_long.isEmpty());
    }

    void showAllNodes() {
        ArrayList<Node> allNodes = dbs.getAllNodes();
        for (Node node : allNodes) {
            // create circle
            Circle nodeCircle = new Circle();
            nodeCircle.setCenterX(node.getXcoord());
            nodeCircle.setCenterY(node.getYcoord());
            nodeCircle.setRadius(20);
            nodeCircle.setFill(DEFAULT_NODE_COLOR);
            // draw circle on the screen
            nodeCircle.setOnMouseClicked(e -> handleNodeClicked(e, node));
            zoomGroup.getChildren().add(nodeCircle);
        }
    }

    // handle zoom map functionality
    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    // Change window back to home screen
    void showHome() throws Exception {
        // open the new editor window
        Parent root = FXMLLoader.load(ResourceLoader.home);
        Stage mainStage = (Stage) cancel_btn.getScene().getWindow();
        StageManager.changeExistingWindow(mainStage, root, "Home");
    }
}
