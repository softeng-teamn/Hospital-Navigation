package map.create_node;

import application_state.ApplicationState;
import com.jfoenix.controls.*;
import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import map.Edge;
import map.Node;
import database.DatabaseService;
import net.kurobako.gesturefx.GesturePane;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * controls the create node FXML
 */
public class CreateNodeController {

    @FXML
    JFXComboBox<String> nodeType_combo, building_combo;
    @FXML
    AnchorPane anchor_pane;
    @FXML
    JFXButton cancel_btn, next_btn;
    @FXML
    GesturePane gPane;
    @FXML
    JFXSlider zoom_slider;
    @FXML
    Label instruction_label;
    @FXML
    VBox node_info_vbox, floor_change_vbox;
    @FXML
    JFXTextField short_field, long_field;
    @FXML
    JFXListView<String> time_view;
    @FXML
    StackPane map_stack_pane;


    private final Color DEFAULT_NODE_COLOR = Color.BLACK;
    private final Color SELECTED_NODE_COLOR = Color.RED;

    // global group for all map entities (to be scaled on zoom)
    Group zoomGroup;
    // State iteration for seperate interaction
    // 0 - select new node location (Start)
    // 1 - add node info select connected nodes
    // 2 - select connected nodes
    // 3 - tell the user WE DID IT! (End)
    int stateIterator;
    // Circle to represent location of New Node
    Circle displayCircle;
    // Node data
    Node myCreatedNode;
    // Node info
    String node_floor = "1";
    String node_type = "";
    String node_short = "";
    String node_long = "";
    String node_building = "";
    // Collection of Node Circles
    ArrayList<Circle> circleCollection = new ArrayList<>();
    // Collection of Edge Nodes to connect to
    ArrayList<Node> chosenEdgeNodes;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();
    // Scroll & Zoom
    private static HashMap<String, ImageView> imageCache;
    private ImageView floorImg;
    private static final double MIN_ZOOM = 0.4;
    private static final double MAX_ZOOM = 1.2;

    @FXML
    void initialize() {

        zoomSliderInit();
        zoomGroupInit();
        imagesInit();

        nodeType_combo.getItems().addAll("HALL", "ELEV", "REST", "STAI", "DEPT", "LABS", "INFO", "CONF", "EXIT", "RETL", "SERV");
        building_combo.getItems().addAll("BTM", "Shapiro", "Tower", "45 Francis", "15 Francis", "FLEX");

        // start state at 0
        stateIterator = 0;

        // remove texfields to SHOW MAP
        hideTextFields();

        // render the state
        renderState();
    }

