import application_state.ApplicationState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

import static application_state.ApplicationState.getApplicationState;

public class LogoutEvent {

    public void showIdle() {
        ApplicationState.getApplicationState().setInactive(true);
        Stage stage = getApplicationState().getPrimaryStage();
        Parent root = null;
        try {
            root = FXMLLoader.load(ResourceLoader.idle);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        try {
            StageManager.changeExistingWindow(stage, root, "Idle");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
