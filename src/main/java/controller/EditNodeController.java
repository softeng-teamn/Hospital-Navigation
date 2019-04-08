package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

public class EditNodeController {

    Group zoomGroup;

    @FXML
    private Pane img_pane;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;

    @FXML
    void initialize() {

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
    }

    @FXML
    void changeFloor(ActionEvent e) throws IOException {
        JFXButton btn = (JFXButton)e.getSource();
        ImageView imageView;
        switch (btn.getText()) {
            case "Floor 3":
                imageView = new ImageView(new Image(
                        ResourceLoader.thirdFloor.openStream()));
                break;
            case "Floor 2":
                imageView = new ImageView(new Image(
                        ResourceLoader.secondFloor.openStream()));
                break;
            case "Floor 1":
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
            case "Ground":
                imageView = new ImageView(new Image(
                        ResourceLoader.groundFloor.openStream()));
                break;
            default:
                System.out.println("We should not have default here!!!");
                imageView = new ImageView(new Image(
                        ResourceLoader.groundFloor.openStream()));
                break;
        }
        img_pane.getChildren().clear();
        img_pane.getChildren().add(imageView);
    }

    @FXML
    void cancelAction(ActionEvent e) throws Exception {
        Stage stage = (Stage) img_pane.getScene().getWindow();
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
