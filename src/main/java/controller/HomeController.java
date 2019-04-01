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
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import model.MapNode;
import model.Node;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Collection;

public class HomeController extends MapController {

    @FXML
    private JFXButton editBtn, editBtnLbl, schedulerBtn, schedulerBtnLbl, serviceBtn, serviceBtnLbl;
    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private JFXListView<Node> list_view;

    public Group zoomGroup;

    Node n1 = new Node("n1",0,1);
    Node n2 = new Node("n2",0,1);
    Node n3 = new Node("n3",20,200);
    Node n4 = new Node("n4",0,1);
    Node n5 = new Node("n5",0,1);
    Node n6 = new Node("n6",0,1);
    Node n7 = new Node("n7",0,1);
    ArrayList<Node> allNodes = new ArrayList<>();
    ObservableList<Node> allNodesObservable;

    @FXML
    void initialize() {


        // THIS IS ONLY FOR MOCKING THE DATABASE "getAllNodes" METHOD
//        ArrayList<Node> nodes = dbs.getAllNodes();
        allNodes.add(n1);
        allNodes.add(n2);
        allNodes.add(n3);
        allNodes.add(n4);

        // Create NodeList
        allNodesObservable = FXCollections.observableArrayList();
        allNodesObservable.addAll(allNodes);

        // Initialize list_view
        list_view.getItems().addAll(allNodesObservable);
        list_view.setCellFactory(param -> new JFXListCell<Node>() {
            @Override
            protected  void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNodeID() == null) {
                    setText(null);
                } else {
                    setText(item.getNodeID());
                }
            }
        });


        System.out.println("We are running our init");
        zoom_slider.setMin(0.3);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.3);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
    }

    // Filters the ListView based on the string
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            list_view.getItems().addAll(allNodesObservable);
        } else {
            ObservableList<Node> original = list_view.getItems();
            ObservableList<Node> filtered = FXCollections.observableArrayList();
            for (Node n : original) {
                if (n.getNodeID().contains(findStr)) {
                    filtered.add(n);
                }
            }
            // NO ITEMS TO SHOW
            if (filtered.size() < 1) {
                System.out.println("do we get here?");
                list_view.getItems().clear();
            } else {
                list_view.getItems().clear();
                list_view.getItems().addAll(filtered);
            }
        }
    }

    @FXML
    public void listViewClicked(MouseEvent e) {
        // SHOW THE X & Y COORDS of the selected node on the map!
        Node selectedNode = list_view.getSelectionModel().getSelectedItem();
        System.out.println("You clicked on: " + selectedNode.getNodeID());

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

    private void drawNode(Node n) {

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
        Stage stage = (Stage) editBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeExistingWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to request screen
    public void showRequest() throws Exception {
        Stage stage = (Stage) serviceBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    @FXML
    // switches window to schedule screen
    public void showSchedule() throws Exception {
        Stage stage = (Stage) schedulerBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
    }
  
    //Pathfind and show path to user
    public void pathfind(Node start, Node dest) {
        MapNode pStart = new MapNode(start.getXcoord(), start.getYcoord(), start);
        MapNode pDest = new MapNode(dest.getXcoord(), dest.getYcoord(), dest);
        PathFindingService pathFindingService = new PathFindingService();
        ArrayList<Node> path;
        path = pathFindingService.genPath(pStart, pDest);
        for (int i=0; i<path.size(); i++){
            Line line = new Line(path.get(i).getXcoord(), path.get(i).getYcoord(),
                                 path.get(i++).getXcoord(), path.get(i++).getYcoord());
            line.getEndX(); //Delete this line, I just put it here to appease spotBugs
            //Nathan here, I don't know the specifics of how our UI system works.
            //Thus, the below lines are commented until I learn how to interface with it.
            //IF you uncomment it, then it will simply draw the path on a white background.
            //NOTE: THIS DOES NOT SUPPORT A DYNAMICALLY MOVING PATH (yet)
            //Group root = new Group();
            //Scene scene = new Scene(root, 1920, 1080, Color.WHITE);
            //root.getChildren.add(line);
            //stage.setScene(scene);
            //stage.show();
        }
    }

    @FXML
    void zoomIn(ActionEvent event) {
    System.out.println("airportapp.Controller.zoomIn");
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.05);
    }

    @FXML
    void zoomOut(ActionEvent event) {
    System.out.println("airportapp.Controller.zoomOut");
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
