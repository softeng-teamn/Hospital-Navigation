package scheduler.controller;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import com.google.common.eventbus.DeadEvent;
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

public class ConfirmReservationController implements Observer {


    @FXML
    public JFXButton homeBtn, makeReservationBtn, backBtn;
    @FXML
    public Label inputErrorLbl;
    @FXML
    public JFXComboBox<String> privacyLvlBox;
    @FXML
    public JFXTextField employeeID, eventName;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();



    // variables to hold incoming event information
    String roomID = "";
    ArrayList<GregorianCalendar> cals = null;


    /**
     * Set up page.
     */
    @FXML
    public void initialize() {


        // sets ID to logged in employee
        setID();

        // keep error message hidden
        inputErrorLbl.setVisible(false);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );
        privacyLvlBox.setItems(options);
    }


    // events I care about: am "subscribed" to
    @Override
    public void notify(Object newEvent) {
        Event e = (Event) newEvent ;
        switch (e.getEventName()) {
            case "times":
                cals = e.getStartAndEndTimes();
                break;
            case "room":
                roomID = e.getRoomId();
                break;
            default:
                break;
        }
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
     *
     * @throws Exception
     */
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }


    /**
     * switches window to back to scheduler screen
     *
     * @throws Exception
     */
    public void backToScheduler() throws Exception {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
    }


    /**
     * Called by the Make Reservation button.
     * Checks if event information is correct
     */
    @FXML
    public void makeReservation() throws Exception {

        // initially set variable to true
        boolean valid = true;

        // set/reset error message to hidden
        inputErrorLbl.setVisible(false);

        // check employee id
        // parse around prompt text
        String id = employeeID.getId();
        boolean badId = false;

        // Check whether the ID is a number
        for (char letter : id.toCharArray()) {
            if (!Character.isDigit(letter)) {
                badId = true;
            }
        }

        if (badId) {
            inputErrorLbl.setText("Error: Please provide a valid employee ID number.");
            inputErrorLbl.setVisible(true);
            //valid = false ;
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
        if (valid) {
            System.out.println("IS VALID - CREATING RESERVATION");
            createReservation();


            Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(ResourceLoader.scheduler);
            StageManager.changeExistingWindow(stage, root, "Scheduler");
        }
    }


    /**
     * Create the reservation and send it to the database.
     */
    @FXML
    public void createReservation() {

        Event event = ApplicationState.getApplicationState().getObservableBus().getEvent() ;

        // Get the privacy level
        int privacy = 0;
        if (privacyLvlBox.getValue().equals("Private")) {
            privacy = 1;
        }

        // set event bussed info from other page
        cals = event.getStartAndEndTimes();
        roomID = event.getRoomId();

        // create new reservation and add to database
        Reservation newRes = new Reservation(-1, privacy, Integer.parseInt(employeeID.getText()), eventName.getText(), roomID, cals.get(0), cals.get(1));
        myDBS.insertReservation(newRes);
        System.out.println("NEW RESRVATION INSERTED INTO DATABASE");

        // Reset the screen
        resetView();
    }


    /**
     * Show the current schedule, clear errors, and clear user input.
     */
    private void resetView() {
        inputErrorLbl.setVisible(false);
        eventName.setText("");
        privacyLvlBox.setValue(null);
    }


}