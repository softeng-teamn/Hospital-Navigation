package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

public class CreateNodeController {

    @FXML
    JFXButton cancel_btn;
    @FXML
    ScrollPane map_scrollpane;
    @FXML
    Slider zoom_slider;

    // global group for all map entities (to be scaled on zoom)
    Group zoomGroup;

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
    void cancelAction(ActionEvent e) throws Exception {
        showHome();
    }

    // handle zoom map functionality
    private void zoom(double scaleValue) {
//    System.out.println("airportapp.Controller.zoom, scaleValue: " + scaleValue);
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
