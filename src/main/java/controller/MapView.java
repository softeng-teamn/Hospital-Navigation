package controller;

import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.HomeState;

import java.util.Observable;
import java.util.Observer;

public class MapView {

    void initialize() {

    }

    // ELEVATOR CALL BUTTONS
    @FXML
    void callElevatorAction(ActionEvent e) {

    }

    @Subscribe
    public void homeStateEvent(HomeState state) {
        System.out.println("map view recieved the state");
    }

}
