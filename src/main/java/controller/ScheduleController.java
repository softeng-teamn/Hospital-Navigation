package controller;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.ReservableSpace;
import model.Reservation;
import service.ResourceLoader;
import service.StageManager;

public class ScheduleController extends Controller {

    @FXML
    public JFXButton homeBtn, filterRoomBtn, makeReservationBtn, instructionsBtn, errorBtn;

    @FXML
    public JFXButton exitConfBtn, submitBtn, closeInstructionsBtn, serviceBtn, adminBtn;

    @FXML
    private VBox roomList;
    @FXML
    public VBox schedule;
    @FXML
    private VBox checks;

    @FXML
    private JFXTextField numRooms;
    @FXML
    public JFXTextField eventName;
    @FXML
    public JFXTextField employeeID;

    @FXML
    private JFXListView reservableList;

    @FXML
    public JFXDatePicker datePicker;

    @FXML
    public JFXTimePicker startTimePicker;
    @FXML
    public JFXTimePicker endTimePicker;

    @FXML
    private Label errorLbl;
    @FXML
    private Label timeLbl;
    @FXML
    public Label confErrorLbl;
    @FXML
    private Label instructionsLbl;

    @FXML
    public TitledPane instructionsPane, errorDlg;

    @FXML
    private AnchorPane confirmationPane, rightPane, bottomPane, homePane;

    @FXML
    private VBox leftPane;

    @FXML
    private HBox header;

    @FXML
    private StackPane stackP;

    @FXML
    public JFXComboBox<String> privacyLvlBox;

    private int openTime = 9;   // hour to start schedule dislay
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private int timeStep = 2;    // Fractions of an hour
    private int timeStepMinutes = 60/timeStep;    // In Minutes

    // Currently selected location
    public ReservableSpace currentSelection;
    // List of ints representing time blocks, where 0 is available and 1 is booked
    private ArrayList<Integer> currentSchedule;

    // TODO
    private BorderPane borderP;

    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {
        // Read in reservable Spaces
        CSVController csvC = new CSVController();
        csvC.importReservableSpaces();

        // Create the instructions and error message
        instructionsPane.setVisible(false);
        instructionsLbl.setText("1. Select desired date of reservation on the left.\n" +
                "2. Select a location in the middle menu to view its schedule " +
                "on that date. \n" +
                "3. Select the start and end times for your reservation on the left.\n" +
                "4. Select \"Make Reservation\" at bottom left (you must have selected" +
                " a location in order to make a reservation).\n" +
                "5. Confirm your reservation and complete the required information.");

        // Disable things that can't be used yet
        errorDlg.setVisible(false);
        confirmationPane.setVisible(false);
        confirmationPane.setDisable(true);
        confErrorLbl.setVisible(false);
        makeReservationBtn.setDisable(true);
        homePane.setDisable(false);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );
        privacyLvlBox.getItems().addAll(options);

        // Create arraylists
        currentSchedule = new ArrayList<Integer>();
        ObservableList<ReservableSpace> resSpaces = FXCollections.observableArrayList();

        // Set default date to today's date
        LocalDate date =  LocalDate.now();
        datePicker.setValue(date);

        // Set default start time to open time
        LocalTime startTime = LocalTime.of(openTime, 0);
        startTimePicker.setValue(startTime);

        // Set default end time to an hour after open time
        LocalTime endTime = LocalTime.of(openTime + 1, 0);
        endTimePicker.setValue(endTime);