    void zoomGroupInit() {
        zoomGroup = new Group();
        zoomGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                mapClickedHandler(t);
            }
        });
        gPane.setContent(zoomGroup);
    }

    void zoomSliderInit() {
        gPane.currentScaleProperty().setValue(MIN_ZOOM+0.1);
        zoom_slider.setMin(MIN_ZOOM);
        zoom_slider.setMax(MAX_ZOOM);
        zoom_slider.setIndicatorPosition(JFXSlider.IndicatorPosition.RIGHT);
        zoom_slider.setValue(gPane.getCurrentScale());
        gPane.currentScaleProperty().bindBidirectional(zoom_slider.valueProperty());
    }

    void imagesInit() {
        imageCache = ApplicationState.getApplicationState().getImageCache();
        this.floorImg = imageCache.get("1");
        setFloor("1"); // DEFAULT
    }

    @FXML
    void cancelAction(ActionEvent e) throws Exception {
        showHome();
    }

    @FXML
    void nextAction(ActionEvent e) throws Exception {
        System.out.println("just clicked next");
        nextState();
        renderState();
    }

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

    // switch floor to new map image
    public void setFloor(String floor) {
        ImageView newImg;
        if (imageCache.containsKey(floor)) {
            newImg = imageCache.get(floor);
        } else {
            // unknown floor change | SETTING TO DEFAULT
            newImg = imageCache.get("1");
        }
        zoomGroup.getChildren().remove(this.floorImg);
        zoomGroup.getChildren().add(0,newImg);
        this.floorImg = newImg;
    }

    @FXML
    void floorChangeAction(ActionEvent e) throws IOException {
        JFXButton clickedBtn = (JFXButton) e.getSource();
        switch (clickedBtn.getText()) {
            case "4":
                if(stateIterator == 0) {node_floor = "4";}
                else if(stateIterator == 2) {showAllNodes("4");}
                break;
            case "3":
                if(stateIterator == 0) {node_floor = "3";}
                else if(stateIterator == 2) {showAllNodes("3");}
                break;
            case "2":
                if(stateIterator == 0) {node_floor = "2";}
                else if(stateIterator == 2) {showAllNodes("2");}
                break;
            case "1":
                if(stateIterator == 0) {node_floor = "1";}
                else if(stateIterator == 2) {showAllNodes("1");}
                break;
            case "L1":
                if(stateIterator == 0) {node_floor = "L1";}
                else if(stateIterator == 2) {showAllNodes("L1");}
                break;
            case "L2":
                if(stateIterator == 0) {node_floor = "L2";}
                else if(stateIterator == 2) {showAllNodes("L2");}
                break;
            case "G":
                if(stateIterator == 0) {node_floor = "G";}
                else if(stateIterator == 2) {showAllNodes("G");}
                break;
            default:
                System.out.println("WHAT BUTTON WAS PRESSED?????");
                break;
        }
        setFloor(clickedBtn.getText());
    }

    // This function is bound to the generated node circles
    // It will run every time you click a node to add as edge
    @FXML
    void handleNodeClicked(MouseEvent e, Node selectedNode) {
        Circle nodeCircle = (Circle) e.getSource();
        // find if the node was already clicked
        if (nodeCircle.getFill().equals(DEFAULT_NODE_COLOR)) {
            // add to selected node list
            chosenEdgeNodes.add(selectedNode);
            // change color
            nodeCircle.setFill(SELECTED_NODE_COLOR);
        } else {
            // already selected
            // change color
            nodeCircle.setFill(DEFAULT_NODE_COLOR);
            // remove from selected node list
            chosenEdgeNodes.remove(selectedNode);
        }
        checkEnoughEdges();
    }


    void checkEnoughEdges() {
        if (chosenEdgeNodes.size() > 0) {
            next_btn.setDisable(false);
        } else {
            next_btn.setDisable(true);
        }
    }


    // iterate to next state
    void nextState() throws Exception {
        if (stateIterator == 3) {
            // SUBMIT AND REDIRECT
            // the node is fully created,
            // we are done here
            Parent root = FXMLLoader.load(ResourceLoader.home);
            Stage stage = (Stage) cancel_btn.getScene().getWindow();
            StageManager.changeExistingWindow(stage, root, "Home");
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
            case 3:
                submitNewNode();
                break;

                default:
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
        // create empty edge node collection
        chosenEdgeNodes = new ArrayList<Node>();
        // display the nodes on the chosen floor of the MAP
        showAllNodes(myCreatedNode.getFloor());
        // show instructions
        instruction_label.setText("Select all nodes that are reachable");
        next_btn.setDisable(true);
        next_btn.setText("Set Edges");
    }

    // STATE: populate the database with our info
    void submitNewNode() {
        // send info to dbs
        myDBS.insertNode(myCreatedNode);
        for (Node n : chosenEdgeNodes) {
            myDBS.insertEdge(new Edge(myCreatedNode, n));
        }
        // show instructions
        instruction_label.setText("Node Successfully Created!");
        next_btn.setDisable(false);
        next_btn.setText("Finish");
        // remove map
        anchor_pane.getChildren().remove(map_stack_pane);
    }

    // remove textfields from screen
    void hideTextFields() {
        anchor_pane.getChildren().remove(node_info_vbox);
        if (!anchor_pane.getChildren().contains(map_stack_pane)) {
            anchor_pane.getChildren().add(map_stack_pane);
        }
    }

    // shows textfields on screen
    void showTextFields() {
        anchor_pane.getChildren().remove(map_stack_pane);
        if (!anchor_pane.getChildren().contains(node_info_vbox)) {
            anchor_pane.getChildren().add(node_info_vbox);
        }
    }

    // takes new text changes and adds them to info variables
    void addTextFieldHooks() {
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
        String nodeID = "X" + node_type + genNodeNumber() + genFloorNumber();
        myCreatedNode.setNodeID(nodeID);
        myCreatedNode.setBuilding(node_building);
        myCreatedNode.setFloor(node_floor);
        myCreatedNode.setShortName(node_short);
        myCreatedNode.setLongName(node_long);
        myCreatedNode.setNodeType(node_type);
        System.out.println("Built node of ID: " + nodeID);
    }

    String genNodeNumber() {
        String str = "%3d";
        int numNodes = myDBS.getNumNodeTypeByFloor(node_type, node_floor);
        return String.format(str, numNodes);
    }

    String genFloorNumber() {
        String str = "%2s";
        return String.format(str, node_floor).replace(' ', '0');
    }

    // validation for node info
    boolean isValid() {
        return (!node_floor.isEmpty() && !node_type.isEmpty() && !node_short.isEmpty() && !node_long.isEmpty() && !node_building.isEmpty());
    }

    void showAllNodes(String floor) {
        ArrayList<Node> allNodes = myDBS.getNodesByFloor(floor);
        zoomGroup.getChildren().removeAll(circleCollection);
        circleCollection.clear();
        for (Node node : allNodes) {
            // create circle
            Circle nodeCircle = new Circle();
            nodeCircle.setCenterX(node.getXcoord());
            nodeCircle.setCenterY(node.getYcoord());
            nodeCircle.setRadius(20);
            if (chosenEdgeNodes.contains(node)) {
                nodeCircle.setFill(SELECTED_NODE_COLOR);
            } else {
                nodeCircle.setFill(DEFAULT_NODE_COLOR);
            }
            // draw circle on the screen
            nodeCircle.setOnMouseClicked(e -> handleNodeClicked(e, node));
            zoomGroup.getChildren().add(nodeCircle);
            circleCollection.add(nodeCircle);
        }
    }

    // Change window back to home screen
    void showHome() throws Exception {
        // open the new editor window
        Parent root = FXMLLoader.load(ResourceLoader.home);
        Stage mainStage = (Stage) cancel_btn.getScene().getWindow();
        StageManager.changeExistingWindow(mainStage, root, "Home");
    }

    @FXML
    public void nodeTypeAction(ActionEvent actionEvent) {
        node_type = nodeType_combo.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void buildingAction(ActionEvent actionEvent) {
        node_building = building_combo.getSelectionModel().getSelectedItem();
    }
}
