package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Node;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.time.LocalDate;


public class ReservationController {

    Group zoomGroup;

    // State
    String searchQuery = "";
    Node selectedRoom;
    LocalDate startDate;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private Pane image_pane;
    @FXML
    private JFXTextField search_bar, employee_id, event_name;
    @FXML
    private JFXDatePicker start_date_picker;
    @FXML
    private JFXButton save_btn;

    @FXML
    void initialize() {

        // validate & disabale save button
        validateInfo();

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

    // Click on Home Button
    @FXML
    void showHome(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(ResourceLoader.home);
        Stage stage = (Stage) map_scrollpane.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Home");
    }

    // clicking on a list item
    @FXML
    void listViewClicked(MouseEvent e) {

    }

    // pressing enter on the search bar
    @FXML
    void searchBarEnter(ActionEvent e) {
        searchQuery = search_bar.getText();
    }

    // clicking to switch floors
    @FXML
    void changeFloor(ActionEvent e) throws IOException {
        JFXButton btn = (JFXButton)e.getSource();
        ImageView imageView;
        String floorName = "";
        switch (btn.getText()) {
            case "Floor 3":
                imageView = new ImageView(new Image(
                        ResourceLoader.thirdFloor.openStream()));
                floorName = "3";
                break;
            case "Floor 2":
                imageView = new ImageView(new Image(
                        ResourceLoader.secondFloor.openStream()));
                floorName = "2";
                break;
            case "Floor 1":
                imageView = new ImageView(new Image(
                        ResourceLoader.firstFloor.openStream()));
                floorName = "1";
                break;
            case "L1":
                imageView = new ImageView(new Image(
                        ResourceLoader.firstLowerFloor.openStream()));
                floorName = "L1";
                break;
            case "L2":
                imageView = new ImageView(new Image(
                        ResourceLoader.secondLowerFloor.openStream()));
                floorName = "L2";
                break;
            case "Ground":
                imageView = new ImageView(new Image(
                        ResourceLoader.groundFloor.openStream()));
                floorName = "G";
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

    void validateInfo() {
        if (event_name.getText() == "" || employee_id.getText() == "" || selectedRoom == null) {
            // can't save event
            save_btn.setDisable(true);
        } else {
            // allow click
            save_btn.setDisable(true);
        }
    }

    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
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
}
