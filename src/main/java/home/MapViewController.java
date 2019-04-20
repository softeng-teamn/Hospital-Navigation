package home;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import database.DatabaseService;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import elevator.ElevatorConnection;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import map.MapNode;
import map.Node;
import map.PathFindingService;
import net.kurobako.gesturefx.GesturePane;
import service.ResourceLoader;
import service.StageManager;
import service_request.model.sub_model.HelpRequest;

import java.io.IOException;
import java.util.*;


/**
 * controls the map view screen
 */
public class MapViewController implements Observer {
    static ElevatorConnection elevatorCon = new ElevatorConnection();

    @FXML
    public VBox showDirVbox;
    @FXML
    public JFXButton showDirectionsBtn;
    @FXML
    private JFXNodesList infoNodeList;
    private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();


    private String currentMethod;

    JFXButton startNodeLabel;
    JFXButton endNodeLabel;
    private Group zoomGroup;
    private Circle startCircle;
    private Circle selectCircle;
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
    private MaterialIconView location;
    public static final Color SECONDARY_COLOR = Color.rgb(246, 189, 56);
    public static final Color PRIMARY_COLOR = Color.rgb(1, 45, 90);
    public Background yellowBackground = new Background(new BackgroundFill(SECONDARY_COLOR, new CornerRadii(8), Insets.EMPTY));
    public Background blueBackground = new Background(new BackgroundFill(PRIMARY_COLOR, new CornerRadii(8), Insets.EMPTY));


    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXButton about_btn, credit_btn;
    @FXML
    private GesturePane gPane;
    @FXML
    private Label FloorInfo;
    @FXML
    private Label show4;
    @FXML
    private Label show3;
    @FXML
    private Label show2;
    @FXML
    private Label show1;
    @FXML
    private Label showG;
    @FXML
    private Label showL1;
    @FXML
    private Label showL2;
    @FXML
    private JFXButton floor4;
    @FXML
    private JFXButton floor3;
    @FXML
    private JFXButton floor2;
    @FXML
    private JFXButton floor1;
    @FXML
    private JFXButton floorG;
    @FXML
    private JFXButton floorL1;
    @FXML
    private JFXButton floorL2;
    @FXML
    private ArrayList<Label> floorLbls;
    @FXML
    private ArrayList<JFXButton> floorBtns;


    /**
     * switch to about page
     *
     * @param e FXML event that calls this method
     * @throws Exception if the FXML is not found
     */
    @FXML
    void showAbout(ActionEvent e) throws Exception {
        Stage stage = (Stage) about_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.about, event.getCurrentBundle());
        StageManager.changeExistingWindow(stage, root, "About Page");
    }

    @FXML
    void showCredit(ActionEvent e) throws Exception {
        Stage stage = (Stage) credit_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.credit, event.getCurrentBundle());
        StageManager.changeExistingWindow(stage, root, "Credit Page");
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

        ArrayList<Label> floorLbls = new ArrayList<Label>(Arrays.asList(show1, show2, show3, show4, showG, showL1, showL2));
        ArrayList<JFXButton> floorBtns = new ArrayList<JFXButton>(Arrays.asList(floor1, floor2, floor3, floor4, floorG, floorL1, floorL2));


        // set directionally available floor labels to hidden
        for (int i = 0; i < floorLbls.size(); i++) {
            floorLbls.get(i).setVisible(false);
        }

        //floor1.setBackground(yellowBackground) ;
        //System.out.println("floor 1 set to yellow (probably)");


        //pingTiming();
        gPane.currentScaleProperty().setValue(MIN_ZOOM + 0.1);
        zoomGroupInit();
        imagesInit();
        // listen to changes
        ApplicationState.getApplicationState().getObservableBus().register("mapViewContoller", this);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();

        // set background of current floor button to be yellow
        //event.getFloor()

        // Setup collection of lines
        polylineCollection = new HashMap<>();

        // Set start circle
        startCircle = new Circle();

        // Initialize Circle Collection
        circleCollection = new ArrayList<>();

        // Setting Up Circle Destination Point
