package controller;

import com.jfoenix.controls.*;
import com.sun.javafx.font.Glyph;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Edge;
import model.Elevator;
import model.MapNode;
import model.Node;
import service.DatabaseService;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.ArrayList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public class HomeController extends MapController {

    @FXML
    private VBox root, edit_VBox, edit_btn_container;
    @FXML
    private HBox top_nav, hbox_container;
    @FXML
    private JFXButton editBtn, editBtnLbl, schedulerBtn, schedulerBtnLbl, serviceBtn, serviceBtnLbl, navigate_btn, auth_btn, edit_btn, newRoom_btn, edit_save_btn, bookBtn, fulfillBtn, call_el1_btn, call_el2_btn, call_el3_btn, call_el4_btn;
    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXTextField search_bar, edit_x, edit_y, edit_floor, edit_building, edit_type, edit_long, edit_short;
    @FXML
    private Label edit_id, new_room1, new_room2, cur_el_floor;
    @FXML
    private JFXListView<Node> list_view;
    @FXML
    private FontAwesomeIconView lock_icon;

    public Group zoomGroup;
    Node restRoom = new Node("BREST00102",2177,1010,"2","45 Francis","REST","Restroom 1 Level 2","REST B0102");
    ArrayList<Node> allNodes;
    ObservableList<Node> allNodesObservable;

    private Node kioskNode = new Node("FEXIT00201",1748,1321,"1","Tower","EDIT","75 Francis Valet Drop-off","75 Francis Edit");
    private Node destNode;
    private Circle destCircle = new Circle();
    private Circle kioskCircle = new Circle();
    private int addNodeState = 0;

    static Elevator elev;

    private ArrayList<Line> drawnLines = new ArrayList<Line>();


    /**
     * pulls up the editor for user interaction
     */
    void showEditor() {
        if (top_nav.getChildren().contains(edit_btn)) {
            top_nav.getChildren().remove(edit_btn);
        }
        if (!hbox_container.getChildren().contains(edit_VBox)) {
            edit_id.setText("Node: " + destNode.getNodeID());
            edit_x.setText(String.valueOf(destNode.getXcoord()));
            edit_y.setText(String.valueOf(destNode.getYcoord()));
            edit_floor.setText(destNode.getFloor());
            edit_building.setText(destNode.getBuilding());
            edit_type.setText(destNode.getNodeType());
            edit_long.setText(destNode.getLongName());
            edit_short.setText(destNode.getShortName());
            hbox_container.getChildren().add(1, edit_VBox);
        }
    }

    /**
     * hides the editor from the screen
     */
    void hideEditor() {
        if (!top_nav.getChildren().contains(edit_btn)) {
            top_nav.getChildren().add(top_nav.getChildren().indexOf(navigate_btn)+1, edit_btn);
        }
        if (hbox_container.getChildren().contains(edit_VBox)) {
            hbox_container.getChildren().remove(edit_VBox);
        }
    }

    @FXML
    // switches window to map editor screen.
    public void showFulfillRequest() throws Exception {
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Fulfill Service Request");
    }

    public static void initializeElevator() {
        try {
            elev = Elevator.get("MyRobotName");
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    public static void initConnections() {
        System.out.println("creating hashmap ...");
        connections = new HashMap<>();
        ArrayList<Edge> allEdges = DatabaseService.getDatabaseService().getAllEdges();

        for (Edge e : allEdges) {
            if (connections.containsKey(e.getNode1().getNodeID())) {
                connections.get(e.getNode1().getNodeID()).add(e.getNode2());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode2());
                connections.put(e.getNode1().getNodeID(), newList);
            }


            if (connections.containsKey(e.getNode2().getNodeID())) {
                connections.get(e.getNode2().getNodeID()).add(e.getNode1());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode1());
                connections.put(e.getNode2().getNodeID(), newList);
            }
        }

        System.out.println("the hashmap is MADE!");
    }

    /**
     * initializes the home controller
     */
    @FXML
    void initialize() {
        initConnections();
        initializeElevator();

        // Hide the edit window
        hideEditor();

        authCheck();

        DatabaseService.getDatabaseService().registerNodeCallback(aVoid -> {
            HomeController.this.nodeChangedCallback();
            return null;
        });

        DatabaseService.getDatabaseService().registerEdgeCallback(aVoid -> {
            HomeController.this.edgeChangedCallback();
            return null;
        });

        repopulateList();

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

    private void elevCallback(Double aDouble) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // if you change the UI, do it here !
                cur_el_floor.setText("" + elev.data[0]);
                System.out.println(elev.data[0]);
            }
        });

    }

    /**
     * DatabaseService calls this when nodes are inserted, modified, deleted
     */
    private void nodeChangedCallback() {
        initConnections();
        repopulateList();
    }

    /**
     * DatabaseService calls this when edges are inserted (to
     */
    private void edgeChangedCallback() {
        initConnections();
    }
  
    void authCheck() {
        if (Controller.getIsAdmin()) {
            lock_icon.setIcon(FontAwesomeIcon.SIGN_OUT);
            edit_btn.setVisible(true);
            newRoom_btn.setVisible(true);

            if (!top_nav.getChildren().contains(edit_btn)) {
                edit_btn.setVisible(false);
                top_nav.getChildren().add(top_nav.getChildren().indexOf(navigate_btn)+1, edit_btn);
                top_nav.getChildren().add(1, newRoom_btn);
                top_nav.getChildren().add(top_nav.getChildren().indexOf(bookBtn)-1, fulfillBtn);
            }
        } else {
            System.out.println("not an admin anymore");
            lock_icon.setIcon(FontAwesomeIcon.SIGN_IN);
            if (top_nav.getChildren().contains(edit_btn)) {
                top_nav.getChildren().remove(edit_btn);
            }
            if (hbox_container.getChildren().contains(edit_VBox)) {
                hbox_container.getChildren().remove(edit_VBox);
            }
            if (top_nav.getChildren().contains(newRoom_btn)) {
                top_nav.getChildren().remove(newRoom_btn);
            }
            if (top_nav.getChildren().contains(fulfillBtn)) {
                top_nav.getChildren().remove(fulfillBtn);
            }
        }
    }

    /**
     * remove all the drawn paths
     */
    public void removeLines() {
        zoomGroup.getChildren().removeAll(drawnLines);
    }

    /**
     *for lists
     */
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    /**
     *Filters the ListView based on the string
     */
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

    /**
     * Runs when user clicks a location
     * @param e
     */
    @FXML
    public void listViewClicked(MouseEvent e) {

//        if (isAdmin) {
//            edit_id.setText("Node: " + destNode.getNodeID());
//            edit_x.setText(String.valueOf(destNode.getXcoord()));
//            edit_y.setText(String.valueOf(destNode.getYcoord()));
//            edit_floor.setText(destNode.getFloor());
//            edit_building.setText(destNode.getBuilding());
//            edit_type.setText(destNode.getNodeType());
//            edit_long.setText(destNode.getLongName());
//            edit_short.setText(destNode.getShortName());
//        }


        Node selectedNode = list_view.getSelectionModel().getSelectedItem();
        System.out.println("You clicked on: " + selectedNode.getNodeID());

        // Remove last path from screen
        removeLines();
        // clear lines cash
        drawnLines = new ArrayList<Line>();
        // Un-hide Navigation button
        navigate_btn.setVisible(true);
        if (Controller.getIsAdmin()) {
            edit_btn.setVisible(true);
        } else {
            edit_btn.setVisible(false);
        }
        // hide editor
        if (Controller.getIsAdmin()) {
            hideEditor();
        }
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

    /**
     * displays the destination node
     * @param n
     */
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

    /**
     * searches for room
     * @param e
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = search_bar.getText();
        System.out.println(search);
        filterList(search);

    }

    /**
     * switches window to map editor screen.
     * @throws Exception
     */
    @FXML
    // switches window to map editor screen.
    public void showAdminLogin() throws Exception {
        if (Controller.getIsAdmin()) {
            Controller.setIsAdmin(false);
            authCheck();
            repopulateList();
        } else {
            Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
            Stage stage = (Stage) navigate_btn.getScene().getWindow();
            StageManager.changeExistingWindow(stage, root, "Admin Login");
        }
    }

    public void showMapEditor() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Map Editor");
    }

    /**
     * switches window to request screen
     * @throws Exception
     */
    @FXML
    public void showRequest() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    /**
     * switches window to schedule screen
     * @throws Exception
     */
    @FXML
    public void showSchedule() throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
        stage.sizeToScene();
        stage.setMaximized(true);
    }

    @FXML
    void callElevatorAction(ActionEvent e) {
        System.out.println("i was just called");
        JFXButton btn = (JFXButton) e.getSource();
        char test = btn.getText().charAt(btn.getText().length()-1);
        switch (test) {
            case '1':
                elev.data[2] = 1;
                elev.data[3] = 1;
                break;
            case '2':
                elev.data[2] = 1;
                elev.data[3] = 2;
                break;
            case '3':
                elev.data[2] = 1;
                elev.data[3] = 3;
                break;
            case '4':
                elev.data[2] = 1;
                elev.data[3] = 4;
                break;
                default:
                    break;

        }
    }

    /**
     * Pathfind and show path to user
     * @param start
     * @param dest
     */
    public void pathfind(Node start, Node dest) {
        MapNode pStart = new MapNode(start.getXcoord(), start.getYcoord(), start);
        MapNode pDest = new MapNode(dest.getXcoord(), dest.getYcoord(), dest);
        PathFindingService pathFindingService = new PathFindingService();
        ArrayList<Node> path = pathFindingService.genPath(pStart, pDest).nodes;
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
    }

    /**
     * Begins the action of pathfinding
     * @param event
     */
    @FXML
    void startNavigation(ActionEvent event) {
        pathfind(kioskNode, destNode);
    }

    /**
     * zooms in the map
     * @param event
     */
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.05);
    }

    /**
     * zooms out the map
     * @param event
     */
    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.05);
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

    /**
     * function to create a new room
     * @param e
     */
    @FXML
    void newRoomAction(ActionEvent e) throws Exception {
        // open the new editor window
        Parent root = FXMLLoader.load(ResourceLoader.createNode);
        Stage mainStage = (Stage) search_bar.getScene().getWindow();
        StageManager.changeExistingWindow(mainStage, root, "Create New Room");
    }

    /**
     * pulls up the room editor
     * @param e
     * @throws IOException
     */
    @FXML
    void editAction(ActionEvent e) throws IOException {
            showEditor();
    }

    /**
     * clicking cancel button in node editor
     * @param e
     */
    @FXML
    void cancelEditAction(ActionEvent e) {
        System.out.println("Lets hide it!");
        edit_btn.setVisible(false);
        hideEditor();
    }

    /**
     * clicking the save button (after editing)
     * @param e
     */
    @FXML
    void editSaveAction(ActionEvent e) {
        System.out.println(edit_short.getText());
        // validation
        if (validateEditNode(edit_id.getText()) &&
        validateEditNode(edit_x.getText()) &&
        validateNumber(edit_x.getText()) &&
        validateEditNode(edit_y.getText()) &&
        validateNumber(edit_y.getText()) &&
        validateEditNode(edit_floor.getText()) &&
        validateEditNode(edit_type.getText()) &&
        validateEditNode(edit_long.getText()) &&
        validateEditNode(edit_short.getText())) {
            // save to send to DB
            sendEditToDB();
        }
    }

    /**
     * Function to pass on edits performed on the map to the database for future use
     */
    void sendEditToDB() {
        Node oldNode = destNode;
        Node myNode = new Node(
                destNode.getNodeID(),
                Integer.parseInt(edit_x.getText()),
                Integer.parseInt(edit_y.getText()),
                edit_floor.getText(),
                edit_building.getText(),
                edit_type.getText(),
                edit_long.getText(),
                edit_short.getText()
        );
        if (DatabaseService.getDatabaseService().updateNode(myNode)) {
            System.out.println("Here is the Old Node");
            System.out.println(oldNode);
            System.out.println("NEW NODE");
            System.out.println(myNode);
            System.out.println("Lets repopulate the list");
            insertNodeIntoList(oldNode, myNode);
        }
    }

    /**
     * probs not needed
     * @param e
     */
    @FXML
    void editNodeTextAction(ActionEvent e) {

    }

    /**
     * TBD
     * @param e
     */
    @FXML
    void nodeTextChanged(ActionEvent e) {
        System.out.println("SHIT WAS CHANGED    ");
    }

    /**
     * prompts the user to validate their Admin status
     * @param e
     */
    @FXML
    void authAction(ActionEvent e) {
        isAdmin = !isAdmin;
        authCheck();
        repopulateList();
    }

    /**
     * checks if the node to be edited exists
     * @param str
     * @return
     */
    boolean validateEditNode(String str) {
        return !str.isEmpty();
    }

    /**
     * checks if num is an integer
     * @param num
     * @return
     */
    boolean validateNumber(String num) {
        return num.matches("[0-9]+");
    }

    /**
     * populates list based on the user
     */
    void repopulateList() {
        System.out.println("Repopulation of listView");
        if (Controller.getIsAdmin()) {
            allNodes = DatabaseService.getDatabaseService().getAllNodes();
        } else {
            allNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");
        }
        // wipe old observable
        allNodesObservable = FXCollections.observableArrayList();
        // repopulate
        allNodesObservable.addAll(allNodes);
        // clear listVIEW
        if (list_view == null) {
            System.out.println("LIST VIEW IS NULL");
            return;
        }
        list_view.getItems().clear();
        // add to listView
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
    }

    /**
     * Removes a node and inserts a new node in its place.
     * @param oldN
     * @param newN
     */
    void insertNodeIntoList(Node oldN, Node newN) {
//        ArrayList<Node> repop;
        int indxOfModified = allNodes.indexOf(oldN);

        System.out.println("Removing old Node: " + allNodes.remove(oldN));
        allNodes.add(indxOfModified, newN);
        // wipe old observable
        allNodesObservable = FXCollections.observableArrayList();
        // repopulate
        allNodesObservable.addAll(allNodes);
        // clear listVIEW
        list_view.getItems().clear();
        // add to listView
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
    }

}
