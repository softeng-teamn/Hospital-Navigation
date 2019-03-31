package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import service.ResourceLoader;
import service.StageManager;

import java.util.GregorianCalendar ;

import service.DatabaseService;

import java.io.IOException;

import static java.util.Calendar.JUNE;

public class ScheduleController extends Controller {


    @FXML
    private JFXButton homeBtn;
    private int timeStep = 30;     // schedule time increment

    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    //asks the database to update a room schedule for a particular room
    //returns true if success
    // assume booking has proper day & time info (mmddyy, hhmmss)
    public boolean bookRoom(String roomID, String day, String time) {
        return dbs.bookRoom(roomID, day, time);
    }

    // pull unavailable times for a room & date
    public void showAvailableTimes(GregorianCalendar date) {
//        Collection<GregorianCalendar> unavailable = dbs.getRoomSched(day, roomID);    // can change Collection<String> later



        ArrayList<String> allTimes = new ArrayList<>();
        // available times
        ArrayList<String> available = new ArrayList<>();

        // to iterate over all possible times in a day
        for (int i = 0; i < allTimes.size(); i += timeStep) {
            // put in available times
        }
   //     available.removeAll(unavailable);

        // UI - display things in available!! thank u :)
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

}
