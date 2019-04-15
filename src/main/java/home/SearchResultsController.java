package home;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import application_state.Event;
import application_state.EventBusFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import map.Node;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import database.DatabaseService;
import scheduler.model.Reservation;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class SearchResultsController {

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();


    @FXML
    private JFXListView<HBox> list_view;    // Changed to HBox
    private HashMap<String, String> buildingAbbrev = new HashMap<>();    // Abbreviate buildings to fit in listview

    private Node destNode;
    private ArrayList<Line> drawnLines = new ArrayList<Line>();
    ArrayList<Node> allNodesObservable;    // Changed to ArrayList
    ArrayList<Node> filteredNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");
    ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();
    ArrayList<Reservation> allReservation = DatabaseService.getDatabaseService().getAllReservations();
    DatabaseService myDBS;
    boolean displayNodes = false;

    @FXML
    void initialize() {
        myDBS = DatabaseService.getDatabaseService();
        buildingAbbrev.put("Shapiro", "Sha");    // Set all building abbreviations
        buildingAbbrev.put("BTM", "BTM");
        buildingAbbrev.put("Tower", "Tow");
        buildingAbbrev.put("45 Francis", "45Fr");
        buildingAbbrev.put("15 Francis", "15Fr");
        buildingAbbrev.put("RES", "RES");
        eventBus.register(this);
        repopulateList(event.isAdmin());
    }

    @Subscribe
    private void eventListener(Event newEvent) throws InterruptedException {
        // set new event
        event = newEvent;
        switch (event.getEventName()) {
            case "node-select":
                //list_view.scrollTo(event.getNodeSelected());
               // list_view.getSelectionModel().select(event.getNodeSelected());   // TODO what does this do? I commented it out
                break;
            case "login":
                //for functions that have threading issue, use this and it will be solved
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        repopulateList(event.isAdmin());
                    }
                });
                break;
            case "search-query":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        filterList(event.getSearchBarQuery());
                    }
                });
                break;
            case "logout":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        repopulateList(event.isAdmin());
                    }
                });
                break;
            default:
                break;
        }
    }

    @FXML
    void closeDrawer(ActionEvent e) {
        event.setEventName("closeDrawer");
        eventBus.post(event);
    }

    /**
     * Runs when user clicks a location
     * @param e
     */
    @FXML
    public void listViewClicked(MouseEvent e) {
        HBox selectedNode = list_view.getSelectionModel().getSelectedItem();
        String ID = ((Label) ((HBox) selectedNode.getChildren().get(1)).getChildren().get(0)).getText();
        System.out.println("You clicked on: " + ID);


        // set destination node
        destNode = DatabaseService.getDatabaseService().getNode(ID);

        if (event.isEndNode()){
            event.setNodeSelected(destNode);
        } else {
            event.setNodeStart(destNode);
        }
        event.setEventName("node-select");
        eventBus.post(event);

    }

    void repopulateList(boolean isAdmin) {

        System.out.println("Repopulation of listView" + isAdmin);

        allNodes = myDBS.getAllNodes();
        allReservation = myDBS.getAllReservations();
        filteredNodes = (ArrayList<Node>) myDBS.getNodesFilteredByType("STAI", "HALL").stream().filter((n) -> !n.isClosed()).collect(Collectors.toList());

        // wipe old observable
        allNodesObservable = new ArrayList<>();

        if (isAdmin) {
            allNodesObservable.addAll(allNodes);
        } else {
            allNodesObservable.addAll(filteredNodes);
        }

        Collections.sort(allNodesObservable, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getLongName().compareTo(o2.getLongName());
            }
        });

        // clear listVIEW
        if (list_view == null) {
            System.out.println("LIST VIEW IS NULL");
            return;
        }

        // TODO: can change to full building name. or CAPS. or change alignment or coloring.
        ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodesObservable, allReservation);

        list_view.getItems().clear();
        // add to listView
        list_view.setItems(observeHboxes);
    }


    /**
     *Filters the ListView based on the string
     */
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            list_view.getItems().clear();
            ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodesObservable, allReservation);
            list_view.getItems().addAll(observeHboxes);
        }
        else {
            //Get List of all nodes
            ObservableList<Node> original = FXCollections.observableArrayList();
            original.addAll(allNodesObservable);

            ObservableList<Reservation> orginalRes = FXCollections.observableArrayList();
            orginalRes.addAll(allReservation);

            //Get Sorted list of nodes based on search value
            List<ExtractedResult> filtered = FuzzySearch.extractSorted(findStr, convertList(original, Node::getLongName),75);
            List<ExtractedResult> filteredRes = FuzzySearch.extractSorted(findStr, convertList(orginalRes, Reservation::getEventName), 75);

            // Map to nodes based on index
            Stream<Node> stream = filtered.stream().map(er -> {
                return original.get(er.getIndex());
            });

            Stream<Reservation> streamRes  = filteredRes.stream().map(er -> {
                return orginalRes.get(er.getIndex());
            });

            // Convert to list and then to observable list
            List<Node> filteredNodes = stream.collect(Collectors.toList());
            List<Reservation> filteredReservation = streamRes.collect(Collectors.toList());
            ObservableList<HBox> observeHboxes = makeIntoHBoxes((ArrayList)filteredNodes, (ArrayList)filteredReservation);

            // Add to view
            list_view.getItems().clear();
            list_view.getItems().addAll(observeHboxes);
        }
    }

    /**
     *for lists
     */
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    /**
     * Make the passed in arraylist into an observable list of hboxes with name, building, floor
     * to put into the listview
     * @param nodes the list of nodes to display
     * @return the list of hboxes, one for each node
     */
    private ObservableList<HBox> makeIntoHBoxes(ArrayList<Node> nodes, ArrayList<Reservation> reservations) {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        if(displayNodes) {
            for (int i = 0; i < nodes.size(); i++) {    // For every node
                Node currNode = nodes.get(i);
                HBox hb = new HBox();
                HBox inner = new HBox();    // So the building can be right-aligned
                inner.setAlignment(Pos.CENTER_RIGHT);
                Label longName = new Label(currNode.getLongName());    // Make a label for the long name
                String buildFlStr = buildingAbbrev.get(currNode.getBuilding()) + ", " + currNode.getFloor();
                Label buildFloor = new Label(buildFlStr);    // Make a label for the building abbreviation and floor
                Label nodeId = new Label(currNode.getNodeID());    // Save the nodeID for pathfinding but make it invisible
                nodeId.setPrefWidth(0);
                nodeId.setVisible(false);
                nodeId.setPadding(new Insets(0, -10, 0, 0));
                hb.getChildren().add(longName);    // Add the node name
                inner.getChildren().add(nodeId);
                inner.getChildren().add(buildFloor);    // Add the ID and building and floor to the right-aligned hbox
                hb.getChildren().add(inner);    // Combine them
                hb.setHgrow(inner, Priority.ALWAYS);
                hb.setSpacing(0);
                hBoxes.add(hb);    // Add it all to the list
            }
        }
        else if (!(displayNodes)) {
            for (int i = 0; i < reservations.size(); i++) {    // For every node
                Reservation currRes = reservations.get(i);
                HBox hb = new HBox();
                HBox inner = new HBox();    // So the building can be right-aligned
                inner.setAlignment(Pos.CENTER_RIGHT);
                Label longName = new Label(currRes.getEventName());    // Make a label for the long name
                String buildFlStr = "";
                //String buildFlStr = buildingAbbrev.get(myDBS.getNode(currRes.getLocationID()).getBuilding()) + ", " + myDBS.getNode(currRes.getLocationID()).getFloor();
                Label buildFloor = new Label(buildFlStr);    // Make a label for the building abbreviation and floor
                Label nodeId = new Label(currRes.getLocationID());    // Save the nodeID for pathfinding but make it invisible
                nodeId.setPrefWidth(0);
                nodeId.setVisible(false);
                nodeId.setPadding(new Insets(0, -10, 0, 0));
                hb.getChildren().add(longName);    // Add the node name
                inner.getChildren().add(nodeId);
                inner.getChildren().add(buildFloor);    // Add the ID and building and floor to the right-aligned hbox
                hb.getChildren().add(inner);    // Combine them
                hb.setHgrow(inner, Priority.ALWAYS);
                hb.setSpacing(0);
                hBoxes.add(hb);    // Add it all to the list
            }
        }
        ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
        observeHboxes.addAll(hBoxes);
        return observeHboxes;
    }

}




// old code to show the selected location on the map and auto scroll to that
//        // Draw Circle on Map
//        showDestination(selectedNode);
//        // animation scroll to new position
//        double mapWidth = zoomGroup.getBoundsInLocal().getWidth();
//        double mapHeight = zoomGroup.getBoundsInLocal().getHeight();
//        double scrollH = (Double) (selectedNode.getXcoord() / mapWidth);
//        double scrollV = (Double) (selectedNode.getYcoord() / mapHeight);
//        final Timeline timeline = new Timeline();
//        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
//        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
//        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv1, kv2);
//        timeline.getKeyFrames().add(kf);
//        timeline.play();