package controller;

import java.util.ArrayList;
import java.util.Collection;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

public class ScheduleController extends Controller {

    @FXML
    private JFXButton homeBtn;

    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    //returns a list of roomIDs which have a max capacity of less than nPeople
    ArrayList<String> getMaxPeople(int nPeople){
        ArrayList<String> a = new ArrayList<>();
        return a;
    }

    //returns a string representing a day asked for by the user
    //in format mmddyyyy
    String getDay(){
        return "";
    }

    //returns the roomID of the room asked for by the user
    String getRoom(){
        return "";
    }

    //returns the roomID of the workstations asked for by the user
    String getWorkStation(){
        return "";
    }

    //asks the database for available times for a particular room on a particular day
    //in format startTime-endTime;startTime-endTime; and so on
    //start and end time are hh:mm
    String getRoomSched(String roomID, String day){
        return "";
    }

    //asks the database to update a room schedule for a particular room
    //returns true if success
    boolean bookRoom(String roomID, String day, String time){
        return false;
    }

}
