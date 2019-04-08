package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.*;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class SearchResults {

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();


    @FXML
    private JFXListView<Node> list_view;

    private Node destNode;
    private ArrayList<Line> drawnLines = new ArrayList<Line>();
    ObservableList<Node> allNodesObservable;
    ArrayList<Node> filteredNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");
    ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();



    @FXML
    void initialize() {

        eventBus.register(this);


        repopulateList(event.isAdmin());
    }

    @Subscribe
    private void eventListener(Event newEvent) throws InterruptedException {
        // set new event
        event = newEvent;
        switch (event.getEventName()) {
            case "node-select":
                list_view.scrollTo(event.getNodeSelected());
                list_view.getSelectionModel().select(event.getNodeSelected());
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
        Node selectedNode = list_view.getSelectionModel().getSelectedItem();
        System.out.println("You clicked on: " + selectedNode.getLongName());


        // set destination node
        destNode = selectedNode;

        event.setNodeSelected(destNode);
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


        // repopulate

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