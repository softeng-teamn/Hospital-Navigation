package home;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import com.jfoenix.controls.JFXButton;
import database.DatabaseService;
import elevator.ElevatorConnection;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import elevator.ElevatorConnection;
import application_state.Event;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.util.Duration;
import map.MapNode;
import map.Node;
import map.PathFindingService;
import net.kurobako.gesturefx.GesturePane;
import service.ResourceLoader;
import service.StageManager;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class MapViewController implements Observer {
    static ElevatorConnection elevatorCon = new ElevatorConnection();

    @FXML
    public VBox showDirVbox;
    @FXML
    public JFXButton showDirectionsBtn;
    private Event event;

    private String currentMethod;

    private Group zoomGroup;
    private Circle startCircle;
    private Circle selectCircle;
    private ArrayList<Line> lineCollection;
    private ArrayList<Circle> circleCollection;
    private HashMap<String, ArrayList<Polyline>> polylineCollection;
    private boolean hasPath = false;
    private ArrayList<Node> path;
    private String units = "feet";    // Feet or meters conversion
    private HashMap<String, Integer> floors = new HashMap<String, Integer>();
    // Scroll & Zoom
    private ImageView floorImg;
    private static HashMap<String, ImageView> imageCache;
    private static final double MIN_ZOOM = 0.4;
    private static final double MAX_ZOOM = 1.2;


    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXButton about_btn;
    @FXML
    private GesturePane gPane;
    @FXML
    private Label FloorInfo;

    // switch to about page
    @FXML
    void showAbout(ActionEvent e) throws Exception {
        Stage stage = (Stage) about_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.about,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"About Page");
    }

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
        //pingTiming();
        gPane.currentScaleProperty().setValue(MIN_ZOOM+0.1);
        zoomGroupInit();
        imagesInit();
        // listen to changes
        ApplicationState.getApplicationState().getObservableBus().register("mapViewContoller", this);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();

        // Setup collection of lines
        lineCollection = new ArrayList<>();
        polylineCollection = new HashMap<>();

        // Set start circle
        startCircle = new Circle();

        // Initialize Circle Collection
        circleCollection = new ArrayList<>();

        // Setting Up Circle Destination Point
        startCircle.setCenterX(currState.getStartNode().getXcoord());
        startCircle.setCenterY(currState.getStartNode().getYcoord());
        startCircle.setRadius(20);
        startCircle.setFill(Color.rgb(67, 70, 76));
        zoomGroup.getChildren().add(startCircle);
    }

    void zoomGroupInit() {
        zoomGroup = new Group();
        gPane.setContent(zoomGroup);
    }

    void imagesInit() {
        imageCache = ApplicationState.getApplicationState().getImageCache();
        this.floorImg = imageCache.get("1");
        setFloor("1"); // DEFAULT
    }

    /*
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
    */

    // switch floor to new map image
    public void setFloor(String floor) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        ImageView newImg;
        if (imageCache.containsKey(floor)) {
            newImg = imageCache.get(floor);
            event.setFloor(floor);
        } else {
            // unknown floor change | SETTING TO DEFAULT
            newImg = imageCache.get("1");
            event.setFloor("1");
        }
        zoomGroup.getChildren().remove(this.floorImg);
        zoomGroup.getChildren().add(newImg);
        this.floorImg = newImg;
    }

    @FXML
    void floorChangeAction(ActionEvent e){
        JFXButton btn = (JFXButton)e.getSource();
        setFloor(btn.getText());
        if (hasPath){
            drawPath();
        }
    }


    @Override
    public void notify(Object e) {
        System.out.println("    mapView notified " + event.getEventName() + " " + this);   // todo cut
        ApplicationState currState = ApplicationState.getApplicationState();
        event = (Event) e;
        switch (event.getEventName()) {
            case "navigation":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            navigationHandler();
                        } catch (Exception ex) {
                            //System.out.println("error posting floor");
                            ex.printStackTrace();
                        }
                    }
                });
                break;
            case "node-select-end":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawPoint(currState.getEndNode(), selectCircle, Color.rgb(72, 87, 125), false);
                    }
                });
                break;
            case "node-select-start":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76), true);
                    }
                });
                break;
            case "refresh":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76), true);
                        drawPoint(event.getNodeSelected(), selectCircle, Color.rgb(72, 87, 125), false);
                    }
                });
                break;
            case "filter":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        filteredHandler();
                    }
                });
                break;
            case "methodSwitch":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentMethod = event.getSearchMethod();
                    }
                });
                break;
            case "editing":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        editNodeHandler(event.isEditing());
                    }
                });
                break;
            case "logout":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        zoomGroup.getChildren().removeAll(circleCollection);
                        circleCollection.clear();
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67,70,76), true);
                    }
                });
                break;
            case "scroll-to-direction" :
                Platform.runLater(new Runnable() {    // TODO: this erases the path. undo that.
                    @Override
                    public void run() {
                        Node n = ApplicationState.getApplicationState().getObservableBus().getEvent().getDirectionsNode();
                        setFloor(n.getFloor());
                        scrollTo(n);
                        if (hasPath){
                            drawPath();
                        }
                    }
                });
                break;
            default:
