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

import java.awt.*;
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
    private JFXListView<HBox> list_view;    // TODO: changed
    private HashMap<String, String> buildingAbbrev = new HashMap<>();

    private Node destNode;
    private ArrayList<Line> drawnLines = new ArrayList<Line>();
    ObservableList<Node> allNodesObservable;
    ArrayList<Node> filteredNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");
    ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();

    @FXML
    void initialize() {
        buildingAbbrev.put("Shapiro", "SHA");    // TODO: added
        buildingAbbrev.put("BTM", "BTM");
        buildingAbbrev.put("Tower", "TOW");
        buildingAbbrev.put("45 Francis", "45FR");
        buildingAbbrev.put("15 Francis", "15FR");
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
               // list_view.getSelectionModel().select(event.getNodeSelected());   // TODO what does this do?
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
            default:
                break;
        }
    }

    /**
     * Runs when user clicks a location
     * @param e
     */
    @FXML
    public void listViewClicked(MouseEvent e) {
        HBox selectedNode = list_view.getSelectionModel().getSelectedItem();  // TODO: changed
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

        // wipe old observable
        allNodesObservable = FXCollections.observableArrayList();

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

        // TODO: showing building and floor. text color need to set?
        // TODO: PRoblem: need to still be connected to node to pathfind to. or get it really fast
        // tODO: make into function
        ArrayList<HBox> hBoxes = new ArrayList<>();
        for (int i = 0; i < allNodesObservable.size(); i++) {
            Node currNode = allNodesObservable.get(i);
            HBox hb = new HBox();
            HBox inner = new HBox();
            inner.setAlignment(Pos.CENTER_RIGHT);
            Label longName = new Label(currNode.getLongName());
            String buildFlStr = buildingAbbrev.get(currNode.getBuilding()) + ", " + currNode.getFloor();
            Label buildFloor = new Label(buildFlStr);
            Label nodeId = new Label(currNode.getNodeID());
            nodeId.setPrefWidth(0);
            nodeId.setVisible(false);
            nodeId.setPadding(new Insets(0,-10,0,0));
            hb.getChildren().add(longName);
            inner.getChildren().add(nodeId);
            inner.getChildren().add(buildFloor);
            hb.getChildren().add(inner);
            hb.setHgrow(inner, Priority.ALWAYS);
            hb.setSpacing(0);
            hBoxes.add(hb);
        }
        ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
        observeHboxes.addAll(hBoxes);

        list_view.getItems().clear();
        // add to listView
        //list_view.getItems().addAll(allNodesObservable); TODO
        list_view.setItems(observeHboxes);

        // TODO: cut?
//        list_view.setCellFactory(param -> new JFXListCell<Node>() {
//            @Override
//            protected  void updateItem(Node item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null || item.getNodeID() == null ) {
//                    setText(null);
//                } else {
//                    setText(item.getLongName());
//                }
//            }
//        });
    }


    /**
     *Filters the ListView based on the string
     */
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            list_view.getItems().clear();
            // TODO: changed
            ArrayList<HBox> hBoxes = new ArrayList<>();
            for (int i = 0; i < allNodesObservable.size(); i++) {
                Node currNode = allNodesObservable.get(i);
                HBox hb = new HBox();
                HBox inner = new HBox();
                inner.setAlignment(Pos.CENTER_RIGHT);
                Label longName = new Label(currNode.getLongName());
                String buildFlStr = buildingAbbrev.get(currNode.getBuilding()) + ", " + currNode.getFloor();
                Label buildFloor = new Label(buildFlStr);
                Label nodeId = new Label(currNode.getNodeID());
                nodeId.setPrefWidth(0);
                nodeId.setVisible(false);
                nodeId.setPadding(new Insets(0,-10,0,0));
                hb.getChildren().add(longName);
                inner.getChildren().add(nodeId);
                inner.getChildren().add(buildFloor);
                hb.getChildren().add(inner);
                hb.setHgrow(inner, Priority.ALWAYS);
                hb.setSpacing(0);
                hBoxes.add(hb);
            }
            ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
            observeHboxes.addAll(hBoxes);
            list_view.getItems().addAll(observeHboxes);
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

            ArrayList<HBox> hBoxes = new ArrayList<>();
            for (int i = 0; i < filteredNodes.size(); i++) {
                Node currNode = allNodesObservable.get(i);
                HBox hb = new HBox();
                HBox inner = new HBox();
                inner.setAlignment(Pos.CENTER_RIGHT);
                Label longName = new Label(currNode.getLongName());
                String buildFlStr = buildingAbbrev.get(currNode.getBuilding()) + ", " + currNode.getFloor();
                Label buildFloor = new Label(buildFlStr);
                Label nodeId = new Label(currNode.getNodeID());
                nodeId.setPrefWidth(0);
                nodeId.setVisible(false);
                nodeId.setPadding(new Insets(0,-10,0,0));
                hb.getChildren().add(longName);
                inner.getChildren().add(nodeId);
                inner.getChildren().add(buildFloor);
                hb.getChildren().add(inner);
                hb.setHgrow(inner, Priority.ALWAYS);
                hb.setSpacing(0);
                hBoxes.add(hb);
            }
            ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
            observeHboxes.addAll(hBoxes);

            // Add to view
            list_view.getItems().clear();
            list_view.getItems().addAll(observeHboxes);
         // todo: changed   list_view.getItems().addAll(toShow);
        }
    }

    /**
     *for lists
     */
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
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