//        startCircle.setCenterX(currState.getStartNode().getXcoord());
//        startCircle.setCenterY(currState.getStartNode().getYcoord());
//        startCircle.setRadius(20);
//        startCircle.setFill(Color.rgb(67, 70, 76));
//        zoomGroup.getChildren().add(startCircle);
        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76));

        infoNodeList.setRotate(90);
        infoNodeList.setSpacing(20);
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

    /**
     * switch floor to new map image
     *
     * @param floor the floor to switch the map image to
     */
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


        // change floor button based on current floor (is this a good place for this?)
        changeCurrentFloorButton();

        // clear past current floor colors
        clearCurrentFloorButton();


    }

    private void changeCurrentFloorButton() {
        String currFloor = event.getFloor();
        ArrayList<JFXButton> floorBtns = new ArrayList<JFXButton>(Arrays.asList(floor1, floor2, floor3, floor4, floorG, floorL1, floorL2));

        for (int i = 0; i < floorBtns.size(); i++) {

            if (floorBtns.get(i).getText().equals(currFloor)) {
                // change color of button
                floorBtns.get(i).setBackground(yellowBackground);
                System.out.println("YELLOW BACKGROUND IS SET");
            }
        }

    }

    private void clearCurrentFloorButton() {
        String currFloor = event.getFloor();
        ArrayList<JFXButton> floorBtns = new ArrayList<JFXButton>(Arrays.asList(floor1, floor2, floor3, floor4, floorG, floorL1, floorL2));

        System.out.println("Inside clear fcn");
        for (int i = 0; i < floorBtns.size(); i++) {
            // if yellow buttons are not the current floor, change back to blue
            System.out.println(i +" background = " + floorBtns.get(i).getBackground());
            System.out.println("yellow background = " + yellowBackground);
            if (floorBtns.get(i).getBackground() == yellowBackground) {
                System.out.println("same backgrounds");
                if (!floorBtns.get(i).getText().equals(currFloor)) {
                    System.out.println("background changed");
                    floorBtns.get(i).setBackground(blueBackground);
                } else {
                    floorBtns.get(i).setBackground(yellowBackground);
                    System.out.println(" Y BACK IS SET");
                }
            }
        }
    }

    /**
     * change floor button controller
     *
     * @param e FXML event that calls this method
     */
    @FXML
    void floorChangeAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        setFloor(btn.getText());
        if (hasPath) {
            drawPath();
        }
    }


    /**
     * Show the buttons indicating which floor the pathfinding begins and ends on
     *
     * @param start node
     * @param end   node
     */
    @FXML
    void showFloorChangeLbls(Node start, Node end) {

        ArrayList<Label> floorLbls = new ArrayList<Label>(Arrays.asList(show1, show2, show3, show4, showG, showL1, showL2));

        String startFloor = "";
        String endFloor = "";
        // get the path of the current pathfinding search

        // get start floor and end floor
        startFloor = start.getFloor();
        endFloor = end.getFloor();
        // make buttons visible

        // if start and end node are not the same
        if (!startFloor.equals(endFloor)) {

            for (int i = 0; i < floorLbls.size(); i++) {
                if (floorLbls.get(i).getText().equals(startFloor)) {
                    floorLbls.get(i).setText("Start Floor");
                    floorLbls.get(i).setVisible(true);
                } else if (floorLbls.get(i).getText().equals(endFloor)) {
                    floorLbls.get(i).setText("End Floor");
                    floorLbls.get(i).setVisible(true);
                }
            }
        } else {
            for (int i = 0; i < floorLbls.size(); i++) {
                if (floorLbls.get(i).getText().equals(startFloor)) {
                    floorLbls.get(i).setText("Start & \nEnd Floor");
                    floorLbls.get(i).setTextAlignment(TextAlignment.CENTER);
                    floorLbls.get(i).setVisible(true);
                }
            }
        }
        // NOTE: make sure to make buttons visible/invisible to respective places

    }

    /**
     * Clear the navigation floor labels from any previous searches
     */
    private void clearFloorChangeLabels() {
        ArrayList<Label> floorLbls = new ArrayList<Label>(Arrays.asList(show1, show2, show3, show4, showG, showL1, showL2));

        // set the orginal floor name text back on the labels & hide
        show1.setText("1");
        show1.setVisible(false);
        show2.setText("2");
        show2.setVisible(false);
        show3.setText("3");
        show3.setVisible(false);
        show4.setText("4");
        show4.setVisible(false);
        showG.setText("G");
        showG.setVisible(false);
        showL1.setText("L1");
        showL1.setVisible(false);
        showL2.setText("L2");
        showL2.setVisible(false);

    }


    /**
     * inhereted obsever method
     *
     * @param e the event object given
     */
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
                        deletePolyLine();
                        drawIcon(currState.getEndNode());
                    }
                });
                break;
            case "node-select-start":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        deletePolyLine();
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76));

                    }
                });
                break;
            case "refresh":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        deletePolyLine();
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76));
                        drawIcon(currState.getEndNode());
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
                        deletePolyLine();
                        if (zoomGroup.getChildren().contains(startNodeLabel)) {
                            zoomGroup.getChildren().remove(startNodeLabel);
                        }
                        if (zoomGroup.getChildren().contains(location)) {
                            zoomGroup.getChildren().remove(location);
                        }
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
                        deletePolyLine();
                        zoomGroup.getChildren().remove(location);
                        drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76));
                    }
                });
                break;
            case "scroll-to-direction":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Node n = ApplicationState.getApplicationState().getObservableBus().getEvent().getDirectionsNode();
                        setFloor(n.getFloor());
                        scrollTo(n);
                        if (hasPath) {
                            drawPath();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    void editNodeHandler(boolean isEditing) {
        if (isEditing) {
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


    private void deletePolyLine() {
        for (ArrayList<Polyline> polylines : polylineCollection.values()) {
            for (Polyline polyline : polylines) {
                if (zoomGroup.getChildren().contains(polyline)) {
                    zoomGroup.getChildren().remove(polyline);
                }
            }
        }
        hasPath = false;
    }

    private void drawPoint(Node node, Circle circle, Color color) {
        // remove old selected Circle
        if (zoomGroup.getChildren().contains(circle)) {
            //System.out.println("we found new Selected Circles");
            zoomGroup.getChildren().remove(circle);
        }

        if (zoomGroup.getChildren().contains(startNodeLabel)) {
            zoomGroup.getChildren().remove(startNodeLabel);
        }

        if (!node.getFloor().equals(event.getFloor())) {
            //switch the map
            //System.out.println(node + node.getFloor());
            setFloor(node.getFloor());
        }

        // create new Circle
        circle = new Circle();
        circle.setCenterX(node.getXcoord());
        circle.setCenterY(node.getYcoord());
        circle.setRadius(20);
        circle.setFill(color);
        zoomGroup.getChildren().add(circle);
        // set circle to selected

        startCircle = circle;


        // Scroll to new point
        scrollTo(node);

        addLabel(node, true);

        //display node info
        System.out.println("done drawing point");
    }

    private void drawIcon(Node node) {
        if (zoomGroup.getChildren().contains(location)) {
            zoomGroup.getChildren().remove(location);
        }

        if (zoomGroup.getChildren().contains(endNodeLabel)) {
            zoomGroup.getChildren().remove(endNodeLabel);
        }

        if (!node.getFloor().equals(event.getFloor())) {
            //switch the map
            //System.out.println(node + node.getFloor());
            setFloor(node.getFloor());
        }

        location = new MaterialIconView();
        location.setIcon(MaterialIcon.LOCATION_ON);
        location.setTranslateX(node.getXcoord() - 50);
        location.setTranslateY(node.getYcoord());
        location.setSize("100");
        location.getStyleClass().add("dest-icon");
        zoomGroup.getChildren().add(location);

        scrollTo(node);

        addLabel(node, false);
    }

    // generate path on the screen
    private void navigationHandler() throws Exception {

        // clear previous navigations floor labels
        clearFloorChangeLabels();

        currentMethod = event.getSearchMethod();
        ApplicationState currState = ApplicationState.getApplicationState();
        PathFindingService pathFinder = new PathFindingService();
        ArrayList<Node> newpath;
        MapNode start = new MapNode(currState.getStartNode().getXcoord(), currState.getStartNode().getYcoord(), currState.getStartNode());
        MapNode dest = new MapNode(currState.getEndNode().getXcoord(), currState.getEndNode().getYcoord(), currState.getEndNode());

        // show start and end floor labels
        showFloorChangeLbls(currState.getStartNode(), currState.getEndNode());


        // check if the path need to be 'accessible'
        if (event.isAccessiblePath()) {
            // do something special
            newpath = pathFinder.genPath(start, dest, true, currentMethod);
        } else {
            // not accessible
            newpath = pathFinder.genPath(start, dest, false, currentMethod);
        }
        /*   uncomment for auto elev call on path find, do breadth and depth things
        if (event.isCallElev()) {//if we are supposed to call elevator
            ElevatorConnection e = new ElevatorConnection();
            if (pathFinder.getElevTimes() != null) {
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
        */
        path = newpath;
        hasPath = false;
        if (path != null && path.size() > 1) {
            drawPath();
            scrollTo(path.get(0));
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
            drawIcon(newpath.get(newpath.size() - 1));
        }

        path = newpath;
        hasPath = false;
        if (path != null && path.size() > 1) {
            drawPath();
            scrollTo(path.get(0));
            event = ApplicationState.getApplicationState().getObservableBus().getEvent();
            event.setPath(path);
            event.setEventName("showText");     // Changed b/c shouldn't try to show directions for nonexistent paths
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        }
    }

    @SuppressFBWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    private void drawPath() {

        ApplicationState currState = ApplicationState.getApplicationState();

        if (!hasPath) {
            setFloor(path.get(0).getFloor());
        }

        deletePolyLine();
        polylineCollection.clear();


        Polyline polyline = new Polyline();

        Node last = path.get(0);
        polyline.getPoints().addAll((double) last.getXcoord(), (double) last.getYcoord());


        for (int i = 1; i < path.size(); i++) {
            Node current = path.get(i);
            if (!current.getFloor().equals(last.getFloor())) {
                addToList(last.getFloor(), polyline);
                if (last.getFloor().equals(event.getFloor())) {
                    Node next = current;
                    int j = i;
                    while (next.getNodeType().equals(current.getNodeType())) {
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

        for (String floor : polylineCollection.keySet()) {
            if (floor.equals(event.getFloor())) {
                ArrayList<Polyline> polylines = polylineCollection.get(floor);
                for (Polyline pl : polylines) {
                    addAnimation(pl);
                    zoomGroup.getChildren().add(pl);

                }
            }
        }

        if (event.getFloor().equals(path.get(0).getFloor())) {
            drawPoint(currState.getStartNode(), startCircle, Color.rgb(67, 70, 76));
        }

        if (event.getFloor().equals(path.get(path.size() - 1).getFloor())) {
            drawIcon(currState.getEndNode());
        }


        hasPath = true;
    }

    private void addButton(Node current, Node next) {
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

    private void addLabel(Node node, boolean isStart) {
        if (isStart) {
            startNodeLabel = new JFXButton(node.getShortName());
            startNodeLabel.getStyleClass().add("path-button");
            startNodeLabel.setTranslateX(node.getXcoord());
            startNodeLabel.setTranslateY(node.getYcoord());
            startNodeLabel.setDisable(true);
            startNodeLabel.setOpacity(0.6);
            zoomGroup.getChildren().add(startNodeLabel);
        } else {
            endNodeLabel = new JFXButton(node.getShortName());
            endNodeLabel.getStyleClass().add("path-button");
            endNodeLabel.setTranslateX(node.getXcoord());
            endNodeLabel.setTranslateY(node.getYcoord());
            endNodeLabel.setDisable(true);
            endNodeLabel.setOpacity(0.6);
            zoomGroup.getChildren().add(endNodeLabel);
        }
    }

    private synchronized void addToList(String mapKey, Polyline polyline) {
        ArrayList<Polyline> polylines = polylineCollection.get(mapKey);

        // if list does not exist create it
        if (polylines == null) {
            polylines = new ArrayList<Polyline>();
            polylines.add(polyline);
            polylineCollection.put(mapKey, polylines);
        } else {
            // add if item is not already in list
            if (!polylines.contains(polyline)) polylines.add(polyline);
        }
    }

    private void addAnimation(Polyline line) {
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

    public void sendHelp(ActionEvent actionEvent) {
        ApplicationState currState = ApplicationState.getApplicationState();
        HelpRequest helpRequest = new HelpRequest(-1, "Need Help", currState.getDEFAULT_NODE(), false);
        helpRequest.makeRequest();
    }
}
