package map.edit_node;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import map.Edge;
import map.Node;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.ArrayList;

public class EditNodeController extends Control {

    Group zoomGroup;
    Circle selectedCircle = new Circle();
    Node tempEditNode;      // mutating the node based on edits
    boolean isEditEdges = false;
    ArrayList<Circle> circleCollection = new ArrayList<>();
    ArrayList<Node> edgeNodeCollection;
    static ArrayList<Edge> oldEdgesFromEditNode;
    double orgSceneX, orgSceneY;    // for circle dragging

    //    @FXML
//    private Pane img_pane;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private JFXTextField building_field, type_field, short_field, long_field;
    @FXML
    private JFXComboBox<String> floor_combo;
    @FXML
    private Pane image_pane;
    @FXML
    private VBox floor_change_vbox, controlls_vbox, edges_container;
    @FXML
    private JFXListView<String> edges_list;
    @FXML
    private JFXButton edit_show_btn;
    @FXML
    private MaterialIconView edit_icon_down, edit_icon_up;
    @FXML
    private Label node_id_label;
    @FXML
    private ToggleButton closedToggle;


    @FXML
    void initialize() {

        tempEditNode = ApplicationState.getApplicationState().getNodeToEdit();

        node_id_label.setText("Node: " + tempEditNode.getNodeID());


        fillEdges(tempEditNode);

        // set image
        try {
            setMapFloor(tempEditNode.getFloor());
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillNodeInfo();

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
        // Setting View Scrolling
        zoom_slider.setMin(0.3);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.4);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.4);
        drawSelectedCircle(tempEditNode.getXcoord(), tempEditNode.getYcoord());

