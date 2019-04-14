package scheduler.controller;

import application_state.ApplicationState;
import application_state.Event;
import application_state.EventBusFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scheduler.model.Reservation;
import service.ResourceLoader;
import service.StageManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ConfirmReservationController {


    @FXML
    public JFXButton homeBtn, makeReservationBtn, backBtn ;
    @FXML
    public Label inputErrorLbl;
    @FXML
    public JFXComboBox<String> privacyLvlBox;
    @FXML
    public JFXTextField employeeID, eventName;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    // variables to hold incoming event information
    String roomID = "" ;
    ArrayList<GregorianCalendar> cals = null ;


    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {

        eventBus.register(this);

        // sets ID to logged in employee
        setID();

        inputErrorLbl.setVisible(false);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );

        privacyLvlBox.setItems(options);

    }



    /**
     * pre-fill employee id field with id of logged in employee
     */
    @FXML
    public void setID() {

        int idNum = ApplicationState.getApplicationState().getEmployeeLoggedIn().getID();
        String id = Integer.toString(idNum);
        employeeID.setText(id);

    }


    /**
     * switches window to home screen
     * @throws Exception
     */
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }





    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() {

        // CHECK FOR IF TIMES ARE VALID ELSEWHERE - BEFORE THIS PAGE!!!!!
        boolean valid = validTimes(true);

        // Check user input for valid ID
        inputErrorLbl.setVisible(false);

        String id = employeeID.getId() ;
        boolean badId = false;

        // Check whether the ID is a number
        for (char letter: id.toCharArray()) {
            if (!Character.isDigit(letter)) {
                badId = true;
            }
        }

       // If the user has not entered an event name, has entered an invalid ID,
        // or has not chosen a privacy level, display an error message
        if (eventName.getText().length() < 1 || id.length() < 1 || privacyLvlBox.getValue() == null) {
            inputErrorLbl.setText("Error: Please complete all fields to make a reservation.");
            inputErrorLbl.setVisible(true);
            valid = false;
        }
        // If the event name is too long, show an error
        if (eventName.getText().length() > 50) {
            inputErrorLbl.setText("Error: Please enter a shorter reservation name.");
            inputErrorLbl.setVisible(true);
            valid = false;
        }

        // If the ID number is bad, display an error message.
        if (myDBS.getEmployee(Integer.parseInt(employeeID.getText())) == null) {
            inputErrorLbl.setText("Error: Please provide a valid employee ID number.");
            inputErrorLbl.setVisible(true);
            valid = false;
        }

        // If evreything is okay, create the reservation
        if (valid){
            createReservation();
        }
    }




    /**
     * confirmation that selected times are valid - hold over from other screen
     */
    private boolean validTimes(boolean forRes) {
        // ONLY FOR NOW
        // CHANGE BASED ON EVENT BUS MAYBE??
        return true;
    }





    // events I care about: am "subscribed" to
    @Subscribe
    private void eventListener(Event newEvent) {

        switch (newEvent.getEventName()) {
            case "times":
                cals = event.getStartAndEndTimes() ;
                break;
            case "room":
                roomID = event.getRoomId() ;
                break;
            default:
                break;
        }

    }




 /**
 * Create the reservation and send it to the database.
 */
 @FXML
    public void createReservation() {


        // Get the times and dates and turn them into gregorian calendars
     // GET TIMES FROM SCHEDULE CONTROLLER
       // ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();

        // Get the privacy level
        int privacy = 0;
        if (privacyLvlBox.getValue().equals("Private")) {
            privacy = 1;
        }

        // Create the new reservation
        Reservation newRes = new Reservation(-1, privacy,Integer.parseInt(employeeID.getText()), eventName.getText(),roomID,cals.get(0),cals.get(1));
        myDBS.insertReservation(newRes);

        // Reset the screen
        resetView();
    }



    /**
     * Show the current schedule, clear errors, and clear user input.
     */
    private void resetView() {
        inputErrorLbl.setVisible(false);
//        timeErrorLbl.setVisible(false);
        eventName.setText("");
        employeeID.setText("");
        privacyLvlBox.setValue(null);
        // resInfoLbl.setText("");
    }








 }
