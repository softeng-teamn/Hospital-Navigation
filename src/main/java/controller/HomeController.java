package controller;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import model.EventBusFactory;
import model.HomeState;

import static controller.Controller.initConnections;

public class HomeController {

    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private MapView mapViewController;
    @FXML
    private SearchResults searchResultsController;
    @FXML
    private TopNav topNavController;

    @FXML
    void initialize() {
        initConnections();
    }

}