        hideEdges();

    }


    void fillEdges(Node node) {
        edgeNodeCollection = DatabaseService.getDatabaseService().getNodesConnectedTo(node);
        for (Node n : edgeNodeCollection) {
            edges_list.getItems().add(n.getNodeID());
        }
        // these are for deleting later
        oldEdgesFromEditNode = DatabaseService.getDatabaseService().getAllEdgesWithNode(node.getNodeID());
    }

    @FXML
    void editToggle(ActionEvent e) {
        isEditEdges = !isEditEdges;
        if (isEditEdges) {
            showEdges();
        } else {
            hideEdges();
        }
    }

    void hideEdges() {
        edit_show_btn.setGraphic(edit_icon_down);
        controlls_vbox.getChildren().remove(floor_change_vbox);
        edges_container.getChildren().remove(edges_list);
        zoomGroup.getChildren().removeAll(circleCollection);
        circleCollection.clear();
    }

    void showEdges() {
        edit_show_btn.setGraphic(edit_icon_up);
        controlls_vbox.getChildren().add(floor_change_vbox);
        edges_container.getChildren().add(edges_list);
        drawFloorNodes(tempEditNode.getFloor());

    }

    void drawSelectedCircle(double x, double y) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(20);
        circle.setFill(Color.GREEN);
        circle.setCursor(Cursor.HAND);
        circle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                orgSceneX = t.getX() - circle.getCenterX();
                orgSceneY = t.getY() - circle.getCenterY();
            }
        });
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                circle.setCenterX(t.getX() - orgSceneX);
                circle.setCenterY(t.getY() - orgSceneY);
            }
        });
        selectedCircle = circle;
        zoomGroup.getChildren().add(circle);
        scrollTo(tempEditNode);
    }



    private void scrollTo(Node node) {
        // animation scroll to new position
        double mapWidth = zoomGroup.getBoundsInLocal().getWidth();
        double mapHeight = zoomGroup.getBoundsInLocal().getHeight();
        double scrollH = (Double) (node.getXcoord() / mapWidth);
        double scrollV = (Double) (node.getYcoord() / mapHeight);
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    void fillNodeInfo() {
        Node node = ApplicationState.getApplicationState().getNodeToEdit();
        building_field.setText(node.getBuilding());
        type_field.setText(node.getNodeType());
        floor_combo.getItems().add("3");
        floor_combo.getItems().add("2");
        floor_combo.getItems().add("1");
        floor_combo.getItems().add("L1");
        floor_combo.getItems().add("L2");
        floor_combo.getItems().add("G");
        floor_combo.getSelectionModel().select(node.getFloor());
        short_field.setText(node.getShortName());
        long_field.setText(node.getLongName());
        closedToggle.setSelected(node.isClosed());
    }

    @FXML
    void mapClickedHandler(MouseEvent e) {
        if (!isEditEdges) {
            double mouseX = e.getX();
            double mouseY = e.getY();
            zoomGroup.getChildren().remove(selectedCircle);
            drawSelectedCircle(mouseX, mouseY);
        }
    }

    private void setMapFloor(String floor) throws IOException {
        ImageView imageView;
        switch (floor) {
            case "3":
                imageView = new ImageView(new Image(
                        ResourceLoader.thirdFloor.openStream()));
                break;
            case "2":
                imageView = new ImageView(new Image(
                        ResourceLoader.secondFloor.openStream()));
                break;
            case "1":
                imageView = new ImageView(new Image(
                        ResourceLoader.firstFloor.openStream()));
                break;
            case "L1":
                imageView = new ImageView(new Image(
                        ResourceLoader.firstLowerFloor.openStream()));
                break;
            case "L2":
                imageView = new ImageView(new Image(
                        ResourceLoader.secondLowerFloor.openStream()));
                break;
            case "G":
                imageView = new ImageView(new Image(
                        ResourceLoader.groundFloor.openStream()));
                break;
            default:
                System.out.println("We should not have default here!!!");
                imageView = new ImageView(new Image(
                        ResourceLoader.groundFloor.openStream()));
                break;
        }
        image_pane.getChildren().clear();
        image_pane.getChildren().add(imageView);
    }

    @FXML
    void comboAction(ActionEvent e) throws IOException {
        String newFloor = floor_combo.getSelectionModel().getSelectedItem();
        tempEditNode.setFloor(newFloor);
        // hide any nodes on screen
        hideEdges();
        setMapFloor(newFloor);
        scrollTo(tempEditNode);
        System.out.println("Selecting NEW FLOOR RENDER: " + newFloor);
    }

    @FXML
    void deleteAction(ActionEvent e) throws IOException {
        ApplicationState.getApplicationState().setEdgesToEdit(oldEdgesFromEditNode);
        Parent root = FXMLLoader.load(ResourceLoader.deleteNodeConfirm);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Delete Confirmation");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(image_pane.getScene().getWindow());
        stage.showAndWait();

        if (ApplicationState.getApplicationState().getNodeToEdit() == null) {
            try {
                Parent myRoot = FXMLLoader.load(ResourceLoader.home);
                Stage myStage = (Stage)map_scrollpane.getScene().getWindow();
                StageManager.changeExistingWindow(myStage, myRoot, "Home");
            } catch (Exception execp) {
                execp.printStackTrace();
            }

        }

    }

    @FXML
    void saveAction(ActionEvent e) throws IOException {
        updateNode();
        ApplicationState.getApplicationState().setNodeToEdit(tempEditNode);
        // remove old edges
        for (Edge edge : oldEdgesFromEditNode) {
            DatabaseService.getDatabaseService().deleteEdge(edge);
        }
        // ADD EDGES TO THE DB
        ArrayList<Edge> newEdges = new ArrayList<>();
        for (Node node : edgeNodeCollection) {
            Edge edge = new Edge(tempEditNode, node);
            newEdges.add(edge);
            DatabaseService.getDatabaseService().insertEdge(edge);
        }
        // updating node
        DatabaseService.getDatabaseService().updateNode(tempEditNode);
        System.out.println("SHOW UPDATED NODE:");
        System.out.println(DatabaseService.getDatabaseService().getNode(tempEditNode.getNodeID()));
        // set edges globally
        ApplicationState.getApplicationState().setEdgesToEdit(newEdges);
        // fire confirmation
        Parent root = FXMLLoader.load(ResourceLoader.saveNodeConfirm);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Save Confirmation");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(image_pane.getScene().getWindow());
        stage.showAndWait();
    }

    void updateNode() {
        tempEditNode.setXcoord((int)selectedCircle.getCenterX());
        tempEditNode.setYcoord((int)selectedCircle.getCenterY());
        tempEditNode.setNodeType(type_field.getText());
        tempEditNode.setBuilding(building_field.getText());
        tempEditNode.setLongName(long_field.getText());
        tempEditNode.setShortName(short_field.getText());
        tempEditNode.setClosed(closedToggle.isSelected());
    }

    @FXML
    void cancelAction(ActionEvent e) throws Exception {
        Stage stage = (Stage) map_scrollpane.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home");
        stage.setMaximized(true);
    }

    /**
     * zooms in the map
     * @param event
     */
    @FXML
    void zoomIn(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() + 0.05);
        zoom_slider.setValue(zoom_slider.getValue());
    }

    /**
     * zooms out the map
     * @param event
     */
    @FXML
    void zoomOut(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() - 0.05);
        zoom_slider.setValue(zoom_slider.getValue());
    }

    /**
     * scales zoom grouping based on given value
     * @param scaleValue
     */
    private void zoom(double scaleValue) {
    //    System.out.println("airportapp.Controller.zoom, scaleValue: " + scaleValue);
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    void drawFloorNodes(String floor){
        ArrayList<Node> floorNodes = DatabaseService.getDatabaseService().getNodesByFloor(floor);
        zoomGroup.getChildren().removeAll(circleCollection);
        circleCollection.clear();
        for (Node node : floorNodes) {
            if (!node.equals(tempEditNode)) {
                Circle circle = new Circle();
                circle.setCenterX(node.getXcoord());
                circle.setCenterY(node.getYcoord());
                if (edgeNodeCollection.contains(node)) {
                    circle.setFill(Color.BLUE);
                } else {
                    circle.setFill(Color.BLACK);
                }
                circle.setRadius(20);
                circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (circle.getFill().equals(Color.BLUE)) {
                            edgeNodeCollection.remove(node);
                            circle.setFill(Color.BLACK);
                            edges_list.getItems().remove(node.getNodeID());
                        } else {
                            edgeNodeCollection.add(node);
                            circle.setFill(Color.BLUE);
                            edges_list.getItems().add(node.getNodeID());
                        }
                    }
                });
                circleCollection.add(circle);
            }
        }
        zoomGroup.getChildren().addAll(circleCollection);
    }


    @FXML
    void floorChangeAction(ActionEvent e) throws IOException {
        JFXButton clickedBtn = (JFXButton) e.getSource();
        switch (clickedBtn.getText()) {
            case "Floor 3":
                setMapFloor("3");
                System.out.println(circleCollection);
                drawFloorNodes("3");
                break;
            case "Floor 2":
                setMapFloor("2");
                drawFloorNodes("2");
                break;
            case "Floor 1":
                setMapFloor("1");
                drawFloorNodes("1");
                break;
            case "L1":
                setMapFloor("L1");
                drawFloorNodes("L1");
                break;
            case "L2":
                setMapFloor("L2");
                drawFloorNodes("L2");
                break;
            case "Ground":
                setMapFloor("G");
                drawFloorNodes("G");
                break;
            default:
                System.out.println("WHAT BUTTON WAS PRESSED?????");
                break;
        }
    }


}
