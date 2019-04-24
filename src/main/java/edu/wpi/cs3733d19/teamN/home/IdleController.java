package edu.wpi.cs3733d19.teamN.home;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;

public class IdleController {

    @FXML
    private JFXButton homeBtn ;

    public void showHome() throws Exception{
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}
