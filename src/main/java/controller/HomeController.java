package controller;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import model.EventBusFactory;
import model.HomeState;

public class HomeController {

    public EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private MapView mapViewController;
    @FXML
    private SearchResults searchResultsController;
    @FXML
    private TopNav topNavController;

    @FXML
    void initialize() {

        eventBus.register(this);

    }


}