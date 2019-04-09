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
import javafx.concurrent.Task;
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
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static controller.Controller.elevatorCon;
import static controller.Controller.floorIsAt;
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
    private JFXButton call_el1_btn, call_el2_btn, call_el3_btn, call_el4_btn;
    @FXML
    private Label cur_el_floor;

    @FXML
    private JFXListView directionsView;

    @FXML
    private JFXButton showDirectionsBtn;

    @FXML
    private VBox showDirVbox;

    private
    HashMap<String, Integer> floors = new HashMap<>();

    // ELEVATOR CALL BUTTONS
    @FXML
    void callElevatorAction(ActionEvent e) {



        JFXButton myBtn = (JFXButton) e.getSource();
        char elevNum = myBtn.getText().charAt(myBtn.getText().length()-1);

        int floor = Integer.parseInt("" + elevNum);

        GregorianCalendar cal = new GregorianCalendar();
        try {
            elevatorCon.postFloor("S", floor, cal);
        }catch (IOException ioe){
            System.out.println("IO Exception");
        }

    }

    @FXML
    void initialize() {

        pingTiming();

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
        floors.put("L2", -2);
        floors.put("L1", -1);
        floors.put("G", 0);
        floors.put("1", 1);
        floors.put("2", 2);
        floors.put("3", 3);
    }

    void pingTiming() {

        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                while (true) {
                    Thread.sleep(1000);
                    System.out.println("shit was fired");
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Elevator At: " + elevatorCon.getFloor("S"));
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            try {
                                System.out.println("Showing at: " + elevatorCon.getFloor("S"));
                                cur_el_floor.setText(elevatorCon.getFloor("S"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };

        new Thread(task).start();
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
            makeDirections(path);
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

    /**
     * Create textual instructions for the given path.
     * @param path the list of nodes in the path
     * @return a String of the directions
     */
    public ArrayList<String> makeDirections(ArrayList<Node> path) {

        if (path == null || path.size() < 2) {
            return null;
        }

        System.out.println(path);    // TODO: remove

        final int NORTH_I = 1122 - 1886;    // Measurements from maps
        final int NORTH_J = 642 - 1501;    // Measurements from maps

        ArrayList<String> directions = new ArrayList<>();    // Collection of instructions
        directions.add("\nStart at " + path.get(0).getLongName() + ".\n");    // First instruction

        // Make the next instruction cardinal, or up/down if it is a floor connector
        String oldFloor = path.get(0).getFloor();
        String newFloor = path.get(1).getFloor();
        if (floors.get(oldFloor) != floors.get(newFloor)) {
            directions.add(upDownConverter(oldFloor, newFloor, path.get(0).getNodeType()));
        }
        else {
            directions.add(convertToCardinal(csDirPrint(path.get(0).getXcoord() + NORTH_I, path.get(0).getYcoord() + NORTH_J, path.get(0), path.get(1))));
        }

        boolean afterFloorChange = false;    // If we've just changed floors, give a cardinal direction
        for (int i = 0; i < path.size() - 2; i++) {    // For each node in the path, make a direction
            String oldFl = (path.get(i+1).getFloor());
            String newFl = (path.get(i+2).getFloor());
            if (afterFloorChange && !path.get(i + 1).getNodeType().equals("ELEV") && !path.get(i + 1).getNodeType().equals("STAI")) {
                afterFloorChange = false;// TODO
                System.out.println("after floor change");
            }
            else if (floors.get(oldFl) != floors.get(newFl)) {    // Otherwise if we're changing floors, give a floor change direction
                directions.add(upDownConverter(oldFl, newFl, path.get(i+1).getNodeType()));
                afterFloorChange = true;
            }
            else {    // Otherwise provide a normal direction
                directions.add(csDirPrint(path.get(i), path.get(i+1), path.get(i+2)));
                afterFloorChange = false;    // TODO cut?
            }
        }

        // Add the final direction
        directions.add("You have arrived at " + path.get(path.size() - 1).getLongName() + ".");

        // Simplify directions that continue approximately straight from each other
        for (int i = 1; i < directions.size() - 1; i++) {
            String currDir = directions.get(i);
            String currOne = currDir.substring(0,1);
            String oldDir = directions.get(i+1);
            String oldOne = oldDir.substring(0,1);
            if ("ANOPQ".contains(currOne) && currOne.equals(oldOne)) {    // If the current direction contains straight, get the distance substring
                String newDir = "";
                if (currOne.equals("A")) {
                    int oldDist = Integer.parseInt(oldDir.substring(1));
                    int currDist = Integer.parseInt(currDir.substring(1));
                    int totalDist = oldDist + currDist;    // Combine the distance of this direction with the previous one
                    newDir = currOne + totalDist;
                }
                else {
                    newDir = currOne + oldDir.substring(1,2) + currDir.substring(2,3);
                }
                directions.remove(i+1);
                directions.remove(i);
                directions.add(i, newDir);
                i--;
            }
        }

        System.out.println(directions);
        printDirections(directions);
        return directions;
    }

    /**
     * Convert two floors into an up/down elevator/stairs instruction
     * @param f1 the first floor
     * @param f2 the second floor
     * @param type the nodeType of the first node
     * @return the instruction for up/down stairs/elevator
     */
    public String upDownConverter(String f1, String f2, String type) {
        HashMap<String, String> floorsQR = new HashMap<>();
        floorsQR.put("L2", "A");
        floorsQR.put("L1", "B");
        floorsQR.put("G", "C");
        floorsQR.put("1", "D");
        floorsQR.put("2", "E");
        floorsQR.put("3", "F");

        String ret = "";

        if (floors.get(f1) < floors.get(f2)) {
            if (type.equals("ELEV")) {
                ret = ("N" + floorsQR.get(f1) + floorsQR.get(f2));
            } else {
                ret = ("P" + floorsQR.get(f1) + floorsQR.get(f2));
            }
        }
        else {
            if (type.equals("ELEV")) {
                ret = ("O" + floorsQR.get(f1) + floorsQR.get(f2));
            } else {
                ret = ("Q" + floorsQR.get(f1) + floorsQR.get(f2));
            }
        }
        return ret;
    }

    /**
     * Populate the listview and turn the list of directions into one printable string.
     * @param ds the list of directions as strings
     * @return a String that is the sum of all the directions
     */
    public String printDirections(ArrayList<String> ds) {    // TODO
        HashMap<String, String> backToFloors = new HashMap<>();
        backToFloors.put("A", "L2");
        backToFloors.put("B", "L1");
        backToFloors.put("C", "G");
        backToFloors.put("D", "1");
        backToFloors.put("E", "2");
        backToFloors.put("F", "3");
        ArrayList<String> directions = new ArrayList<>();
        directions.add(ds.get(0));
        ObservableList<Label> dirs = FXCollections.observableArrayList();
        ArrayList<Label> labels = new ArrayList<>();
        for (int i = 1; i < ds.size() - 1; i++) {
            String direct = ds.get(i);
            switch(direct.substring(0,1)) {
                case "A":
                    direct = "Walk straight for " + direct.substring(1) + " feet.\n";
                    break;
                case "B":
                    direct = "Turn left and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "C":
                    direct = "Turn slightly left and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "D":
                    direct = "Turn sharply left and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "E":
                    direct = "Turn right and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "F":
                    direct = "Turn slightly right and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "G":
                    direct = "Turn sharply right and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "H":
                    direct = "Turn around and walk for " + direct.substring(1) + " feet.\n";
                    break;
                case "I":
                    direct = "Walk to the elevator.\n";
                    break;
                case "J":
                    direct = "Walk to the stairs.\n";
                    break;
                case "N":
                    direct = "Take the elevator up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "O":
                    direct = "Take the elevator down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";                    break;
                case "P":
                    direct = "Take the stairs up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";                    break;
                case "Q":
                    direct = "Take the stairs down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";                    break;
                case "S":
                    direct = "Walk north for " + direct.substring(1) + " feet.\n";
                    break;
                case "T":
                    direct = "Walk north west for " + direct.substring(1) + " feet.\n";
                    break;
                case "U":
                    direct = "Walk west for " + direct.substring(1) + " feet.\n";
                    break;
                case "V":
                    direct = "Walk south west for " + direct.substring(1) + " feet.\n";
                    break;
                case "W":
                    direct = "Walk south for " + direct.substring(1) + " feet.\n";
                    break;
                case "X":
                    direct = "Walk south east for " + direct.substring(1) + " feet.\n";
                    break;
                case "Y":
                    direct = "Walk east for " + direct.substring(1) + " feet.\n";
                    break;
                case "Z":
                    direct = "Walk north east for " + direct.substring(1) + " feet.\n";
                    break;
                default:
                    direct = "Houston we have a problem";
                    break;
            }

//            Label l = new Label(direct);
//            l.setWrapText(true);
//            l.setTextFill(Color.WHITE);
//            labels.add(l); TODO TESTING
            directions.add(direct);
        }

//        dirs.addAll(labels);
//        directionsView.setItems(dirs);

        // Print out the directions TODO cut
        directions.add(ds.get(ds.size() -1));
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < directions.size(); ++i) {
            buf.append(directions.get(i));
        }
        String total = buf.toString();
        System.out.println(total);    // TODO cut
        return total;
    }


    /**
     * Convert this direction to a cardinal direction
     * ONLY WHEN MADE WITH SOUTH AS THE FIRST VECTOR
     * @param cardinal the direction with the first vector going south
     * @return the direction as a cardinal direction
     */
    public String convertToCardinal(String cardinal) {
        if (cardinal.contains("C")) {
            cardinal = "X" + cardinal.substring(1);
        }
        else if (cardinal.contains("F")) {
            cardinal = "V" + cardinal.substring(1);
        }
        else if (cardinal.contains("D")) {
            cardinal = "Z" + cardinal.substring(1);
        }
        else if (cardinal.contains("G")) {
            cardinal = "T " + cardinal.substring(1);
        }
        else if (cardinal.contains("B")) {
            cardinal = "Y" + cardinal.substring(1);
        }
        else if (cardinal.contains("E")) {
            cardinal = "U" + cardinal.substring(1);
        }
        else if (cardinal.contains("A")) {
            cardinal = "W" + cardinal.substring(1);
        }
        else {
            cardinal = "S" + cardinal.substring(1);
        }
        return cardinal;
    }

    public String csDirPrint(int x, int y, Node curr, Node next) {
        Node n1 = new Node("ID", x, y, "HALL");
        return csDirPrint(n1, curr, next);
    }

    /**
     * Compute the direction turned and distance between the middle and last point for the given 3 points.
     * @param prev the previous node
     * @param curr the current node
     * @param next the next node
     * @return the direction for someone walking from points 1 to 3 with the turn direction and distance
     *      *          between the middle and last point
     */
    public String csDirPrint(Node prev, Node curr, Node next) {
//        if(!curr.getNodeType().equals("ELEV") && !curr.getNodeType().equals("STAI") && (next.getNodeType().equals("ELEV") || next.getNodeType().equals("STAI"))) {    // If next node is elevator, say so
//            if (next.getNodeType().equals("ELEV")) {
//                return "I";
//            } else {
//                return "J";
//            }
//        } TODO

        double prevXd, prevYd, currXd, currYd, nextXd, nextYd;
        prevXd = prev.getXcoord();
        prevYd = prev.getYcoord();
        currXd = curr.getXcoord();
        currYd = curr.getYcoord();
        nextXd = next.getXcoord();
        nextYd = next.getYcoord();

        final double THRESHOLD = .0001;   // Double comparison standard

        // The slopes for the two vectors and y-intercept for the second vector as a line
        double slope1, slope2, intercept;
        slope1 = (currYd - prevYd) / (currXd - prevXd);
        slope2 = (nextYd - currYd) / (nextXd - currXd);
        intercept = nextYd - slope2 * nextXd;

        // The vector components for both vectors and their lengths
        double oldI, oldJ, newI, newJ, lengthOld, lengthNew;
        oldI = currXd - prevXd;
        oldJ = currYd - prevYd;
        newI = nextXd - currXd;
        newJ = nextYd - currYd;
        lengthOld = Math.sqrt(oldI*oldI + oldJ*oldJ);
        lengthNew = Math.sqrt(newI*newI + newJ * newJ);

        // Distance in feet based on measurements from the map: 260 pixels per 85 feet
        double distance = lengthNew /260 * 85;

        // Compute the angle, theta, between the old and new vector
        double uDotV = oldI * newI + oldJ * newJ;
        double theta, alpha, plus, minus;
        theta = Math.acos(uDotV/(lengthNew*lengthOld));
        alpha = Math.atan(slope1);    // Compute the angle, alpha, between the old vector and horizontal
        plus = theta + alpha;    // The sum of the two angles
        minus = alpha - theta;    // The difference between the two angles

        double computedY1 = currYd + Math.tan(plus);    // Guess which side of the old vector we turned to

        double expectedVal = (currXd + 1) * slope2 + intercept;    // The actual side of the old vector we turned to

        if (Math.abs(newI) < THRESHOLD) {    // If the next vector is vertical, make sure it does what it's supposed to
            if ((nextYd > currYd && prevXd < currXd) || (nextYd < currYd && prevXd > currXd)) {
                expectedVal = 1;
                computedY1 = 1;
            }
            else {
                expectedVal = 1;
                computedY1 = 0;
            }
        }
        // If the next vector is horizontal and this one was vertical, make it give the correct direction
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

        if (Math.abs(plus - minus) < Math.PI/8) {    // Say straight within a small angle
            turn = "A";
        }
        else if (Math.abs(theta - Math.PI) < THRESHOLD) {    // Turn around if theta is to behind you
            turn = "H";
        }
        else if (Math.abs(expectedVal - computedY1) < THRESHOLD) {    // Otherwise turn the correct direction
            if (theta <= Math.PI/4) {
                turn = "F";
            }
            else if (theta >= Math.PI*3/4) {
                turn = "G";
            }
            else {
                turn = "E";
            }
        }
        else {
            if (theta <= Math.PI/4) {
                turn = "C";
            }
            else if (theta >= Math.PI*3/4) {
                turn = "D";
            }
            else {
                turn = "B";
            }
        }
        System.out.println(" turn " + turn + " theta " + theta + " alpha " + alpha + " expected " +
                        expectedVal + " computed " + computedY1 + "\n  PLUS " + plus +
                        " minus " + minus);

        // Create and return the direction
        String direction = String.format(turn +  "%.0f", distance);
        return direction;
    }

    /**
     * On click, show the directions. On second click, hide them again.
     */
    @FXML
    public void showDirections() {
        // TODO
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

    /**
     * Compress a given set of directions into a series of characters
     * to be used in a QR code
     * Format: <Instruction> <Distance/Floor> <Hint>
     * @return the String to use in the QR code
     */
    private String convertToQRCode(ArrayList<String> directions) {
        // TODO if necc - ex all into one
        return "";
    }
}
