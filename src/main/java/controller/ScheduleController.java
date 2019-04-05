package controller;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Node;
import model.ReservableSpace;
import model.Reservation;
import service.CSVService;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

public class ScheduleController extends Controller {

    @FXML
    public JFXButton homeBtn, makeReservationBtn, availRoomsBtn, bookedRoomsBtn;

    @FXML
    public JFXButton submitBtn;

    @FXML
    public VBox schedule, checks;

    @FXML
    public JFXTextField eventName, employeeID, searchBar;

    @FXML
    public JFXListView reservableList;

    @FXML
    public JFXDatePicker datePicker;

    @FXML
    public JFXTimePicker startTimePicker, endTimePicker;

    @FXML
    public Label errorLbl, timeLbl, confErrorLbl;

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

    // LIst of spaces to display
    private ObservableList<ReservableSpace> resSpaces;

    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {
        // Read in reservable Spaces
        CSVService.importReservableSpaces();

        // Disable things that can't be used yet
        errorLbl.setVisible(false);
        confErrorLbl.setVisible(false);
        makeReservationBtn.setDisable(true);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );
        privacyLvlBox.getItems().addAll(options);

        // Create arraylists
        currentSchedule = new ArrayList<Integer>();
        resSpaces = FXCollections.observableArrayList();

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
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) DatabaseService.getDatabaseService().getAllReservableSpaces();
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

        reservableList.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            focusState(newValue);
        });
        datePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            focusState(newValue);
        });
        startTimePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            focusState(newValue);
        });
        endTimePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            focusState(newValue);
        });
    }

    private void focusState(boolean value) {
        if (!value) {
            makeTimeValid();
            LocalTime start = startTimePicker.getValue();
            LocalTime end = endTimePicker.getValue();

            // Display the current information
            timeLbl.setText("Reservation Location:      " + currentSelection.getSpaceName()
                    + "\n\nReservation Date:            " + datePicker.getValue()
                    + "\n\nReservation Start Time:   " + start
                    + "\n\nReservation End Time:    " + end);
        }
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
        LocalDate endDate = datePicker.getValue().plus(1, ChronoUnit.DAYS);
        GregorianCalendar gcalStart = GregorianCalendar.from(chosenDate.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEnd = GregorianCalendar.from(endDate.atStartOfDay(ZoneId.systemDefault()));

        // Get reservations for this space and these times
        ArrayList<Reservation> reservations = (ArrayList<Reservation>) DatabaseService.getDatabaseService().getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);
        System.out.println(curr.getSpaceID() + " " + reservations);

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
                actLabel.setMaxWidth(Double.MAX_VALUE);

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

        /**
         * For each of this location's reservations, mark it booked on the schedule
         */
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
        boolean valid = validTimes(true);

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
        else if (eventName.getText().length() > 50) {
            confErrorLbl.setText("Error: Please enter a shorter reservation name.");
            confErrorLbl.setVisible(true);
        }
        // TODO: validate id
        // If the ID number is bad, display an error message.
        else if (badId /*|| DatabaseService.getDatabaseService().getEmployee(Integer.parseInt(employeeID.getText())) == null*/) {
            confErrorLbl.setText("Error: Please provide a valid employee ID number.");
            confErrorLbl.setVisible(true);
        }
        else if (valid){    // Otherwise, create the reservation
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
        Reservation newRes = new Reservation(-1, privacy,Integer.parseInt(employeeID.getText()), eventName.getText(),currentSelection.getSpaceID(),gcalStart,gcalEnd);
        DatabaseService.getDatabaseService().insertReservation(newRes);

        // Reset the screen
        showRoomSchedule();
        confErrorLbl.setVisible(false);
        errorLbl.setVisible(false);
        eventName.setText("");
        employeeID.setText("");
        privacyLvlBox.setValue(null);
        timeLbl.setText("");
    }

    /**
     * Check whether the selected times are valid. If not, return false.
     * Valid times must: be within the chosen location's start and end times.
     *      Have an end time greater than the start time.
     *      Not conflict with any existing reservation.
     *      Be in the future. TODO
     * @return true if the selected times are valid, false otherwise
     */
    private boolean validTimes(boolean forRes) {
        boolean valid = true;
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
            valid = false;
        }

        if (forRes) {
            // For each time in the reservation, check whether it is already booked
            for (int i = index; i < endIndex; i++) {
                if (currentSchedule.get(i) == 1) {
                    valid = false;
                }
            }
        }

        if (!valid) {
            errorLbl.setText("Please enter valid start and end times.");
            errorLbl.setVisible(true);
        }
        return valid;
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

    /**
     * searches for room
     * @param e
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = searchBar.getText();
        filterList(search);
    }

    /**
     *Filters the ListView based on the string
     */
    private void filterList(String findStr) {
        ObservableList<ReservableSpace> resSpaces = FXCollections.observableArrayList();
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) DatabaseService.getDatabaseService().getAllReservableSpaces();
        resSpaces.addAll(dbResSpaces);
        if (findStr.equals("")) {
            reservableList.getItems().clear();
            reservableList.getItems().addAll(resSpaces);
        }
        else {
            //Get List of all nodes
            ObservableList<ReservableSpace> original = resSpaces;

            //Get Sorted list of nodes based on search value
            List<ExtractedResult> filtered = FuzzySearch.extractSorted(findStr, convertList(original, ReservableSpace::getSpaceName),75);

            // Map to nodes based on index
            Stream<ReservableSpace> stream = filtered.stream().map(er -> {
                return original.get(er.getIndex());
            });

            // Convert to list and then to observable list
            List<ReservableSpace> filteredSpaces = stream.collect(Collectors.toList());
            ObservableList<ReservableSpace> toShow = FXCollections.observableList(filteredSpaces);

            // Add to view
            reservableList.getItems().clear();
            reservableList.getItems().addAll(toShow);
        }
    }

    /**
     *for lists
     */
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    /**
     * Filter rooms by currently selected date and times
     * Displays rooms without reservations that overlap those times
     */
    public void availRooms() {
        errorLbl.setVisible(false);
        boolean valid = validTimes(false);
        if (valid) {
            // Get selected times
            LocalDate chosenDate = datePicker.getValue();
            LocalTime startTime = startTimePicker.getValue();
            LocalTime endTime = endTimePicker.getValue();
            GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
            GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(chosenDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));

            // Get reservations between selected times
            ArrayList<Reservation> reservationsBetween = (ArrayList<Reservation>) DatabaseService.getDatabaseService().getReservationsBetween(gcalStart, gcalEnd);

            int startSize = resSpaces.size();
            for (int i = 0; i < reservationsBetween.size(); i++) {    // For each reservation
                for (int j = 0; j < startSize; j++) {    // Go through all the spaces to remove that space from the list
                    if (resSpaces.get(j).getSpaceID().equals(reservationsBetween.get(i).getLocationID())) {
                        resSpaces.remove(j);
                        startSize--;
                        j--;
                    }
                }
            }

            // Set button
            availRoomsBtn.setOnAction(EventHandler -> {
                clearFilter(availRoomsBtn, "Show Available Rooms");
            });
            availRoomsBtn.setText("Clear filter");

            // Clear the current schedule
            schedule.getChildren().clear();
            checks.getChildren().clear();
            currentSchedule.clear();
            bookedRoomsBtn.setDisable(true);
        }
    }

    /**
     * Display booked rooms for currently selected date and times
     */
    public void bookedRooms() {
        errorLbl.setVisible(false);
        boolean valid = validTimes(false);
        if (valid) {
            // Get selected times
            LocalDate chosenDate = datePicker.getValue();
            LocalTime startTime = startTimePicker.getValue();
            LocalTime endTime = endTimePicker.getValue();
            GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
            GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(chosenDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));

            ObservableList<ReservableSpace> bookedSpaces = FXCollections.observableArrayList();

            // Get reservations between selected times
            ArrayList<Reservation> reservationsBetween = (ArrayList<Reservation>) DatabaseService.getDatabaseService().getReservationsBetween(gcalStart, gcalEnd);

            int startSize = resSpaces.size();
            for (int i = 0; i < reservationsBetween.size(); i++) {    // For each reservation
                for (int j = 0; j < startSize; j++) {    // Go through all the spaces to remove that space from the list
                    if (resSpaces.get(j).getSpaceID().equals(reservationsBetween.get(i).getLocationID())) {
                        bookedSpaces.add(resSpaces.get(j));
                        resSpaces.remove(j);
                        startSize--;
                        j--;
                    }
                }
            }

            resSpaces.clear();
            resSpaces.addAll(bookedSpaces);

            // Set button
            bookedRoomsBtn.setOnAction(EventHandler -> {
                clearFilter(bookedRoomsBtn, "Show Booked Rooms");
            });
            bookedRoomsBtn.setText("Clear filter");

            // Clear the current schedule
            schedule.getChildren().clear();
            checks.getChildren().clear();
            currentSchedule.clear();
            availRoomsBtn.setDisable(true);
        }
    }

    /**
     * Reset display to show all spaces
     */
    public void clearFilter(JFXButton btn, String text) {
        // Set list to all spaces
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) DatabaseService.getDatabaseService().getAllReservableSpaces();
        resSpaces.clear();
        resSpaces.addAll(dbResSpaces);

        // Clear schedule
        schedule.getChildren().clear();
        checks.getChildren().clear();
        currentSchedule.clear();

        if (availRoomsBtn.getText().contains("Clear")) {
            availRoomsBtn.setOnAction(EventHandler -> {availRooms();});
        }
        else {
            bookedRoomsBtn.setOnAction(EventHandler -> {bookedRooms();});
        }
        btn.setText(text);
        availRoomsBtn.setDisable(false);
        bookedRoomsBtn.setDisable(false);
    }


    /**
     * Currently unused:
     * returns a list of roomIDs which have a max capacity of less than nPeople
     * @param nPeople
     * @return
     */
    ArrayList<String> getMaxPeople(int nPeople){
        ArrayList<String> a = new ArrayList<>();
        return a;
    }
}
