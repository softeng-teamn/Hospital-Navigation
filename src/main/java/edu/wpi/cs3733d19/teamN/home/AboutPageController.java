package edu.wpi.cs3733d19.teamN.home;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * controller for the about page screen
 */
public class AboutPageController {

    @FXML
    private JFXButton homeBtn ;

    /**
     * switches window to home screen
     * @throws Exception if the FXML fails to load
     */
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}