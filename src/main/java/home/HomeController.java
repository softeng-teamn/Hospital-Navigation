package home;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import map.MapController;
import service.ResourceLoader;

import java.io.IOException;

/**
 * Controls the main screen of the application
 */
public class HomeController implements Observer {

    private Event event;

    @FXML
    private JFXDrawer drawer;

    StackPane drawerPane = new StackPane();

    /** initializes the main screen
     * @throws IOException if the FXML fails to load
     */
    @FXML
    void initialize() throws IOException {
        // Get the current info and register this as observer
        ApplicationState.getApplicationState().getObservableBus().register("homeController", this);
        ApplicationState currState = ApplicationState.getApplicationState();
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        currState.setDefaultStartNode();    // Set the default start
        currState.setEndNode(null);
        MapController.initConnections();

        drawer.setSidePane(drawerPane);
        drawer.setDefaultDrawerSize(480);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);
    }

    /** handles an observed event and changes the screen as appropriate
     * @param newevent event to respond to from the observed class
     */
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
            case "showSearch-start":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationState.getApplicationState().setStartEnd("start");
                        try {
                            showSearch();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showSearch-end":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationState.getApplicationState().setStartEnd("end");
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
                        drawer.close();
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
    }

    private void showPathSettings() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
        drawer.open();
    }
}
