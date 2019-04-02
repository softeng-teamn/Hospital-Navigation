package controller;

import java.time.LocalDateTime;
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
//import sun.util.calendar.Gregorian;

import java.io.IOException;

import static java.util.Calendar.*;

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



    // pull unavailable times for a room & date from database, return available times
     ArrayList<GregorianCalendar> getAvailableTimes(String id, GregorianCalendar date) {

        // correct time zone to set
        TimeZone tz = TimeZone.getTimeZone("GMT");

        // set date
        date.setTimeZone(tz) ;

        // get unavailable reservations from database
        List<Reservation> unavailableReservations = dbs.getReservationsBySpaceId(id);


        // fields that will be incremented to generate all possible times
        int hour = 0;
        int minute = 0;

        // list of all possible times, will be filtered out to contain only available times
        ArrayList<GregorianCalendar> allPossTimes = new ArrayList<>();

        // make all available times by time step increment
        // loop 48 times (30 minute increments over 24 hours)
        System.out.println("LIST OF ALL POSS TIMES");
        for (int i = 0; i < 48; i++) {

            // create new GC object
            GregorianCalendar addGC = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), hour, minute, date.get(Calendar.SECOND));
            // set the time zone
            addGC.setTimeZone(tz) ;
            // add to list
            allPossTimes.add(addGC);

            // if time is at the half hour mark, reset minutes to 0, increment hour
            if (minute == timeStep) {
                minute = 0;
                hour += 1;

            } else {
                minute += timeStep;

            }
            System.out.println(allPossTimes.get(i).toInstant());
        }

        // remove unavailable times in for loop over unavailable reservations
        for (int i = 0; i < unavailableReservations.size(); i++) {

            // Create a new Reservation object with SET TIMES (so i can retrieve from this object and not keep referencing the incorrect time (zone) of the original)
            Reservation newRes = new Reservation(unavailableReservations.get(i).getEventID(),
                    unavailableReservations.get(i).getPrivacyLevel(),
                    unavailableReservations.get(i).getEmployeeId(),
                    unavailableReservations.get(i).getEventName(),
                    unavailableReservations.get(i).getLocationID(),
                    unavailableReservations.get(i).getStartTime(),
                    unavailableReservations.get(i).getEndTime() );
            // create new GC objects for same purpose as above
            //GregorianCalendar start = (unavailableReservations.get(i).getStartTime()) ;
            //GregorianCalendar end = (unavailableReservations.get(i).getEndTime()) ;
            GregorianCalendar start = (newRes.getStartTime()) ;
            GregorianCalendar end = (newRes.getEndTime()) ;
            // set to correct time zone
            start.setTimeZone(tz);
            end.setTimeZone(tz);

            System.out.println("Times for reservations");
            System.out.println(start.toInstant());
            System.out.println(end.toInstant()) ;

            // set the hour and the minute for start
            int hour2 = start.get(HOUR);
            int minute2 = start.get(MINUTE);

            // while the start time is less than the end time
            while (start.compareTo(end) < 0) {

                // remove start time from list
                allPossTimes.remove(start);

                // increment hour and reset minutes if at 30 minute slot
                if (minute2 == timeStep) {
                    minute2 = 0;
                    hour2 += 1;
                    start.set(MINUTE, minute2);
                    start.set(HOUR, hour2);

                } else {
                    minute2 = 30;
                    start.set(MINUTE, minute2);
                }
            }
        }

        // at this point allPossTimes is filtered to only available times - ready to use!
        return allPossTimes;

        // UI - display things in available list of calendar dates!! thank u :)
    }


    //returns the roomID of the room asked for by the user
    String getRoom() {
        return "";
    }

    //returns the roomID of the workstations asked for by the user
    String getWorkStation() {
        return "";
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

}
