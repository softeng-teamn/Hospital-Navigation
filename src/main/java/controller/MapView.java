package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import service.DatabaseService;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static controller.Controller.nodeToEdit;

public class MapView {

    private EventBus eventBus = EventBusFactory.getEventBus();
    private Event event = EventBusFactory.getEvent();

    private Group zoomGroup;
    private Circle startCircle;
    private Circle selectCircle;
    private ArrayList<Line> lineCollection;
    private ArrayList<Circle> circleCollection;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private JFXButton f1_btn, f2_btn, f3_btn, l1_btn, l2_btn, ground_btn;
    @FXML
    private Pane image_pane;

    // ELEVATOR CALL BUTTONS
    @FXML
    void callElevatorAction(ActionEvent e) {

    }

    @FXML
    void initialize() {
        // listen to changes
        eventBus.register(this);

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);

        // Setup collection of lines
        lineCollection = new ArrayList<Line>();

        // Set start circle
        startCircle = new Circle();

        // Initialize Circle Collection
        circleCollection = new ArrayList<Circle>();

        // Setting Up Circle Destination Point
        startCircle.setCenterX(event.getDefaultNode().getXcoord());
        startCircle.setCenterY(event.getDefaultNode().getYcoord());
        startCircle.setRadius(20);
        startCircle.setFill(Color.rgb(67, 70, 76));
        zoomGroup.getChildren().add(startCircle);


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
        event.setEventName("floor");
        String floorName = "";
        event.setFloor(btn.getText());
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
        event.setFloor(floorName);
        eventBus.post(event);
        // Handle Floor changes
        editNodeHandler(event.isEditing());
    }

    @Subscribe
    void eventListener(Event event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switch (event.getEventName()) {
                    case "navigation":
                        navigationHandler();
                        break;
                    case "node-select":
                        drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72,87,125));
                        break;
                    case "editing":
                        editNodeHandler(event.isEditing());
                    default:
                        break;
                }
            }
        });
        this.event = event;
    }

    void editNodeHandler(boolean isEditing) {
        if (isEditing) {
            // remove previous selected circle
            if (zoomGroup.getChildren().contains(selectCircle)) {
                zoomGroup.getChildren().remove(selectCircle);
            }
            // remove old circles
            zoomGroup.getChildren().removeAll(circleCollection);
            circleCollection.clear();
            // load all nodes for the floor
            ArrayList<Node> nodeByFlooor = DatabaseService.getDatabaseService().getNodesByFloor(event.getFloor());
            for (Node n : nodeByFlooor) {
                Circle nodeCircle = new Circle();
                nodeCircle.setCenterX(n.getXcoord());
                nodeCircle.setCenterY(n.getYcoord());
                nodeCircle.setRadius(20);
                nodeCircle.setFill(Color.GREEN);
                Tooltip tp = new Tooltip("ID: " + n.getNodeID() + ", Short Name: " + n.getShortName());
                nodeCircle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Stage stage = (Stage) image_pane.getScene().getWindow();
                        Circle c = (Circle)event.getSource();
                        tp.show(c, stage.getX()+event.getSceneX()+15, stage.getY()+event.getSceneY());
                        image_pane.getScene().setCursor(Cursor.HAND);
                    }
                });
                nodeCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        tp.hide();
                        image_pane.getScene().setCursor(Cursor.DEFAULT);
                    }
                });
                nodeCircle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        nodeToEdit = n;
                        System.out.println("WE CLICKED THE CIRCLE");
                        try {
                            Stage stage = (Stage) image_pane.getScene().getWindow();
                            Parent root = FXMLLoader.load(ResourceLoader.editNode);
                            StageManager.changeExistingWindow(stage, root, "Node Editor");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                circleCollection.add(nodeCircle);
            }
            // Show on screen
            zoomGroup.getChildren().addAll(circleCollection);
        } else {
            zoomGroup.getChildren().removeAll(circleCollection);
            circleCollection.clear();
        }
    }


    private void drawPoint(Node node, Circle circle, Color color) {
        // remove points
        for (Line line : lineCollection) {
            if (zoomGroup.getChildren().contains(line)) {
                zoomGroup.getChildren().remove(line);
            }
        }
        // remove old selected Circle
        if (zoomGroup.getChildren().contains(selectCircle)) {
            System.out.println("we found new Selected Circles");
            zoomGroup.getChildren().remove(selectCircle);
        }
        // create new Circle
        circle = new Circle();
        circle.setCenterX(node.getXcoord());
        circle.setCenterY(node.getYcoord());
        circle.setRadius(20);
        circle.setFill(color);
        zoomGroup.getChildren().add(circle);
        // set circle to selected
        selectCircle = circle;
        // Scroll to new point
        scrollTo(event.getNodeSelected());

    }

    // generate path on the screen
    private void navigationHandler() {
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> path;
        MapNode start = new MapNode(event.getNodeStart().getXcoord(), event.getNodeStart().getYcoord(), event.getNodeStart());
        MapNode dest = new MapNode(event.getNodeSelected().getXcoord(), event.getNodeSelected().getYcoord(), event.getNodeSelected());
        // check if the path need to be 'accessible'
        if (event.isAccessiblePath()) {
            // do something special
            path = pathFinder.genPath(start, dest, true);
        } else {
            // not accessible
            path = pathFinder.genPath(start, dest, false);
        }

        drawPath(path);

    }

    // draw path on the screen
    private void drawPath(ArrayList<Node> path) {
        // remove points
        for (Line line : lineCollection) {
            if (zoomGroup.getChildren().contains(line)) {
                zoomGroup.getChildren().remove(line);
            }
        }
        if (path != null && path.size() > 1) {
            Node last = path.get(0);
            Node current;
            for (int i = 1; i < path.size(); i++) {
                current = path.get(i);
                Line line = new Line();

                line.setStartX(current.getXcoord());
                line.setStartY(current.getYcoord());

                line.setEndX(last.getXcoord());
                line.setEndY(last.getYcoord());

                line.setFill(Color.BLACK);
                line.setStrokeWidth(10.0);
                zoomGroup.getChildren().add(line);
                lineCollection.add(line);
                last = current;
            }
        }

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
