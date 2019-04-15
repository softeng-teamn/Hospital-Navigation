package map.edit_node;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
import net.kurobako.gesturefx.GesturePane;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditNodeController extends Control {

    Group zoomGroup;
    Circle selectedCircle = new Circle();
    Node tempEditNode;      // mutating the node based on edits
    boolean isEditEdges = false;
    ArrayList<Circle> circleCollection = new ArrayList<>();
    ArrayList<Node> edgeNodeCollection;
    static ArrayList<Edge> oldEdgesFromEditNode;
    double orgSceneX, orgSceneY;    // for circle dragging
    // Scroll & Zoom
    private static HashMap<String, ImageView> imageCache;
    private ImageView floorImg;
    private static final double MIN_ZOOM = 0.4;
    private static final double MAX_ZOOM = 1.2;

    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private JFXTextField building_field, type_field, short_field, long_field;
    @FXML
    private JFXComboBox<String> floor_combo;
    @FXML
    private GesturePane gPane;
    @FXML
    private HBox floor_change_hbox;
    @FXML
    private VBox floor_change_vbox, edges_container;
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
    void initialize() throws InterruptedException {

        zoomSliderInit();
        zoomGroupInit();
        imagesInit();

        tempEditNode = ApplicationState.getApplicationState().getNodeToEdit();

        node_id_label.setText("Node: " + tempEditNode.getNodeID());


        fillEdges(tempEditNode);

        // set image
        setFloor(tempEditNode.getFloor());


        fillNodeInfo();

        hideEdges();

        drawSelectedCircle(tempEditNode.getXcoord(), tempEditNode.getYcoord());

    }

    void zoomGroupInit() {
        zoomGroup = new Group();
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
        floor_change_hbox.getChildren().remove(floor_change_vbox);
        edges_container.getChildren().remove(edges_list);
        zoomGroup.getChildren().removeAll(circleCollection);
        circleCollection.clear();
    }

    void showEdges() {
        edit_show_btn.setGraphic(edit_icon_up);
        floor_change_hbox.getChildren().add(0,floor_change_vbox);
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
                tempEditNode.setXcoord((int)circle.getCenterX());
                tempEditNode.setYcoord((int)circle.getCenterY());
            }
        });
        System.out.println("Selected Circle -> x:" + tempEditNode.getXcoord() + " y:" + tempEditNode.getYcoord());
        selectedCircle = circle;
        zoomGroup.getChildren().add(circle);
    }



    private void scrollTo(Node node) {
        System.out.println("Position Scrolling x:" + node.getXcoord() + " y:" + node.getYcoord());
        // animation scroll to new position
        gPane.animate(Duration.millis(200))
                .interpolateWith(Interpolator.EASE_BOTH)
                .beforeStart(() -> System.out.println("Starting..."))
                .afterFinished(() -> System.out.println("Done!"))
                .centreOn(new Point2D(node.getXcoord(), node.getYcoord()));
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

//    @FXML
//    void mapClickedHandler(MouseEvent e) {
//        if (!isEditEdges) {
//            double mouseX = e.getX();
//            double mouseY = e.getY();
//            zoomGroup.getChildren().remove(selectedCircle);
//            drawSelectedCircle(mouseX, mouseY);
//        }
//    }

    @FXML
    void comboAction(ActionEvent e) throws IOException {
        String newFloor = floor_combo.getSelectionModel().getSelectedItem();
        tempEditNode.setFloor(newFloor);
        // hide any nodes on screen
        hideEdges();
        setFloor(newFloor);
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
        stage.initOwner(gPane.getScene().getWindow());
        stage.showAndWait();

        if (ApplicationState.getApplicationState().getNodeToEdit() == null) {
            try {
                Parent myRoot = FXMLLoader.load(ResourceLoader.home);
                Stage myStage = (Stage)gPane.getScene().getWindow();
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
        stage.initOwner(gPane.getScene().getWindow());
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
        Stage stage = (Stage) gPane.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home");
        stage.setMaximized(true);
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
        setFloor(clickedBtn.getText());
    }


}
