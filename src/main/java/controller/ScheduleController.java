package controller;

import java.util.ArrayList;
import java.util.Collection;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

public class ScheduleController extends Controller {

    @FXML
    private JFXButton homeBtn;

    @FXML
    private VBox roomList, schedule, checks;

    @FXML
    private JFXTextField numRooms;

    private int openTime = 9; // arbitrary
    private int closeTime = 17;
    private double timeStep = 2;    // Fractions of an hour


    // !!! alter this to generate rooms based on list of rooms
    public void generateRoomList() {
        // use : dbs.getAllReservableSpaces
        int numRms = Integer.parseInt(numRooms.getText());
        for (int i = 0; i < numRms; i++)
        {
            JFXButton item = new JFXButton("item " + i);
            item.setOnAction(e -> showRoomSchedule());
            // if busy, setdisabled, change color to red, etc.
            roomList.getChildren().add(item);
        }
    }

    // On room button click, show the schedule for that room
    // !!! change to reflect data fetched from database
    public void showRoomSchedule() {
        schedule.getChildren().clear();
        checks.getChildren().clear();
        for (int i = openTime; i < closeTime; i++) {
            int time = i % 12;
            if (time == 0) {
                time = 12;
            }

            for (int j = 0; j < timeStep; j++) {
                String minutes = "00";
                if (j > 0) {
                    minutes = String.format("%.0f",(60/timeStep));
                }
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                JFXButton timeInc = new JFXButton(time + ":" + minutes);
                timeInc.setStyle("-fx-background-color: #4BC06E; ");
                hBox.getChildren().add(timeInc);
                // item.setOnAction(...);
                schedule.getChildren().add(hBox);
                JFXCheckBox check = new JFXCheckBox("Reserve Time");
                checks.getChildren().add(check);
            }
        }
    }

//    // pull unavailable times for a room & date
//    public void showAvailableTimes(String roomID, String day) {
//        Collection<String> unavailable = dbs.getRoomSched(day, roomID);    // can change Collection<String> later
//
//        ArrayList<String> allTimes = new ArrayList<>();
//        // available times
//        ArrayList<String> available = new ArrayList<>();
//        // to iterate over all possible times in a day
//        for (int i = 0; i < allTimes.size(); i += timeStep) {
//            // put in available times
//        }
//        available.removeAll(unavailable);
//
//        // UI - display things in available!! thank u :)
//    }



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
