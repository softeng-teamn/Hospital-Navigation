package controller;

import java.util.ArrayList;
import java.util.Collection;

public class ScheduleController extends Controller {

    // switches window to home screen
    private void showHome() {

    }

    //returns a list of rooms which have a max capacity of less than nPeople
    private Collection<String> getMaxPeople(int nPeople){
        Collection<String> a = new ArrayList<String>();
        return a;
    }

    //returns a string representing a day asked for by the user
    //in format mmddyyyy
    private String getDay(){
        return "";
    }

    //returns the roomID of the room asked for by the user
    private String getRoom(){
        return "";
    }

    //returns the roomID of the workstations asked for by the user
    private String getWorkStation(){
        return "";
    }

    //asks the database for available times for a particular room on a particular day
    //in format startTime-endTime;startTime-endTime; and so on
    //start and end time are hh:mm
    private String getRoomSched(String roomID, String day){
        return "";
    }

    //asks the database to update a room schedule for a particular room
    //returns true if success
    private boolean bookRoom(String roomID, String day, String time){
        return false;
    }

}
