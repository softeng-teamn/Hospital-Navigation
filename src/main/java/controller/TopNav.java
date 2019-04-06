package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.Event;
import model.EventBusFactory;
import model.HomeState;
import model.LoginEvent;

public class TopNav {

    private Event event;
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    void showAdminLogin(ActionEvent e) {
        // Creating a new event
        event.setEventName("admin-login");
        // Make change
        event.setAdmin(false);
        // share to the world!
        eventBus.post(event);
    }

    @FXML
    void showFulfillRequest(ActionEvent e) {

    }

    @FXML
    void showSchedule(ActionEvent e) {

    }


    @FXML
    void showRequest(ActionEvent e) {

    }

    @Subscribe
    void eventListener(Event event) {

        this.event = event;
    }

    @FXML
    void initialize() {
        eventBus.register(this);
    }

    void handleFindPath() {

    }


}
