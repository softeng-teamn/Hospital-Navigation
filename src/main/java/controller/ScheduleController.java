package controller;

import java.util.ArrayList;
import java.util.Collection;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.mockito.Mock;
import service.ResourceLoader;
import service.StageManager;
import service.DatabaseService;
import org.mockito.Mockito;

import java.io.IOException;

public class ScheduleController extends Controller {
    DatabaseService dbs;

    @FXML
    private JFXButton homeBtn;

    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    //asks the database to update a room schedule for a particular room
    //returns true if success
    public boolean bookRoom(String roomID, String day, String time){
        return dbs.bookRoom(roomID, day, time);
    }

}
