package scheduler.controller;

import application_state.ApplicationState;
import application_state.Event;
import com.google.zxing.WriterException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.model.Reservation;
import service.QRService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Reservation confirmation screen. Displays selected location, date, and time
 * and allows user to enter event name, auto-populates the user's ID,
 * and allows user to select public or private event and submit reservation.
 */
public class ConfirmReservationController {

    @FXML
    public JFXButton homeBtn, makeReservationBtn, backBtn;
    @FXML
    public Label inputErrorLbl, resInfoLbl;
    @FXML
    public JFXComboBox<String> privacyLvlBox;
    @FXML
    public JFXTextField employeeID, eventName;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

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

        Event e = ApplicationState.getApplicationState().getObservableBus().getEvent();
        ArrayList<GregorianCalendar> cals = e.getStartAndEndTimes();

        // Get the start time
        int startHour = (int) (cals.get(0).getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
        String startMinutes = Integer.toString((int) (cals.get(0).getTimeInMillis() / (1000 * 60)) % 60);
        if (startMinutes.equals("0")) {
            startMinutes = "00";
        }
        // Get the end time
        int endHour = (int) (cals.get(1).getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
        String endMinutes = Integer.toString((int) (cals.get(1).getTimeInMillis() / (1000 * 60)) % 60);
        if (endMinutes.equals("0")) {
            endMinutes = "00";
        }

        String date = "" + cals.get(0).getTime();
        date = date.substring(0, 10) + ", " + date.substring(24);

        String endDate = "" + cals.get(1).getTime();
        endDate = endDate.substring(0, 10) + ", " + endDate.substring(24);


        resInfoLbl.setText("Location:      " + myDBS.getReservableSpace(e.getRoomId()).getSpaceName()
                + "\n\nStart Date:   " + date
                + "\n\nStart Time:   " + startHour + ":" + startMinutes
                + "\n\nEnd Date:     " + endDate
                + "\n\nEnd Time:    " + endHour + ":" + endMinutes);
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
        String id = employeeID.getText();
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
            valid = false ;
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

        // If everything is okay, create the reservation
        if (valid) {
            System.out.println("IS VALID - CREATING RESERVATION");
            createReservation();

            // Return to schedule main page
            Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(ResourceLoader.scheduler);
            StageManager.changeExistingWindow(stage, root, "Scheduler");
        }
    }

    /**
     * Create the reservation and send it to the database.
     */
    @FXML
    public void createReservation() throws IOException, WriterException {
        Event event = ApplicationState.getApplicationState().getObservableBus().getEvent() ;
        // Get the privacy level
        int privacy = 0;
        if (privacyLvlBox.getValue().equals("Private")) {
            privacy = 1;
        }

        // set event bussed info from other page
        ArrayList<GregorianCalendar> cals = event.getStartAndEndTimes();
        String roomID = event.getRoomId();

        // create new reservation and add to database
        Reservation newRes = new Reservation(-1, privacy, Integer.parseInt(employeeID.getText()), eventName.getText(), roomID, cals.get(0), cals.get(1));
        myDBS.insertReservation(newRes);

        // Create QR code popup
        // TODO: figure out adding some sort of label
        Stage stage = (Stage) privacyLvlBox.getScene().getWindow();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new ImageView(QRService.generateQRCode("https://softeng-teamn.github.io/cal.html?eventName=" + eventName.getText() + "&eventLocation=" + myDBS.getReservableSpace(roomID).getSpaceName() + "&eventOrganizer=" + myDBS.getEmployee(Integer.parseInt(employeeID.getText())).getUsername() + "&startTime=" + cals.get(0).getTimeInMillis()/1000 + "&endTime=" + cals.get(1).getTimeInMillis()/1000, true)));
        Scene dialogScene = new Scene(dialogVbox, 350, 350);
        dialog.setScene(dialogScene);
        dialog.show();

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
