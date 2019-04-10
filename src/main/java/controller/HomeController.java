package controller;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.EventBusFactory;
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
    private Pane leftPane;

    @FXML
    void initialize() {
        initConnections();

    }

    /*

    pane switching

    */

}