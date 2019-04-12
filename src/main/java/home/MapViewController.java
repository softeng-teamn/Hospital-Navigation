package home;

import application_state.ApplicationState;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import elevator.ElevatorConnnection;
import application_state.Event;
import application_state.EventBusFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import map.MapNode;
import map.Node;
import database.DatabaseService;
import map.PathFindingService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class MapViewController {
    static ElevatorConnnection elevatorCon = new ElevatorConnnection();

    @FXML
    public VBox showDirVbox;
    @FXML
    public JFXButton showDirectionsBtn;
    private EventBus eventBus = EventBusFactory.getEventBus();
    private Event event = EventBusFactory.getEvent();

    private String currentMethod;

    private Group zoomGroup;
    private Circle startCircle;
    private Circle selectCircle;
    private ArrayList<Line> lineCollection;
    private ArrayList<Circle> circleCollection;
    private boolean hasPath = false;
    private ArrayList<Node> path;
    private String units = "feet";    // Feet or meters conversion
    private HashMap<String, Integer> floors = new HashMap<String, Integer>();


    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private JFXButton f1_btn, f2_btn, f3_btn, l1_btn, l2_btn, ground_btn;
    @FXML
    private Pane image_pane;
    @FXML
    private JFXButton call_el1_btn, call_el2_btn, call_el3_btn, call_el4_btn;
    @FXML
    private Label cur_el_floor;
    @FXML
    public JFXListView directionsView;

    private static HashMap<String, ImageView> imageCache = new HashMap<>();
    private static boolean imagesCached = false;

    // ELEVATOR CALL BUTTONS
    @FXML
    void callElevatorAction(ActionEvent e) {
        JFXButton myBtn = (JFXButton) e.getSource();
        String elevNum = "" + myBtn.getText().substring(myBtn.getText().length() - 2);

        GregorianCalendar cal = new GregorianCalendar();
        try {
            elevatorCon.postFloor("S", elevNum, cal);
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

        // Initialize Circle Collection
        circleCollection = new ArrayList<Circle>();

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

        directionsView.setVisible(false);

        // Cache imageViews so they can be reused, but only if they haven't already been cached
        if(!imagesCached) {
            try {
                imageCache.put("3", new ImageView(new Image(ResourceLoader.thirdFloor.openStream())));
                imageCache.put("2", new ImageView(new Image(ResourceLoader.secondFloor.openStream())));
                imageCache.put("1", new ImageView(new Image(ResourceLoader.firstFloor.openStream())));
                imageCache.put("L1", new ImageView(new Image(ResourceLoader.firstLowerFloor.openStream())));
                imageCache.put("L2", new ImageView(new Image(ResourceLoader.secondLowerFloor.openStream())));
                imageCache.put("G", new ImageView(new Image(ResourceLoader.groundFloor.openStream())));
                imagesCached = true;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    void pingTiming() {

        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                while (true) {
                    Thread.sleep(1000);
//                    System.out.println("shit was fired");
                    TimeUnit.SECONDS.sleep(1);
//                    System.out.println("Elevator At: " + elevatorCon.getFloor("S"));
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            try {
//                                System.out.println("Showing at: " + elevatorCon.getFloor("S"));
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

    @FXML
    void floorChangeAction(ActionEvent e) throws IOException {
        JFXButton btn = (JFXButton)e.getSource();
        ImageView imageView;
        event.setEventName("floor");
        String floorName = "";
        event.setFloor(btn.getText());
        switch (btn.getText()) {
            case "3":
                imageView = imageCache.get("3");
                floorName = "3";
                break;
            case "2":
                imageView = imageCache.get("2");
                floorName = "2";
                break;
            case "1":
                imageView = imageCache.get("1");
                floorName = "1";
                break;
            case "L1":
                imageView = imageCache.get("L1");
                floorName = "L1";
                break;
            case "L2":
                imageView = imageCache.get("L2");
                floorName = "L2";
                break;
            case "G":
                imageView = imageCache.get("G");
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
        if (hasPath){
            drawPath();
        }
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
                        try {
                            navigationHandler();
                        }
                        catch(Exception ex){
                            System.out.println("error posting floor");
                        }
                        break;
                    case "node-select":
                        if(event.isEndNode()){
                            drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72,87,125), false);
                        } else {
                            drawPoint(event.getNodeStart(), startCircle, Color.rgb(67,70,76), true);
                        }
                        directionsView.getItems().clear();
                        hideDirections();
                        break;
                    case "refresh":
                        drawPoint(event.getNodeStart(), startCircle, Color.rgb(67,70,76), true);
                        drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72,87,125), false);
                        break;
                    case "filter":
                        filteredHandler();
                        break;
                    case "methodSwitch":
                        currentMethod = event.getSearchMethod();
                        break;
                    case "editing":
                        editNodeHandler(event.isEditing());
                        break;
                    default:
//                        System.out.println("I don'");
                        break;
                }
            }
        });
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
                    }
                });
                nodeCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        tp.hide();
                    }
                });
                nodeCircle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                        ApplicationState.getApplicationState().setNodeToEdit(n);

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


    private void clearBothPoint(){
        if (zoomGroup.getChildren().contains(selectCircle)){
            zoomGroup.getChildren().remove((selectCircle));
        }
        if (zoomGroup.getChildren().contains(startCircle)){
            zoomGroup.getChildren().remove(startCircle);
        }
    }


    private void drawPoint(Node node, Circle circle, Color color, boolean start) {
        // remove points
        for (Line line : lineCollection) {
            if (zoomGroup.getChildren().contains(line)) {
                zoomGroup.getChildren().remove(line);
                hasPath = false;
            }
        }
        // remove old selected Circle
        if (zoomGroup.getChildren().contains(circle)) {
            System.out.println("we found new Selected Circles");
            zoomGroup.getChildren().remove(circle);
        }
        // create new Circle
        circle = new Circle();
        circle.setCenterX(node.getXcoord());
        circle.setCenterY(node.getYcoord());
        circle.setRadius(20);
        circle.setFill(color);
        zoomGroup.getChildren().add(circle);
        // set circle to selected
        if (start){
            startCircle = circle;
        } else {
            selectCircle = circle;
        }
        // Scroll to new point
        scrollTo(node);


    }

    // generate path on the screen
    private void navigationHandler() throws Exception{
        currentMethod = event.getSearchMethod();
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(event.getNodeStart().getXcoord(), event.getNodeStart().getYcoord(), event.getNodeStart());
        MapNode dest = new MapNode(event.getNodeSelected().getXcoord(), event.getNodeSelected().getYcoord(), event.getNodeSelected());
        // check if the path need to be 'accessible'
        if (event.isAccessiblePath()) {
            // do something special
            newpath = pathFinder.genPath(start, dest, true, currentMethod);
        } else {
            // not accessible
            newpath = pathFinder.genPath(start, dest, false, currentMethod);
        }
        if(event.isCallElev()){//if we are supposed to call elevator
            ElevatorConnnection e = new ElevatorConnnection();
            for (String key: pathFinder.getElevTimes().keySet()
            ) {
                System.out.println("Calling Elevator " + key + "to floor " + pathFinder.getElevTimes().get(key).getFloor());
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.MINUTE, pathFinder.getElevTimes().get(key).getEta());
                try {
                    e.postFloor(key.substring(key.length() - 1), pathFinder.getElevTimes().get(key).getFloor(), gc);
                }
                catch (Exception ex){
                    System.out.println("WifiConnectionError, post didn't happen");
                    throw new Exception(ex);
                }
            }
        }

        path = newpath;
        drawPath();



    }

    private void filteredHandler() {
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(event.getNodeStart().getXcoord(), event.getNodeStart().getYcoord(), event.getNodeStart());
        Boolean accessibility = event.isAccessiblePath();

        switch (event.getFilterSearch()){
            case "REST":
                newpath = pathFinder.genPath(start, null, accessibility, "REST");
                break;
            case "ELEV":
                newpath = pathFinder.genPath(start, null, accessibility, "ELEV");
                break;
            case "STAI":
                newpath = pathFinder.genPath(start, null, false, "STAI");
                break;
            case "CONF":
                newpath = pathFinder.genPath(start, null, accessibility, "CONF");
                break;
            case "INFO":
                newpath = pathFinder.genPath(start, null, accessibility, "INFO");
                break;
            case "EXIT":
                newpath = pathFinder.genPath(start, null, accessibility, "EXIT");
                break;
            default:
                newpath = null;
                break;
        }

        if (newpath == null){
            System.out.println("DIDNT FIND A PATH");
        } else {
            drawPoint(newpath.get(newpath.size()-1), selectCircle, Color.rgb(72,87,125), false);
        }

        path = newpath;
        drawPath();
    }

    // draw path on the screen
    private void drawPath() {
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

                if (current.getFloor().equals(event.getFloor())){
                    line.setStroke(Color.valueOf("183284"));
                } else {
                    line.setStroke(Color.rgb(139,155,177));
                }
                line.setStrokeWidth(20.0);
                zoomGroup.getChildren().add(line);
                lineCollection.add(line);
                last = current;
            }

