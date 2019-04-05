package controller;

import com.jfoenix.controls.JFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import model.HomeState;
import model.Node;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SearchResults implements Observer {


    public HomeState state;
    @FXML
    private JFXListView<Node> list_view;

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


//        Node selectedNode = list_view.getSelectionModel().getSelectedItem();
//        System.out.println("You clicked on: " + selectedNode.getNodeID());
//
//        // Remove last path from screen
//        removeLines();
//        // clear lines cash
//        drawnLines = new ArrayList<Line>();
//        // Un-hide Navigation button
//        navigate_btn.setVisible(true);
//        if (Controller.getIsAdmin()) {
//            edit_btn.setVisible(true);
//        } else {
//            edit_btn.setVisible(false);
//        }
//        // hide editor
//        if (Controller.getIsAdmin()) {
//            hideEditor();
//        }
//        // set destination node
//        destNode = selectedNode;
//
//        // Draw Circle on Map
//        showDestination(selectedNode);
//
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
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
