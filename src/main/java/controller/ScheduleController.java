package controller;

import java.util.ArrayList;
import java.util.Collection;

public class ScheduleController extends Controller {

    // switches window to home screen
    private void showHome() {

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
