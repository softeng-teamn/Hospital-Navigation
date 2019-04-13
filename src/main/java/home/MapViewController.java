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
import map.MapController;
import map.MapNode;
import map.Node;
import database.DatabaseService;
import map.PathFindingService;
import service.ResourceLoader;
import service.StageManager;

import javax.xml.crypto.Data;
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
        } catch (IOException ioe) {
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
        if (!imagesCached) {
            try {
                imageCache.put("3", new ImageView(new Image(ResourceLoader.thirdFloor.openStream())));
                imageCache.put("2", new ImageView(new Image(ResourceLoader.secondFloor.openStream())));
                imageCache.put("1", new ImageView(new Image(ResourceLoader.firstFloor.openStream())));
                imageCache.put("L1", new ImageView(new Image(ResourceLoader.firstLowerFloor.openStream())));
                imageCache.put("L2", new ImageView(new Image(ResourceLoader.secondLowerFloor.openStream())));
                imageCache.put("G", new ImageView(new Image(ResourceLoader.groundFloor.openStream())));
                imagesCached = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void pingTiming() {

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    Thread.sleep(1000);
//                    System.out.println("shit was fired");
                    TimeUnit.SECONDS.sleep(1);
//                    System.out.println("Elevator At: " + elevatorCon.getFloor("S"));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
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
        JFXButton btn = (JFXButton) e.getSource();
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
        if (hasPath) {
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
                        } catch (Exception ex) {
                            //System.out.println("error posting floor");
                            ex.printStackTrace();
                        }
                        break;
                    case "node-select":
                        if (event.isEndNode()) {
                            drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72, 87, 125), false);
                        } else {
                            drawPoint(event.getNodeStart(), startCircle, Color.rgb(67, 70, 76), true);
                        }
                        directionsView.getItems().clear();    // TODO here
                        //hideDirections();
                        break;
                    case "refresh":
                        drawPoint(event.getNodeStart(), startCircle, Color.rgb(67, 70, 76), true);
                        drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72, 87, 125), false);
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
                        Circle c = (Circle) event.getSource();
                        tp.show(c, stage.getX() + event.getSceneX() + 15, stage.getY() + event.getSceneY());
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


    private void clearBothPoint() {
        if (zoomGroup.getChildren().contains(selectCircle)) {
            zoomGroup.getChildren().remove((selectCircle));
        }
        if (zoomGroup.getChildren().contains(startCircle)) {
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
        if (start) {
            startCircle = circle;
        } else {
            selectCircle = circle;
        }
        // Scroll to new point
        scrollTo(node);
    }

    // generate path on the screen
    private void navigationHandler() throws Exception {
        System.out.println("handler");
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
//        if (event.isCallElev()) {//if we are supposed to call elevator
//            ElevatorConnnection e = new ElevatorConnnection();
//            for (String key : pathFinder.getElevTimes().keySet()
//            ) {
//                System.out.println("Calling Elevator " + key + "to floor " + pathFinder.getElevTimes().get(key).getFloor());
//                GregorianCalendar gc = new GregorianCalendar();
//                gc.add(Calendar.MINUTE, pathFinder.getElevTimes().get(key).getEta());
//                try {
//                    e.postFloor(key.substring(key.length() - 1), pathFinder.getElevTimes().get(key).getFloor(), gc);
//                } catch (Exception ex) {
//                    System.out.println("WifiConnectionError, post didn't happen");
//                    throw new Exception(ex);
//                }
//            }
//        } // todo

        path = newpath;
        drawPath();

        event.setPath(path);
        event.setEventName("showText");     // TODO here
        //eventBus.post(event);
    }

    private void filteredHandler() {
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(event.getNodeStart().getXcoord(), event.getNodeStart().getYcoord(), event.getNodeStart());
        Boolean accessibility = event.isAccessiblePath();

        switch (event.getFilterSearch()) {
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

        if (newpath == null) {
            System.out.println("DIDNT FIND A PATH");
        } else {
            drawPoint(newpath.get(newpath.size() - 1), selectCircle, Color.rgb(72, 87, 125), false);
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

                if (current.getFloor().equals(event.getFloor())) {
                    line.setStroke(Color.valueOf("183284"));
                } else {
                    line.setStroke(Color.rgb(139, 155, 177));
                }
                line.setStrokeWidth(20.0);
                zoomGroup.getChildren().add(line);
                lineCollection.add(line);
                last = current;
            }
        }
        hasPath = true;
    }

    /**
     * zooms in the map
     *
     * @param event
     */
    @FXML
    void zoomIn(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() + 0.05);
        zoom_slider.setValue(zoom_slider.getValue());
    }

    /**
     * zooms out the map
     *
     * @param event
     */
    @FXML
    void zoomOut(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() - 0.05);
        zoom_slider.setValue(zoom_slider.getValue());
    }

    /**
     * scales zoom grouping based on given value
     *
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