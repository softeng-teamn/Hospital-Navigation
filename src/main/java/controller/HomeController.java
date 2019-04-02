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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.MapNode;
import model.Node;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomeController extends MapController {

    @FXML
    private VBox root;
    @FXML
    private JFXButton editBtn, editBtnLbl, schedulerBtn, schedulerBtnLbl, serviceBtn, serviceBtnLbl, navigate_btn;
    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private JFXListView<Node> list_view;


    public Group zoomGroup;
    Node restRoom = new Node("BREST00102",2177,1010,"2","45 Francis","REST","Restroom 1 Level 2","REST B0102");
    ArrayList<Node> allNodes;
    ObservableList<Node> allNodesObservable;

    private Node kioskNode = new Node("ARETL00101",1619,2522,"1","BTM","RETL","Cafe","Cafe");
    private Node destNode;
    private Circle destCircle = new Circle();
    private Circle kioskCircle = new Circle();

    private ArrayList<Line> drawnLines = new ArrayList<Line>();

    @FXML
    void initialize() {

        allNodes = dbs.getNodesFilteredByType("STAI", "HALL");

        // Create NodeList
        allNodesObservable = FXCollections.observableArrayList();
        allNodesObservable.addAll(allNodes);

        // Initialize list_view
        list_view.getItems().addAll(allNodesObservable);
        list_view.setCellFactory(param -> new JFXListCell<Node>() {
            @Override
            protected  void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNodeID() == null ) {
                    setText(null);
                } else {
                    setText(item.getLongName());
                }
            }
        });

        // MAKE NAVIGATION BUTTON INVISIBLE
        navigate_btn.setVisible(false);

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);

        // Setting Up Circle Destination Point
        kioskCircle.setCenterX(kioskNode.getXcoord());
        kioskCircle.setCenterY(kioskNode.getYcoord());
        destCircle.setRadius(20);
        kioskCircle.setRadius(20);
        destCircle.setFill(Color.TRANSPARENT);
        kioskCircle.setFill(Color.BLUE);
        zoomGroup.getChildren().add(destCircle);
        zoomGroup.getChildren().add(kioskCircle);


        // Setting View Scrolling
        zoom_slider.setMin(0.3);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.3);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.3);
    }

    public void removeLines() {
        zoomGroup.getChildren().removeAll(drawnLines);
    }

    //for lists
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    // Filters the ListView based on the string
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            list_view.getItems().clear();
            list_view.getItems().addAll(allNodesObservable);
        }
        else {
            //Get List of all nodes
            ObservableList<Node> original = allNodesObservable;

            //Get Sorted list of nodes based on search value
            List<ExtractedResult> filtered = FuzzySearch.extractSorted(findStr, convertList(original, Node::getLongName),75);

            // Map to nodes based on index
            Stream<Node> stream = filtered.stream().map(er -> {
               return original.get(er.getIndex());
            });

            // Convert to list and then to observable list
            List<Node> filteredNodes = stream.collect(Collectors.toList());
            ObservableList<Node> toShow = FXCollections.observableList(filteredNodes);

            // Add to view
            list_view.getItems().clear();
            list_view.getItems().addAll(toShow);
        }
    }

    @FXML
    public void listViewClicked(MouseEvent e) {
        Node selectedNode = list_view.getSelectionModel().getSelectedItem();
        System.out.println("You clicked on: " + selectedNode.getNodeID());

        // Remove last path from screen
        removeLines();
        // clear lines cash
        drawnLines = new ArrayList<Line>();
        // Un-hide Navigation button
        navigate_btn.setVisible(true);
        // set destination node
        destNode = selectedNode;

        // Draw Circle on Map
        showDestination(selectedNode);

        // animation scroll to new position
        double mapWidth = zoomGroup.getBoundsInLocal().getWidth();
        double mapHeight = zoomGroup.getBoundsInLocal().getHeight();
        double scrollH = (Double) (selectedNode.getXcoord() / mapWidth);
        double scrollV = (Double) (selectedNode.getYcoord() / mapHeight);
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void showDestination(Node n) {
        destCircle.setCenterX(n.getXcoord());
        destCircle.setCenterY(n.getYcoord());
        destCircle.setFill(Color.GREEN);
    }

    // Later Todos :
    //      Generate Fixed x,y position that the kiosk is at
    //      Copy google maps in Finding path
    //      Call Find path and Draw Line
    //      Clear Screen

    @FXML
    // searches for Room
    public void searchBarEnter(ActionEvent e) {
        String search = search_bar.getText();
        System.out.println(search);
        filterList(search);

    }

    @FXML
    // switches window to map editor screen.
    public void showMapEditor() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to request screen
    public void showRequest() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    @FXML
    // switches window to schedule screen
    public void showSchedule() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
    }
  
    //Pathfind and show path to user
    public void pathfind(Node start, Node dest) {
        MapNode pStart = new MapNode(start.getXcoord(), start.getYcoord(), start);
        MapNode pDest = new MapNode(dest.getXcoord(), dest.getYcoord(), dest);
        PathFindingService pathFindingService = new PathFindingService();
        ArrayList<Node> path = pathFindingService.genPath(pStart, pDest);
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
                drawnLines.add(line);
                last = current;
            }
        } else {
            // draw NOTHING
            System.out.println("we have a path with 1 node. Is the destination & start the same???");
        }
//        for (int i=0; i<path.size(); i++){
//            Line line = new Line(path.get(i).getXcoord(), path.get(i).getYcoord(),
//                                 path.get(i++).getXcoord(), path.get(i++).getYcoord());
//            line.getEndX();
//            zoomGroup.getChildren().add(line);
            //Delete this line, I just put it here to appease spotBugs
            //Nathan here, I don't know the specifics of how our UI system works.
            //Thus, the below lines are commented until I learn how to interface with it.
            //IF you uncomment it, then it will simply draw the path on a white background.
            //NOTE: THIS DOES NOT SUPPORT A DYNAMICALLY MOVING PATH (yet)
            //Group root = new Group();
            //Scene scene = new Scene(root, 1920, 1080, Color.WHITE);
            //root.getChildren.add(line);
            //stage.setScene(scene);
            //stage.show();
//        }
    }

    @FXML
    void startNavigation(ActionEvent event) {
//          ArrayList<Node> connectedNodes = dbs.getNodesConnectedTo(destNode);
//        System.out.println(connectedNodes);
//        System.out.println(dbs.getAllEdges());
//        System.out.println(dbs.getAllNodes());
//            Line line = new Line();
//            line.setStartX(kioskCircle.getCenterX());
//            line.setEndX(destCircle.getCenterX());
//            line.setStartY(kioskCircle.getCenterY());
//            line.setEndY(destCircle.getCenterY());
//            line.setFill(Color.BLACK);
//            line.setStrokeWidth(10.0);
//            zoomGroup.getChildren().add(line);
        pathfind(kioskNode, destNode);
    }

    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.05);
    }

    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.05);
    }

    private void zoom(double scaleValue) {
//    System.out.println("airportapp.Controller.zoom, scaleValue: " + scaleValue);
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

}
