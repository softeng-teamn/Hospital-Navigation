package home;

import application_state.ApplicationState;
import application_state.Observer;
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
import service.ResourceLoader;

import java.io.IOException;

public class HomeController implements Observer {

    private Event event;

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
        ApplicationState.getApplicationState().getFeb().register("homeContoller", this);
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setDefaultStartNode();
        event.setNodeEnd(null);
        event.setIsEndNode(true);

        MapController.initConnections();

        drawer.setSidePane(drawerPane);
        drawer.setDefaultDrawerSize(480);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);
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
            case "showEmployee":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showEmployee();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break ;
            case "showPathSetting":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showPathSettings();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                  });
                  break ;
            case "closeDrawer":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawer.close();
                    }
                });
                break;
            case "logout":
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
            default:
                break;
        }
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
        //event.setEventName("printText");
        //eventBus.post(event);TODO
    }

    private void showPathSettings() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
        drawer.open();
    }
}
