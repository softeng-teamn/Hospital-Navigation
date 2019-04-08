package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.EventBusFactory;
import model.Node;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

import static controller.Controller.nodeToEdit;

public class EditNodeController extends Control {

    Group zoomGroup;
    private EventBus eventBus = EventBusFactory.getEventBus();
    Circle selectedCircle = new Circle();
    Node tempEditNode;

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
    void initialize() {

        tempEditNode = nodeToEdit;

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
        zoom_slider.setValue(0.3);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.6);
        drawSelectedCircle(tempEditNode.getXcoord(), tempEditNode.getYcoord());
    }

    void drawSelectedCircle(double x, double y) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(20);
        circle.setFill(Color.GREEN);
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
        Node node = nodeToEdit;
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
    }

    @FXML
    void mapClickedHandler(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        zoomGroup.getChildren().remove(selectedCircle);
        drawSelectedCircle(mouseX, mouseY);
    }

    private void setMapFloor(String floor) throws IOException {
        ImageView imageView;
        switch (tempEditNode.getFloor()) {
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
        setMapFloor(newFloor);
        scrollTo(tempEditNode);
        System.out.println("Selecting NEW FLOOR RENDER: " + newFloor);
    }

    @FXML
    void deleteAction(ActionEvent e) {

    }

    @FXML
    void saveAction(ActionEvent e) {

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



}