//                        System.out.println("I don'");
                break;
        }
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
                        Stage stage = (Stage) gPane.getScene().getWindow();
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
                            Stage stage = (Stage) gPane.getScene().getWindow();
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
            //System.out.println("we found new Selected Circles");
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

        if(!node.getFloor().equals(event.getFloor())){
            //switch the map
            //System.out.println(node + node.getFloor());
                setFloor(node.getFloor());
        }

        // Scroll to new point
        scrollTo(node);

        //display node info
        System.out.println("done drawing point");
    }

    // generate path on the screen
    private void navigationHandler() throws Exception {
        currentMethod = event.getSearchMethod();
        ApplicationState currState = ApplicationState.getApplicationState();
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(currState.getStartNode().getXcoord(), currState.getStartNode().getYcoord(), currState.getStartNode());
        MapNode dest = new MapNode(currState.getEndNode().getXcoord(),currState.getEndNode().getYcoord(), currState.getEndNode());
        // check if the path need to be 'accessible'
        if (event.isAccessiblePath()) {
            // do something special
            newpath = pathFinder.genPath(start, dest, true, currentMethod);
        } else {
            // not accessible
            newpath = pathFinder.genPath(start, dest, false, currentMethod);
        }
        if (event.isCallElev()) {//if we are supposed to call elevator
            ElevatorConnection e = new ElevatorConnection();
            if (pathFinder.getElevTimes() != null) {    // TODO: do breadth and depth set elevTimes? I'm getting null pointer exceptions here when I use them
                for (String key : pathFinder.getElevTimes().keySet()
                ) {
                    System.out.println("Calling Elevator " + key + "to floor " + pathFinder.getElevTimes().get(key).getFloor());
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.add(Calendar.MINUTE, pathFinder.getElevTimes().get(key).getEta());
                    try {
                        e.postFloor(key.substring(key.length() - 1), pathFinder.getElevTimes().get(key).getFloor(), gc);
                    } catch (Exception ex) {
                        System.out.println("WifiConnectionError, post didn't happen");
                        throw new Exception(ex);
                    }
                }
            }
        } // todo

        path = newpath;
        hasPath = false;
        if (path != null && path.size() > 1) {
            drawPath();
            event = ApplicationState.getApplicationState().getObservableBus().getEvent();
            event.setPath(path);
            event.setEventName("showText");     // Changed b/c shouldn't try to show directions for nonexistent paths
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        }
    }

    private void filteredHandler() {
        PathFindingService pathFinder = new PathFindingService();
        ApplicationState currState = ApplicationState.getApplicationState();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(currState.getStartNode().getXcoord(), currState.getStartNode().getYcoord(), currState.getStartNode());
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
        hasPath = false;
        if (path != null && path.size() > 1) {
            drawPath();
            event = ApplicationState.getApplicationState().getObservableBus().getEvent();
            event.setPath(path);
            event.setEventName("showText");     // Changed b/c shouldn't try to show directions for nonexistent paths
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        }
    }

    @SuppressFBWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    private void drawPath() {

        if(!hasPath){
            setFloor(path.get(0).getFloor());
            scrollTo(path.get(0));
        }

        for (ArrayList<Polyline> polylines : polylineCollection.values()) {
            for(Polyline polyline : polylines){
                if(zoomGroup.getChildren().contains(polyline)){
                    zoomGroup.getChildren().remove(polyline);
                }
            }
        }

        polylineCollection.clear();

        Polyline polyline = new Polyline();

        Node last = path.get(0);
        polyline.getPoints().addAll((double) last.getXcoord(), (double) last.getYcoord());

        for(int i = 1; i < path.size(); i++) {
            Node current = path.get(i);
            if(!current.getFloor().equals(last.getFloor())){
                addToList(last.getFloor(), polyline);
                if(last.getFloor().equals(event.getFloor())){
                    Node next = current;
                    int j = i;
                    while (next.getNodeType().equals(current.getNodeType())){
                        j++;
                        next = path.get(j);
                    }
                    addButton(current, next);
                }
                polyline = new Polyline();
            }
            polyline.getPoints().addAll((double) current.getXcoord(), (double) current.getYcoord());
            last = current;
        }

        addToList(path.get(path.size() - 1).getFloor(), polyline);

        for(String floor : polylineCollection.keySet()) {
            if(floor.equals(event.getFloor())){
                ArrayList<Polyline> polylines = polylineCollection.get(floor);
                for(Polyline pl : polylines){
                    addAnimation(pl);
                    zoomGroup.getChildren().add(pl);

                }
            }
        }
        hasPath = true;
    }

    private void addButton(Node current, Node next){
        JFXButton floorSwitcher = new JFXButton("Take the " + current.getNodeType() + " to floor " + next.getFloor());
        floorSwitcher.getStyleClass().add("path-button");
        floorSwitcher.setTranslateX(current.getXcoord());
        floorSwitcher.setTranslateY(current.getYcoord());
        floorSwitcher.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setFloor(next.getFloor());
                drawPath();
            }
        });
        zoomGroup.getChildren().add(floorSwitcher);
    }

    private synchronized void addToList(String mapKey, Polyline polyline) {
        ArrayList<Polyline> polylines = polylineCollection.get(mapKey);

        // if list does not exist create it
        if(polylines == null) {
            polylines = new ArrayList<Polyline>();
            polylines.add(polyline);
            polylineCollection.put(mapKey, polylines);
        } else {
            // add if item is not already in list
            if(!polylines.contains(polyline)) polylines.add(polyline);
        }
    }

    private void addAnimation(Polyline line){
        line.getStrokeDashArray().setAll(16d, 16d);
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(8);

        final double maxOffset =
                line.getStrokeDashArray().stream()
                        .reduce(
                                0d,
                                (a, b) -> a + b
                        );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                maxOffset,
                                Interpolator.LINEAR
                        )
                )
        );
        timeline.setRate(-1);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void scrollTo(Node node) {
        // animation scroll to new position
        gPane.animate(Duration.millis(200))
                .interpolateWith(Interpolator.EASE_BOTH)
                .beforeStart(() -> System.out.println("Starting..."))
                .afterFinished(() -> System.out.println("Done!"))
                .centreOn(new Point2D(node.getXcoord(), node.getYcoord()));
    }
}
