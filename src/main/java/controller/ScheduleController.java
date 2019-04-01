package controller;

import java.util.*;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import model.Reservation;
import service.ResourceLoader;
import service.StageManager;

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
    public boolean insertReservation(Reservation reservation) {
        return dbs.insertReservation(reservation);
    }

    // pull unavailable times for a room & date
    // i think we might need to pass a room id in
    public void showAvailableTimes(String id, GregorianCalendar date) {

        //Collection<GregorianCalendar> unavailable = dbs.getRoomSched(day, roomID);

        // String, Date, Date
        // where do i get the room id??
        //List<Reservation> unavailable = dbs.getReservationsBySpaceId(id) ;

        // all possible time slots in a day (assuming room is always open, 30 minute booking periods)
//        ArrayList<GregorianCalendar> allTimes = new ArrayList<>();
//        GregorianCalendar first = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0) ;
//        allTimes.add(first) ;

        // available times - UI can work off this list
        //ArrayList<GregorianCalendar> available = new ArrayList<>();

        // to iterate over all possible times in a day
//        for (int i = 0; i < allTimes.size() /* can i just set this to loop 48 - 1 times? (for 30 min slots over 24 hrs)*/; i += timeStep) {
//            // put in available times
//
//            // new GregorianCalendar(year, month, day, second, increment hour/minute based off of previous list item)
//            int year = first.get(Calendar.YEAR) ;
//            int month = first.get(Calendar.MONTH) ;
//            int day = first.get(Calendar.DAY_OF_MONTH) ;
//            int hour = allTimes.get(i).get(Calendar.HOUR) ;
//            int minute = allTimes.get(i).get(Calendar.MINUTE + 30) ;
//            int second = allTimes.get(i).get(Calendar.SECOND) ;
//            GregorianCalendar next = new GregorianCalendar(year, month, day, hour, minute, second);
//            // i think when i set hour it will stay the same and not update as the minute would - check
//            allTimes.add(next) ;

 //       }
        //available.removeAll(unavailable);


        // UI - display things in available list of calendar dates!! thank u :)
    }

     //returns the roomID of the room asked for by the user
    String getRoom(){
        return "";
    }
    //returns the roomID of the workstations asked for by the user
    String getWorkStation(){
        return "";
    }  

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

}
