package home;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

public class AboutPageController {

    @FXML
    private JFXButton homeBtn ;

    /**
     * switches window to home screen
     * @throws Exception
     */
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}
