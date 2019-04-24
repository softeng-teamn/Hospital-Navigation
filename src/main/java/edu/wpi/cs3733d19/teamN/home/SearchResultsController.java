package edu.wpi.cs3733d19.teamN.home;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import edu.wpi.cs3733d19.teamN.application_state.Event;
import edu.wpi.cs3733d19.teamN.application_state.Observer;
import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
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
import javafx.scene.shape.Line;
import edu.wpi.cs3733d19.teamN.map.Node;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import edu.wpi.cs3733d19.teamN.scheduler.model.Reservation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controls the search bar
 */
public class SearchResultsController implements Observer {

    private Event event;    // The current event

    @FXML
    private JFXListView<HBox> list_view;    // Changed to HBox

    private HashMap<String, String> buildingAbbrev = new HashMap<>();    // Abbreviate buildings to fit in listview

    private Node destNode;
    private ArrayList<Line> drawnLines = new ArrayList<Line>();
    ArrayList<Node> allNodesObservable;    // List of nodes for listView
    ArrayList<Node> filteredNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");    // Non-admin list
    ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();
    ArrayList<Reservation> allReservation = DatabaseService.getDatabaseService().getAllReservations();
    DatabaseService myDBS;

    @FXML
    void initialize() {
        myDBS = DatabaseService.getDatabaseService();
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        buildingAbbrev.put("Shapiro", "Sha");    // Set all building abbreviations
        buildingAbbrev.put("BTM", "BTM");
        buildingAbbrev.put("Tower", "Tow");
        buildingAbbrev.put("45 Francis", "45Fr");
        buildingAbbrev.put("15 Francis", "15Fr");
        buildingAbbrev.put("RES", "RES");
        buildingAbbrev.put("FLEX", "FLEX");
        ApplicationState.getApplicationState().getObservableBus().register("searchResultsContoller",this);    // Register as observer
        repopulateList(event.isAdmin());    // Populate the list based on whether the current user is admin - can see halls
    }

    /**
     * Change what's shown based on the event
     * @param newEvent    the updated event
     */
    @Override
    public void notify(Object newEvent)  {
        event = (Event) newEvent;
        switch (event.getEventName()) {
            case "login":   // Add halls if the user is admin
                //for functions that have threading issue, use this and it will be solved
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        repopulateList(event.isAdmin());
                    }
                });
                break;
            case "search-query":    // Filter list based on user search input
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        filterList(event.getSearchBarQuery());
                    }
                });
                break;
            case "logout":    // Populate the list without halls
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
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("closeDrawer");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /**
     * Runs when user clicks a location
     * @param e FXML event that calls this method
     */
    @FXML
    public void listViewClicked(MouseEvent e) {
        // Get the current event and ApplicationState
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();

        // Get the selected/clicked item
        HBox selectedNode = list_view.getSelectionModel().getSelectedItem();

        // Get the nodeID from that item
        String ID = ((Label) ((HBox) selectedNode.getChildren().get(1)).getChildren().get(0)).getText();
        System.out.println(ID);
        String Name = DatabaseService.getDatabaseService().getNode(ID).getLongName();
        System.out.println("You clicked on: " + ID + Name);

        // Set destination node with the clicked-on item's ID
        destNode = DatabaseService.getDatabaseService().getNode(ID);

        // Tell topNav whether the start or end node was selected
        if (ApplicationState.getApplicationState().getStartEnd().equals("end")){
            event.setNodeSelected(destNode);
            currState.setEndNode(destNode);
            event.setEventName("node-select-end");
        } else {
            event.setNodeSelected(destNode);
            currState.setStartNode(destNode);
            event.setEventName("node-select-start");
        }
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);

    }

    /**
     * Repopulate the list based on whether the employee is admin.
     * @param isAdmin    true if the employee is admin otherwise false
     */
    private void repopulateList(boolean isAdmin) {

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

        ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodesObservable, allReservation, false);

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
            ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodesObservable, allReservation, false);
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
            List<ExtractedResult> filteredRes = FuzzySearch.extractSorted(findStr, convertList(orginalRes,  Reservation::getEventName), 75);

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
            ObservableList<HBox> observeHboxes = makeIntoHBoxes((ArrayList)filteredNodes, (ArrayList)filteredReservation, true);

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
    private ObservableList<HBox> makeIntoHBoxes(ArrayList<Node> nodes, ArrayList<Reservation> reservations, Boolean displayEvents) {
        ArrayList<HBox> hBoxes = new ArrayList<>();
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
        if (displayEvents) {
            for (int i = 0; i < reservations.size(); i++) {    // For every public event
                Reservation currRes = reservations.get(i);
                if (currRes.getPrivacyLevel() == 0) {
                    HBox hb = new HBox();
                    HBox inner = new HBox();    // So the building can be right-aligned
                    inner.setAlignment(Pos.CENTER_RIGHT);
                    Label longName = new Label("Event: " + currRes.getEventName());    // Make a label for the long name
                    String buildFlStr = "FlWk, 4";
                    //String buildFlStr = buildingAbbrev.get(myDBS.getNode(currRes.getLocationID()).getBuilding()) + ", " + myDBS.getNode(currRes.getLocationID()).getFloor();
                    Label buildFloor = new Label(buildFlStr);    // Make a label for the building abbreviation and floor
                    Label nodeId = new Label(myDBS.getReservableSpace(currRes.getLocationID()).getLocationNodeID());    // Save the nodeID for pathfinding but make it invisible
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
        }

        ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
        observeHboxes.addAll(hBoxes);
        return observeHboxes;
    }

}