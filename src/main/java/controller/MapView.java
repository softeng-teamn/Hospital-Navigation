package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import model.*;
import service.PathFindingService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MapView {

    private EventBus eventBus = EventBusFactory.getEventBus();
    private Event event = EventBusFactory.getEvent();

    private Group zoomGroup;
    private Circle startCircle;
    private Circle selectCircle;
    private ArrayList<Line> lineCollection;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;

    @FXML
    private JFXListView directionsView;

    @FXML
    private JFXButton showDirectionsBtn;

    @FXML
    private VBox showDirVbox;

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

        // Setting Up Circle Destination Point
        startCircle.setCenterX(event.getDefaultNode().getXcoord());
        startCircle.setCenterY(event.getDefaultNode().getYcoord());
        startCircle.setRadius(20);
        startCircle.setFill(Color.rgb(67, 70, 76));
        zoomGroup.getChildren().add(startCircle);


        // Setting View Scrolling
        zoom_slider.setMin(0.4);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.4);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.4);
        zoom(0.3);

        directionsView.setVisible(false);
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
                    default:
                        break;
                }
            }
        });
        this.event = event;
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
            System.out.println(makeDirections(path));
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

    // todo: watch out for multiple floors? are those separate nodes -> problem w directions
    // TODO: comments

    private String makeDirections(ArrayList<Node> path) {
        final int NORTH_I = 1122 - 1886;
        final int NORTH_J = 642 - 1501;

        double oldX, oldY;
        ArrayList<String> directions = new ArrayList<>();
        directions.add("\nStart at " + path.get(0).getLongName() + ".\n");

        String cardinal = csDirPrint(path.get(0).getXcoord() + NORTH_I, path.get(0).getYcoord() + NORTH_J, path.get(0).getXcoord(), path.get(0).getYcoord(), path.get(1).getXcoord(), path.get(1).getYcoord());
        cardinal = convertToCardinal(cardinal);
        directions.add(cardinal);

        boolean afterFloorChange = false;
        for (int i = 0; i < path.size() - 2; i++) {
            if (afterFloorChange) {
                String afterEl = csDirPrint(path.get(i+1).getXcoord() + NORTH_I, path.get(i+1).getYcoord() + NORTH_J, path.get(i+1).getXcoord(), path.get(i+1).getYcoord(), path.get(i+2).getXcoord(), path.get(i+2).getYcoord());
                directions.add(convertToCardinal(afterEl));
                afterFloorChange = false;
            }
            else if (!path.get(i).getFloor().equals(path.get(i+1).getFloor())) {
                System.out.println(path.get(i) + " " + path.get(i+1));
                if (path.get(i).getNodeType().equals("ELEV")) {
                    directions.add("Take the elevator from floor " + path.get(i).getFloor() + " to floor " + path.get(i+1).getFloor() + "\n");
                }
                else {
                    directions.add("Take the stairs from floor " + path.get(i).getFloor() + " to floor " + path.get(i+1).getFloor() + "\n");
                }
                afterFloorChange = true;
            }
            else if(path.get(i+1).getNodeType().equals("ELEV")) {
                directions.add("Walk to the elevator.\n");
            }
            else if (path.get(i+1).getNodeType().equals("STAI")) {
                directions.add("Walk to the stairs.\n");
            }
            else {
                directions.add(csDirPrint(path.get(i).getXcoord(), path.get(i).getYcoord(), path.get(i + 1).getXcoord(), path.get(i + 1).getYcoord(), path.get(i + 2).getXcoord(), path.get(i + 2).getYcoord()) + "\n");
            }
        }

        directions.add("You have arrived at " + path.get(path.size() - 1).getLongName() + ".");

        for (int i = 1; i < directions.size(); i++) {
            String currDir = directions.get(i);
            String oldDir = directions.get(i-1);
            if (currDir.contains("straight")) {
                int feetIndex = oldDir.indexOf("for");
                if (feetIndex < 0) {
                    feetIndex = oldDir.indexOf("walk") + 5;
                }
                else {
                    feetIndex += 4;
                }
                int oldDist = Integer.parseInt(oldDir.substring(feetIndex, oldDir.indexOf("feet")-1));
                int currDist = Integer.parseInt(currDir.substring(currDir.indexOf("walk") + 5, currDir.indexOf("feet")-1));
                int totalDist = oldDist + currDist;

                String newDir = "";
                if (oldDir.contains("for")) {
                    newDir = oldDir.substring(0, oldDir.indexOf("for") + 4) + totalDist + " feet\n";
                }
                else {
                    newDir = oldDir.substring(0, oldDir.indexOf("walk") + 5) + totalDist + " feet\n";
                }
                directions.remove(i);
                directions.remove(i-1);
                directions.add(i-1, newDir);
                i--;
            }
        }

        ObservableList<Label> dirs = FXCollections.observableArrayList();
        ArrayList<Label> labels = new ArrayList<>();
        for (int i = 0; i < directions.size(); i++) {
            Label l = new Label(directions.get(i));
            l.setWrapText(true);
            l.setTextFill(Color.WHITE);
            labels.add(l);
        }
        dirs.addAll(labels);
        directionsView.setItems(dirs);

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < directions.size(); ++i) {
            buf.append(directions.get(i));
        }
        String total = buf.toString();

        return total;
    }

    private String convertToCardinal(String cardinal) {
        String feet = cardinal.substring(cardinal.indexOf("walk")+5)+ "\n";
        if (cardinal.contains("slightly left")) {
            cardinal = "Walk south east for " + feet;
        }
        else if (cardinal.contains("slightly right")) {
            cardinal = "Walk south west for " + feet;
        }
        else if (cardinal.contains("sharply left")) {
            cardinal = "Walk north east for " + feet;
        }
        else if (cardinal.contains("sharply right")) {
            cardinal = "Walk north west for " + feet;
        }
        else if (cardinal.contains("left")) {
            cardinal = "Walk east for " + feet;
        }
        else if (cardinal.contains("right")) {
            cardinal = "Walk west for " + feet;
        }
        else if (cardinal.contains("straight")) {
            cardinal = "Walk south for " + feet;
        }
        else {
            cardinal = "Walk north for " + feet;
        }
        return cardinal;
    }

    private String csDirPrint(double pX, double pY, double cX, double cY, double nX, double nY) {
        final double THRESHOLD = .0001;

        double prevXd, prevYd, currXd, currYd, nextXd, nextYd;
        prevXd = pX;
        prevYd = pY;
        currXd = cX;
        currYd = cY;
        nextXd = nX;
        nextYd = nY;

        double slope1, slope2, intercept;
        slope1 = (currYd - prevYd) / (currXd - prevXd);
        slope2 = (nextYd - currYd) / (nextXd - currXd);
        intercept = nextYd - slope2 * nextXd;

        double oldI, oldJ, newI, newJ, lengthOld, lengthNew;
        oldI = currXd - prevXd;
        oldJ = currYd - prevYd;
        newI = nextXd - currXd;
        newJ = nextYd - currYd;
        lengthOld = Math.sqrt(oldI*oldI + oldJ*oldJ);
        lengthNew = Math.sqrt(newI*newI + newJ * newJ);

        double distance = lengthNew /260 * 85;

        double uDotV = oldI * newI + oldJ * newJ;
        double theta, alpha, plus, minus;
        theta = Math.acos(uDotV/(lengthNew*lengthOld));
        alpha = Math.atan(slope1);
        plus = theta + alpha;
        minus = alpha - theta;

        double computedY1 = currYd + Math.tan(plus);

        double expectedVal = (currXd + 1) * slope2 + intercept;

        if (Math.abs(newI) < THRESHOLD) {
            if ((nextYd > currYd && prevXd < currXd) || (nextYd < currYd && prevXd > currXd)) {
                expectedVal = 1;
                computedY1 = 1;
            }
            else {
                expectedVal = 1;
                computedY1 = 0;
            }
        }

        if (Math.abs(oldI) < THRESHOLD && Math.abs(newJ) < THRESHOLD) {
            if ((currYd > prevYd && nextXd > currXd) || (currYd < prevYd && currXd > nextXd)) {
                expectedVal = 1;
                computedY1 = 0;
            }
            else {
                expectedVal = 1;
                computedY1 = 1;
            }
        }

        String turn = "";

        if (Math.abs(plus - minus) < Math.PI/8) {
            turn = "straight";
        }
        else if (Math.abs(theta - Math.PI) < THRESHOLD) {
            turn = "around";
        }
        else if (Math.abs(expectedVal - computedY1) < THRESHOLD) {
            if (theta < Math.PI/4) {
                turn = "slightly right";
            }
            else if (theta > Math.PI*3/4) {
                turn = "sharply right";
            }
            else {
                turn = "right";
            }
        }
        else {
            if (theta < Math.PI/4) {
                turn = "slightly left";
            }
            else if (theta > Math.PI*3/4) {
                turn = "sharply left";
            }
            else {
                turn = "left";
            }
        }


        String direction = String.format("Turn " + turn + " and walk %.0f feet.", distance);
        return direction;
    }

    @FXML
    private void showDirections() {
        directionsView.setVisible(!directionsView.isVisible());
        if (showDirectionsBtn.getText().contains("Show")) {
            showDirectionsBtn.setText("Close Textual Directions");
            directionsView.toFront();
        }
        else {
            showDirectionsBtn.setText("Show Textual Directions");
            showDirVbox.toFront();
        }
        if (showDirVbox.getAlignment().equals(Pos.BOTTOM_RIGHT)) {
            showDirVbox.setAlignment(Pos.TOP_RIGHT);
        }
        else {
            showDirVbox.setAlignment(Pos.BOTTOM_RIGHT);
        }
    }
}
