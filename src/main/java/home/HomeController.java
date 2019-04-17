package home;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import map.MapController;
import application_state.Event;
import application_state.EventBusFactory;
import service.ResourceLoader;

import java.io.IOException;

public class HomeController {

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXDrawer drawer;
    @FXML
    private MapViewController mapViewController;
    @FXML
    private SearchResultsController searchResultsController;
    @FXML
    private TopNavController topNavController;

    StackPane drawerPane = new StackPane();

    @FXML
    void initialize() throws IOException {
        eventBus.register(this);
        MapController.initConnections();
        drawer.setSidePane(drawerPane);
        drawer.setDefaultDrawerSize(480);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);
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
                        case "showEmployee":
                            showEmployee() ;
                            break ;
                        case "showPathSetting":
                            showPathSettings();
                            break;
                        case "closeDrawer":
                            drawer.close();
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
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.adminServices));
        drawer.open();
    }

    private void showEmployee() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.adminServices));
        drawer.open();
    }

    private void showSearch() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.searchResults));
        drawer.open();
    }

    private void showText() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.directionMessage));
        drawer.open();
        event.setEventName("printText");
        eventBus.post(event);
    }

    private void showPathSettings() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
        drawer.open();
    }
}