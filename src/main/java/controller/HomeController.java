package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Event;
import model.EventBusFactory;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static controller.Controller.initConnections;

public class HomeController {

    private Event event = EventBusFactory.getEvent();
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
    void initialize() throws IOException {
        eventBus.register(this);

        initConnections();

        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.searchResults));


    }



    @Subscribe
    private void eventListener(Event newevent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (event.getEventName()) {
                        case "showText":
                            showText();
                            break;
                        case "showSearch":
                            showSearch();
                            break;
                        case "showAdmin":
                            showAdmin();
                            break;
                        case "showPathSetting":
                            showPathSetting();
                            break;
                        default:
                            break;
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAdmin() throws IOException {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.adminServices));
    }

    private void showSearch() throws IOException {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.searchResults));
    }

    private void showText() throws IOException {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.directionMessage));
        event.setEventName("printText");
        eventBus.post(event);
    }

    private void showPathSetting() throws IOException {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
    }
}