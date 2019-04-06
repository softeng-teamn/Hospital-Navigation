package controller;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import model.HomeState;

public class HomeController {

    private HomeState state;
    public EventBus eventBus;

    @FXML
    private MapView mapViewController;
    @FXML
    private SearchResults searchResultsController;
    @FXML
    private TopNav topNavController;

    @FXML
    void initialize() {
        eventBus = new EventBus();


    }

}