//            event.setPath(path);
//            event.setEventName("showText");
//            eventBus.post(event);

            printDirections(makeDirections(path));

        }

        hasPath = true;

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
        floors.put("L2", -2);
        floors.put("L1", -1);
        floors.put("G", 0);
        floors.put("1", 1);
        floors.put("2", 2);
        floors.put("3", 3);

        if (path == null || path.size() < 2) {
            return null;
        }

        final int NORTH_I = 1122 - 1886;    // Measurements from maps
        final int NORTH_J = 642 - 1501;    // Measurements from maps

        ArrayList<String> directions = new ArrayList<>();    // Collection of instructions
        directions.add("\nStart at " + path.get(0).getLongName() + ".\n");    // First instruction

        // Make the first instruction cardinal, or up/down if it is a floor connector
        String oldFloor = path.get(0).getFloor();
        String newFloor = path.get(1).getFloor();
        if (!floors.get(oldFloor).equals(floors.get(newFloor))) {
            directions.add(upDownConverter(oldFloor, newFloor, path.get(0).getNodeType()));
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("ELEV")) || (path.size() > 2 && path.get(1).getNodeType().equals("ELEV") && path.get(2).getNodeType().equals("ELEV"))) {
            directions.add("I");
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("STAI")) || (path.size() > 2 && path.get(1).getNodeType().equals("STAI") && path.get(2).getNodeType().equals("STAI"))) {
            directions.add("J");
        }
        else {
            directions.add(convertToCardinal(csDirPrint(path.get(0).getXcoord() + NORTH_I, path.get(0).getYcoord() + NORTH_J, path.get(0), path.get(1))));
        }

        boolean afterFloorChange = false;    // If we've just changed floors, give a cardinal direction
        for (int i = 0; i < path.size() - 2; i++) {    // For each node in the path, make a direction
            System.out.println("in loop: " + directions);
            String oldFl = (path.get(i+1).getFloor());
            String newFl = (path.get(i+2).getFloor());
            if (afterFloorChange && !path.get(i + 2).getNodeType().equals("ELEV") && !path.get(i + 2).getNodeType().equals("STAI")) {
                afterFloorChange = false;
                directions.add(convertToCardinal(csDirPrint(path.get(i+1).getXcoord() + NORTH_I, path.get(i+1).getYcoord() + NORTH_J, path.get(i+1), path.get(i+2))));
            }
            else if(!path.get(i+1).getNodeType().equals("ELEV") && !path.get(i+1).getNodeType().equals("STAI") && (path.get(i+2).getNodeType().equals("ELEV") || path.get(i+2).getNodeType().equals("STAI"))
                    && ((i < path.size() - 3 && (path.get(i+3).getNodeType().equals("ELEV") || path.get(i+3).getNodeType().equals("STAI"))) || i == path.size() -3)) {    // If next node is elevator, say so
                if (path.get(i+2).getNodeType().equals("ELEV")) {
                    directions.add("I");
                } else {
                    directions.add("J");
                }
            }
            else if (!floors.get(oldFl).equals(floors.get(newFl))) {    // Otherwise if we're changing floors, give a floor change direction
                directions.add(upDownConverter(oldFl, newFl, path.get(i+1).getNodeType()));
                afterFloorChange = true;
            }
            else {    // Otherwise provide a normal direction
                directions.add(csDirPrint(path.get(i), path.get(i+1), path.get(i+2)));
                afterFloorChange = false;
            }
        }

        System.out.println("before simplifying: " + directions);
        // Simplify directions that continue approximately straight from each other
        for (int i = 1; i < directions.size(); i++) {
            String currDir = directions.get(i);
            String currOne = currDir.substring(0,1);
            String prevDir = directions.get(i-1);
            String prevOne = prevDir.substring(0,1);
            String newDir = "";
            boolean changed = false;
            if (currOne.equals("A") && !"IJ".contains(prevOne)) {
                System.out.println("straight " + prevDir + currDir);
                int prevDist = Integer.parseInt(prevDir.substring(1,6));
                int currDist = Integer.parseInt(currDir.substring(1,6));
                double totalDist = prevDist + currDist;    // Combine the distance of this direction with the previous one
                newDir = prevOne + padWithZeros(totalDist);
                changed = true;
            }
            else if ("NOPQ".contains(currOne) && currOne.equals(prevOne)) {    // If the current direction contains straight, get the distance substring
                newDir = currOne + prevDir.substring(1, 2) + currDir.substring(2, 3);
                changed = true;
            }
            if (changed) {
                directions.remove(i);
                directions.remove(i-1);
                directions.add(i-1, newDir);
                i--;
            }
            System.out.println("in loop simplifying");
        }
        System.out.println("out of loop");

        // Add the final direction
        directions.add("You have arrived at " + path.get(path.size() - 1).getLongName() + ".");
        System.out.println(directions); // TODO cut
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
    public String printDirections(ArrayList<String> ds) {
        System.out.println("printing...");
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

        Label first = new Label(ds.get(0));
        first.setWrapText(true);
        first.setTextFill(Color.WHITE);
        labels.add(first);

        for (int i = 1; i < ds.size() - 1; i++) {
            String direct = ds.get(i);
            switch(direct.substring(0,1)) {
                case "A":
                    direct = "Walk straight for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "B":
                    direct = "Turn left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "C":
                    direct = "Turn slightly left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "D":
                    direct = "Turn sharply left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "E":
                    direct = "Turn right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "F":
                    direct = "Turn slightly right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "G":
                    direct = "Turn sharply right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "H":
                    direct = "Turn around and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
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
                    direct = "Take the elevator down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "P":
                    direct = "Take the stairs up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "Q":
                    direct = "Take the stairs down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "S":
                    direct = "Walk north for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "T":
                    direct = "Walk north west for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "U":
                    direct = "Walk west for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "V":
                    direct = "Walk south west for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "W":
                    direct = "Walk south for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "X":
                    direct = "Walk south east for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "Y":
                    direct = "Walk east for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                case "Z":
                    direct = "Walk north east for " + Integer.parseInt(direct.substring(1,6)) + " " + units + ".\n";
                    break;
                default:
                    direct = "Houston we have a problem";
                    break;
            }

            Label l = new Label(direct);
            l.setWrapText(true);
            l.setTextFill(Color.WHITE);
            labels.add(l);
            directions.add(direct);
        }
        directions.add(ds.get(ds.size() -1));

        Label last = new Label(ds.get(ds.size() - 1));
        last.setWrapText(true);
        last.setTextFill(Color.WHITE);
        labels.add(last);

        dirs.addAll(labels);
        directionsView.setItems(dirs);

        // Return the directions
        directions.add(ds.get(ds.size() -1));
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < directions.size(); ++i) {
            buf.append(directions.get(i));
        }
        String total = buf.toString();
        System.out.println(total);
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
            cardinal = "T" + cardinal.substring(1);
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
        else if (cardinal.contains("I") || cardinal.contains("J")) {
            // Leave as is
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
        double distance;
        if (units.equals("feet")) {
            distance = lengthNew /260 * 85;    // Pixels to feet
        }
        else {
            distance = lengthNew / 260 * 25.908;    // Pixels to meters
        }

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

        // Create and return the direction
        String distPadded = padWithZeros(distance);    // Pad direction with zeros so always same lengtb
        String direction = turn + distPadded;
        return direction;
    }

    /**
     * Pad a distance with zeros so all distances are the same length
     * @param distance the distance to pad with zeros
     * @return the padded distance as a string
     */
    private String padWithZeros(double distance) {
        String orig = String.format("%.0f", distance);
        for (int i = 0; i < 6; i++) {    // Assume max distance is less than 99999 feet
            if (orig.length() < i) {    // Pad with zeroes while string length is less than 6
                orig = "0" + orig;
            }
        }
        return orig;
    }

    /**
     * On click, show the directions. On second click, hide them again.
     */
    @FXML
    public void showDirections() {
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

    private void hideDirections() {
        showDirectionsBtn.setText("Show Textual Directions");
        showDirVbox.toFront();
        showDirVbox.setAlignment(Pos.BOTTOM_RIGHT);
        directionsView.setVisible(false);
    }

    /**
     * Get the current units
     * @return current units: feet or meters
     */
    public String getUnits() {
        return units;
    }

    /**
     * Set the current units as feet or meters
     */
    public void setUnits() {
        if (units.equals("feet")) {
            units = "meters";
        }
        else {
            units = "feet";
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

    // TODO: add all changes to directions controller... blech. also cut print statements.

}