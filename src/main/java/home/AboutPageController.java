package home;

import application_state.ApplicationState;
import application_state.Event;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

/**
 * controller for the about page screen
 */
public class AboutPageController {

    @FXML
    private JFXButton homeBtn ;

    private Event event;

    void initialize() throws IOException{
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
    }

    /**
     * switches window to home screen
     * @throws Exception if the FXML fails to load
     */
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}
