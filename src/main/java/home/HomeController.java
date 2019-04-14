package home;

import application_state.ApplicationState;
import application_state.Observer;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import map.MapController;
import application_state.Event;
import service.ResourceLoader;

import java.io.IOException;

public class HomeController implements Observer {

    private Event event;

    @FXML
    private MapViewController mapViewController;
    @FXML
    private SearchResultsController searchResultsController;
    @FXML
    private TopNavController topNavController;
    @FXML
    private Pane leftPane;

    @FXML
    void initialize() throws IOException {
        System.out.println("    Home screen initizliaed, mapview ctrler: " + mapViewController + this);
        ApplicationState.getApplicationState().getFeb().register("homeContoller", this);
        event = ApplicationState.getApplicationState().getFeb().getEvent();

        MapController.initConnections();

        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.searchResults));

    }

    @Override
    public void notify(Object newevent) {
        event = (Event) newevent;
        switch (event.getEventName()) {
            case "showText":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showText();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showSearch":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showSearch();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showAdmin":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showAdmin();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showPathSetting":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showPathSetting();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                break;
        }
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

        //event.setEventName("printText");
       // eventBus.post(event); todo here
    }

    private void showPathSetting() throws IOException {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
    }

    /*

    pane switching



    */

}