        //  Pull spaces from database
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) dbs.getAllReservableSpaces();
        resSpaces.addAll(dbResSpaces);

        // Add the nodes to the listview
        reservableList.setItems(resSpaces);

        // Set the cell to display only the name of the reservableSpace
        reservableList.setCellFactory(param -> new ListCell<ReservableSpace>() {
            @Override
            protected void updateItem(ReservableSpace item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getSpaceName() == null) {
                    setText(null);
                } else {
                    setText(item.getSpaceName());
                    setOnMouseClicked(EventHandler -> {showRoomSchedule();} );
                }
            }
        });
        reservableList.setEditable(false);
    }


    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    /**
     *   On room button click, show the schedule for that room and date.
      */
    @FXML
    public void showRoomSchedule() {
        // Having chosen a location, users can now make a reservation
        makeReservationBtn.setDisable(false);

        // Get the selected location
        ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();
        currentSelection = curr;

        // Get that date and turn it into gregorian calendars to pass to the database
        LocalDate chosenDate = datePicker.getValue();
        LocalDate endDate = chosenDate.plus(1, ChronoUnit.DAYS);
        GregorianCalendar gcalStart = GregorianCalendar.from(chosenDate.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEnd = GregorianCalendar.from(endDate.atStartOfDay(ZoneId.systemDefault()));

        // Get reservations for this space and these times
        ArrayList<Reservation> reservations = (ArrayList<Reservation>) dbs.getReservationBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);

        // clear the previous schedule
        schedule.getChildren().clear();
        checks.getChildren().clear();
        currentSchedule.clear();

        // Make a list of time and activity labels for the schedule
        ArrayList<HBox> schedToAdd = new ArrayList<HBox>();
        ArrayList<Label> labelsToAdd = new ArrayList<Label>();

        // For every hour between the time the room closes and the time it opens
        for (int i = openTime; i < closeTime; i++) {
            String amPm = "AM";    // Half of the day
            if (((int) i / 12) == 1) {    // If in the afternoon, use PM
                amPm = "PM";
            }

            int time = i % 12;    // The hour, from 24 hours
            if (time == 0) {    // If the hour was 12, make it display as 12
                time = 12;
            }

            // For each time step in that hour, create a time label and activity label
            for (int j = 0; j < timeStep; j++) {
                Label actLabel = new Label("Available");    // Default activity is available
                String minutes = "00";
                if (j > 0) {    // Set the minutes
                    minutes = String.format("%d", (60 / timeStep));
                }

                // Create a new hbox to contain both labels and fill
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                Label timeInc = new Label(time + ":" + minutes + " " + amPm);
                timeInc.setMinWidth(68);
                timeInc.setTextAlignment(TextAlignment.CENTER);
                timeInc.setTextFill(Color.web("#FFFEFE"));
                timeInc.setStyle("-fx-background-color: #0f9d58; ");    // Default color is green, ie available
                hBox.getChildren().add(timeInc);

                // Add the labels to the lists
                schedToAdd.add(hBox);
                labelsToAdd.add(actLabel);
                currentSchedule.add(0);    // Default is 0, available
            }
        }

        // For each of this location's resMap, mark it booked on the schedule
        for (Reservation res : reservations) {
            // Get the start time
            int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
            int startFrac = startMinutes/(int)(timeStepMinutes);

            // Get the end time
            int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
            int endFrac = endMinutes/(int)(timeStepMinutes);

            // For every time between the start and end of the reservation,
            // Mark it as booked, color it red, and display the event name
            // or "Booked" depending on its privacy level
            for (int box = (startHour - openTime)*timeStep + startFrac; box < (endHour - openTime)*timeStep + endFrac; box++) {
                Label time = (Label) schedToAdd.get(box).getChildren().get(0);
                time.setStyle("-fx-background-color: #9b0f16; ");
                Label actLabel = (Label) labelsToAdd.get(box);
                if (res.getPrivacyLevel() == 0) {
                    actLabel.setText(res.getEventName());
                }
                else {
                    actLabel.setText("Booked");
                }
                currentSchedule.set(box, 1);
            }
        }

        // Add everything to the schedule to display
        schedule.getChildren().addAll(schedToAdd);
        checks.getChildren().addAll(labelsToAdd);
    }

    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() {
        makeTimeValid();
        boolean valid = validTimes();

        if (!valid) {    // If not valid, display an error message
            errorLbl.setText("Please enter valid start and end times " +
                    "for this location.\n\n" +
                    "Start and end times must not conflict with any " +
                    "currently scheduled resMap.");
            errorDlg.setVisible(true);
        }
        else {    // Otherwise, display the confirmation screen
            homePane.setDisable(true);
            homePane.toBack();
            confirmationPane.setVisible(true);
            confirmationPane.setDisable(false);
            showConf();
        }
    }

    /**
     * Called by the submit button on the confirmation page.
     * Creates a new reservation and adds it to the database.
     */
    @FXML
    public void submit() {
        confErrorLbl.setVisible(false);
        String id = employeeID.getText();
        boolean badId = false;

        // Check whether the ID is a number
        for (char letter: id.toCharArray()) {
            if (!Character.isDigit(letter)) {
                badId = true;
            }
        }

        // If the user has not entered an event name, has entered an invalid ID,
        // or has not chosen a privacy level, display an error message
        if (eventName.getText().length() < 1 || employeeID.getText().length() < 1 || privacyLvlBox.getValue() == null) {
            confErrorLbl.setText("Error: Please complete all fields to make a reservation.");
            confErrorLbl.setVisible(true);
        }

        // If the ID number is bad, display an error message.
        else if (badId /*|| dbs.getEmployee(Integer.parseInt(employeeID.getText())) == null*/) {
            confErrorLbl.setText("Error: Please provide a valid employee ID number.");
            confErrorLbl.setVisible(true);
        }
        else {    // Otherwise, create the reservation
            createReservation();
        }
    }

    /**
     * Create the reservation and send it to the database.
     */
    @FXML
    public void createReservation() {
        // Get the times and dates and turn them into gregorian calendars
        LocalDate chosenDate = datePicker.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();
        GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(chosenDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));

        // Get the privacy level
        int privacy = 0;
        if (privacyLvlBox.getValue().equals("Private")) {
            privacy = 1;
        }

        // Create the new reservation
        Reservation newRes = new Reservation(-1, privacy,Integer.parseInt(employeeID.getText()), eventName.getText(),currentSelection.getLocationNodeID(),gcalStart,gcalEnd);
        dbs.insertReservation(newRes);
        showRoomSchedule();
        closeConf();
    }

    /**
     * Show basic text instructions for how a user makes a reservation.
     */
    @FXML
    public void showInstructions() {
        instructionsPane.setVisible(true);
    }

    /**
     * Close instructions window.
     */
    @FXML
    public void closeInstructions() {
        instructionsPane.setVisible(false);
    }

    /**
     * Close the error dialog.
     */
    @FXML
    public void closeError() {
        errorDlg.setVisible(false);
    }

    /**
     * Show the reservation confirmation
     */
    private void showConf() {
        // Get the times to display
        LocalTime start = startTimePicker.getValue();
        LocalTime end = endTimePicker.getValue();

        // Display the current information
        timeLbl.setText("Reservation Location:      " + currentSelection.getSpaceName()
                + "\n\nReservation Date:            " + datePicker.getValue()
                + "\n\nReservation Start Time:   " + start
                + "\n\nReservation End Time:    " + end);
    }

    /**
     * Close the reservation confirmation dialog.
     */
    public void closeConf() {
        confirmationPane.toFront();
        confirmationPane.setVisible(false);
        homePane.setDisable(false);
        confErrorLbl.setVisible(false);
        eventName.setText("");
        employeeID.setText("");
        privacyLvlBox.setValue(null);
    }

    /**
     * Check whether the selected times are valid. If not, return false.
     * Valid times must: be within the chosen location's start and end times.
     *      Have an end time greater than the start time.
     *      Not conflict with any existing reservation.
     * @return true if the selected times are valid, false otherwise
     */
    private boolean validTimes() {
        // Get the selected times
        int start = startTimePicker.getValue().getHour();
        int mins = startTimePicker.getValue().getMinute()/(timeStepMinutes);
        int index = (start - openTime) * timeStep + mins;
        int end = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute()/(timeStepMinutes);
        int endIndex = (end - openTime) * timeStep + endMins;

        // If the times are outside the location's open times
        // or end is greater than start, the times are invalid
        if (endIndex <= index || start < openTime || closeTime < end) {
            return false;
        }

        // For each time in the reservation, check whether it is already booked
        for (int i = index; i < endIndex; i++) {
            if (currentSchedule.get(i) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * If the minutes selected are not an available time step
     * (ex. if timeStep is 2, so you can only select half-hour time incremenets,
     *  then :30 and :00 are valid but :04 and :15, etc. are not),
     *  round them down to the closest time step.
     */
    private void makeTimeValid() {
        // If the start minutes selected are not divisible by the timeStep (ex. 30 minutes)
        if (startTimePicker.getValue().getMinute()%(timeStepMinutes)!= 0) {
            // Then round them down to the nearest timeStep
            int minutes = ((int) startTimePicker.getValue().getMinute()/(timeStepMinutes))*(timeStepMinutes);
            startTimePicker.setValue(LocalTime.of(startTimePicker.getValue().getHour(), minutes));
        }
        // If the end minutes selected are not divisible by the timeStep (ex. 30 minutes)
        if (endTimePicker.getValue().getMinute()%(timeStepMinutes)!= 0) {
            // Rund them down to the nearest timeStep
            int minutes = ((int) endTimePicker.getValue().getMinute()/(timeStepMinutes))*(timeStepMinutes);
            endTimePicker.setValue(LocalTime.of(endTimePicker.getValue().getHour(), minutes));
        }
    }

    @FXML
    // switches window to map editor screen.
    public void showLogin() throws Exception {
        Stage stage = (Stage) adminBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeExistingWindow(stage, root, "Admin Login");
    }

    @FXML
    // switches window to request screen
    public void showService() throws Exception {
        Stage stage = (Stage) serviceBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    // TODO
    public void filterRooms() {

    }

    // TODO
    public void clearFilter() {

    }



    // Currently unused:
    //returns a list of roomIDs which have a max capacity of less than nPeople
    ArrayList<String> getMaxPeople(int nPeople){
        ArrayList<String> a = new ArrayList<>();
        return a;
    }
}
