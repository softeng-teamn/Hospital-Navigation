package controller;

import com.jfoenix.controls.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
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
import model.Node;
import model.ReservableSpace;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class ReservationController {

    static final Color AVAILIBLE_COLOR = Color.rgb(87,255,132,0.5);
    static final Color UNAVAILABLE_COLOR = Color.rgb(255,82,59, 0.5);
    static final Color AVAILIBLE_COLOR_SELECT = Color.rgb(87,255,132,1);
    static final Color UNAVAILABLE_COLOR_SELECT = Color.rgb(255,82,59, 1);
    Group zoomGroup;
    ArrayList<ReservableSpace> resCollection;
    ArrayList<Node> nodeCollection;
    ArrayList<Circle> circleCollection = new ArrayList<Circle>();;
    ArrayList<Node> availibleNodeCollection = new ArrayList<Node>();
    Circle lastSelectedCircle;

    // State
    Node selectedRoom;


    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private JFXTextField employee_id, event_name;
    @FXML
    private JFXDatePicker date_picker;
    @FXML
    private JFXTimePicker start_time, end_time;
    @FXML
    private JFXButton save_btn;

    @FXML
    void initialize() {

        // setup onChange handlers to elements
        setOnChangeHandlers();

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

        // populate reservableSpaces
        initReservables();

        // validate & disabale save button
        validateInfo();
    }

    // Click on Home Button
    @FXML
    void showHome(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(ResourceLoader.home);
        Stage stage = (Stage) map_scrollpane.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Home");
    }


    @FXML
    void mapClickedHandler(MouseEvent e) {
        System.out.println("x: " + e.getX() + ", y: " + e.getY());
    }

    // Atatch event listeners to every field
    void setOnChangeHandlers() {
        start_time.setValue(LocalTime.now());
        start_time.setDefaultColor(Color.valueOf("#065185"));
        // event name
        event_name.textProperty().addListener((o, oldVal, newVal) -> handleChange());
        // employee id
        employee_id.textProperty().addListener((o, oldVal, newVal) -> handleChange());
        // date
        date_picker.setValue(LocalDate.now());
        date_picker.valueProperty().addListener((o, oldVal, newVal) -> handleChange());
        // start time
        start_time.valueProperty().addListener((o, oldVal, newVal) -> handleChange());
        // end time
        end_time.valueProperty().addListener((o, oldVal, newVal) -> handleChange());
    }

    void handleChange() {
        validateInfo();
    }

    // make sure every field is CORRECT
    void validateInfo() {
        System.out.println("validating");
        createCircles();
        if (!event_name.getText().equals("") && !employee_id.getText().equals("") &&
                date_picker.getValue().compareTo(LocalDate.now()) >= 0 && start_time.getValue().compareTo(end_time.getValue()) < 0 &&
                selectedRoom != null) {
            // Validated
            save_btn.setDisable(false);
        } else {
            // CANT SAVE
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


    void initReservables() {
        // Retrieves all the reservable spaces in the DB
        resCollection = (ArrayList<ReservableSpace>) DatabaseService.getDatabaseService().getAllReservableSpaces();
        // Finds all nodes that represent the location of that space
        nodeCollection = new ArrayList<Node>();
        for(ReservableSpace rs : resCollection) {
            nodeCollection.add(DatabaseService.getDatabaseService().getNode(rs.getLocationNodeID()));
        }
        createCircles();
    }

    void createCircles() {
        // find availible spaced
        ArrayList<ReservableSpace> availableSpaces = new ArrayList<>();
        if (start_time.getValue() != null && end_time.getValue() != null && date_picker.getValue() != null)  {
            LocalDate localDate = date_picker.getValue();
            LocalTime localStartTime = start_time.getValue();
            GregorianCalendar calStart = new GregorianCalendar(
                    localDate.getYear(),
                    localDate.getMonthValue(),
                    localDate.getDayOfMonth(),
                    localStartTime.getHour(),
                    localStartTime.getMinute()
            );
            LocalTime localEndTime = end_time.getValue();
            GregorianCalendar calEnd = new GregorianCalendar(
                    localDate.getYear(),
                    localDate.getMonthValue(),
                    localDate.getDayOfMonth(),
                    localEndTime.getHour(),
                    localEndTime.getMinute()
            );
            if (localStartTime.compareTo(localEndTime) < 0) {
                System.out.println("WE ARE IN THE LOOP!!!");
                System.out.println("FILTERING AVAILIBLE SPACES");

                availableSpaces = (ArrayList<ReservableSpace>) DatabaseService.getDatabaseService().getAvailableReservableSpacesBetween(calStart, calEnd);
                if (availableSpaces != null) {
                    for(ReservableSpace rs : availableSpaces) {
                        availibleNodeCollection.add(DatabaseService.getDatabaseService().getNode(rs.getLocationNodeID()));
                    }
                } else {
                    availibleNodeCollection = new ArrayList<Node>();
                }
            } else {
                availibleNodeCollection = new ArrayList<Node>();
            }
        }

        System.out.println(availibleNodeCollection);
        // remove last circle collection
        zoomGroup.getChildren().removeAll(circleCollection);
        circleCollection = new ArrayList<Circle>();
        for(Node node : nodeCollection) {
            Circle circle = new Circle();
            circle.setRadius(80);
            if (availibleNodeCollection.contains(node)) {
                circle.setFill(AVAILIBLE_COLOR);
            } else {
                circle.setFill(UNAVAILABLE_COLOR);
            }
            circle.setCenterX(node.getXcoord());
            circle.setCenterY(node.getYcoord());
            circle.setOnMouseClicked(e -> handleCircleClicked(e, node));
            circleCollection.add(circle);
        }
        // show circles
        renderCircles();
    }

    void handleCircleClicked(MouseEvent e, Node node) {
        Circle circle = (Circle)e.getSource();
        if (circle.getFill().equals(AVAILIBLE_COLOR)) {
            // select this room
            selectedRoom = node;
            // set last selected circle to be new color
            unselectLastCircleSelect();
            // change color to selected
            circle.setFill(AVAILIBLE_COLOR_SELECT);
            // this is now the new lastSelectedCircle
            lastSelectedCircle = circle;
        } else if (circle.getFill().equals(AVAILIBLE_COLOR_SELECT)){
            // remove room selection
            selectedRoom = null;
            lastSelectedCircle = null;
            // set circle back to default
            circle.setFill(AVAILIBLE_COLOR);
        } else if (circle.getFill().equals(UNAVAILABLE_COLOR)) {
            // room can't be selected
            selectedRoom = null;
            // set last selected circle to be new color
            unselectLastCircleSelect();
            // change color to selected
            circle.setFill(UNAVAILABLE_COLOR_SELECT);
            // this is now the new lastSelectedCircle
            lastSelectedCircle = circle;
        } else if (circle.getFill().equals(UNAVAILABLE_COLOR_SELECT)) {
            // remove room selection
            selectedRoom = null;
            lastSelectedCircle = null;
            // set circle back to default
            circle.setFill(UNAVAILABLE_COLOR);
        }
        updateTimeView();
    }

    void unselectLastCircleSelect() {
        int lastIndx = circleCollection.indexOf(lastSelectedCircle);
        // found the selected circle (it could be null, which means there is no other selected circle)
        if (lastIndx >= 0) {
            Circle selCirc = circleCollection.get(lastIndx);
            if (selCirc.getFill().equals(AVAILIBLE_COLOR_SELECT)) {
                selCirc.setFill(AVAILIBLE_COLOR);
            } else if (selCirc.getFill().equals(UNAVAILABLE_COLOR_SELECT)) {
                selCirc.setFill(UNAVAILABLE_COLOR);
            }
            circleCollection.remove(lastIndx);
            circleCollection.add(lastIndx, selCirc);
        }
    }

    void renderCircles() {
        zoomGroup.getChildren().removeAll(circleCollection);
        zoomGroup.getChildren().addAll(circleCollection);
    }

    void updateTimeView() {
        if (selectedRoom == null) {
            // remove all times
        } else {
            // show times
//            LocalTime sTime = start_time.getValue();
//            LocalTime eTime = end_time.getValue();
            LocalDate date = date_picker.getValue();

            if (date == null) {
                // I don't know when to actually look for the reservations
                return;
            }
//            DatabaseService.getDatabaseService().getReservableSpaceWithNodeId()
//            ReservableSpace selectedSpace;
//            GregorianCalendar calStart = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
//            LocalDate nextDay = date.plus(1, ChronoUnit.DAYS);
//            GregorianCalendar calEnd = GregorianCalendar.from(nextDay.atStartOfDay(ZoneId.systemDefault()));
//            ArrayList<ReservableSpace> bookedTimes = DatabaseService.getDatabaseService().getReservationsBySpaceIdBetween(
//                    selectedSpace.getSpaceID(),
//                    calStart,
//                    calEnd
//            );
        }
    }
}
