package scheduler.controller;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application_state.ApplicationState;
import com.jfoenix.controls.*;
import controller.Controller;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import map.Node;
import scheduler.model.ReservableSpace;
import scheduler.model.Reservation;
import database.CSVService;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

public class ScheduleController extends Controller {

    private static class ScheduleWrapper {
        private String time;
        public void setTime(String value) { this.time = value; }
        public String getTime() { return time; }

        private String availability;
        public void setAvailability(String value) { this.availability = value; }
        public String getAvailability() { return availability; }

        public ScheduleWrapper(String time) {
            this.time = time;
            this.availability = "Available";
        }

        @Override
        public String toString() {
            return "ScheduleWrapper{" +
                    "time='" + time + '\'' +
                    ", availability='" + availability + '\'' +
                    '}';
        }
    }

    @FXML
    public JFXButton homeBtn, makeReservationBtn, availRoomsBtn, bookedRoomsBtn;

    @FXML
    public JFXButton submitBtn;

    @FXML
    public JFXTextField eventName, searchBar, employeeID;

    @FXML
    private TableView<ScheduleWrapper> scheduleTable;

    @FXML
    public JFXListView reservableList;

    @FXML
    public JFXDatePicker datePicker;

    @FXML
    public JFXTimePicker startTimePicker, endTimePicker;

    @FXML
    public Label resInfoLbl, inputErrorLbl;

    @FXML
    public JFXComboBox<String> privacyLvlBox;




    // Map Stuff
    static final Color AVAILIBLE_COLOR = Color.rgb(87,255,132,0.8);
    static final Color UNAVAILABLE_COLOR = Color.rgb(255,82,59, 0.8);
    Group zoomGroup;
    ArrayList<Node> nodeCollection = new ArrayList<Node>();
    ArrayList<Circle> circleCollection = new ArrayList<Circle>();
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXSlider zoom_slider;


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
    // Error messages
    private String timeErrorText, availRoomsText, bookedRoomsText, clearFilterText, conflictErrorText, pastDateErrorText;
    // Database
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    public int getOpenTime() {
        return openTime;
    }

    public void setOpenTime(int openTime) {
        this.openTime = openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
        this.closeTime = closeTime;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {

        setID();

        // Map Initialization

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);

        // Setting View Scrolling
        zoom_slider.setMin(0.2);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.2);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom(0.2);




        resInfoLbl.setText("");
        timeErrorText = "Please enter valid start and end times.";
        availRoomsText = "Show Available Spaces";
        bookedRoomsText = "Show Booked Spaces";
        clearFilterText = "Clear Filter";
        conflictErrorText = "Please select times that do not conflict with currently scheduled times.";
        pastDateErrorText = "Please select a date that is not in the past.";

        // Read in reservable Spaces
        CSVService.importReservableSpaces();

        // Don't show errors yet
//        timeErrorLbl.setVisible(false);
        inputErrorLbl.setVisible(false);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );
        privacyLvlBox.setItems(options);

        // Set default date to today's date
        LocalDate date =  LocalDate.now();
        datePicker.setValue(date);

        // Set default start time to current time, or the closest open hour
        int startHour = LocalTime.now().getHour();
        if (startHour < openTime) {
            startHour = openTime;
        }
        if (startHour >= closeTime) {
            startHour = closeTime - 1;
        }
        LocalTime startTime = LocalTime.of(startHour, 0);
        startTimePicker.setValue(startTime);

        // Set default end time to an hour after open time
        LocalTime endTime = LocalTime.of(startHour + 1, 0);
        endTimePicker.setValue(endTime);


        // Create arraylists
        currentSchedule = new ArrayList<Integer>();
        resSpaces = FXCollections.observableArrayList();

        //  Pull spaces from database, sort, add to list and listview
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) myDBS.getAllReservableSpaces();
        for (ReservableSpace rs : dbResSpaces) {
            nodeCollection.add(DatabaseService.getDatabaseService().getNode(rs.getLocationNodeID()));
        }
        // SHOW MAP NODES *****************
        populateMap();
        Collections.sort(dbResSpaces);
        resSpaces.addAll(dbResSpaces);
        reservableList.setItems(resSpaces);
        reservableList.setEditable(false);

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

        // Select the first item and display its schedule
        reservableList.getSelectionModel().select(0);
        reservableList.getFocusModel().focus(0);

        // Create table columns, set what they display, and add to the table
        TableColumn<ScheduleWrapper, String> timeCol = new TableColumn<>("Time");
        TableColumn<ScheduleWrapper, String> bookingCol = new TableColumn<>("Availability");
        bookingCol.setPrefWidth(400);
        scheduleTable.setEditable(false);
        timeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return new ReadOnlyStringWrapper(p.getValue().getTime());
            }
        });
        bookingCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return new ReadOnlyStringWrapper(p.getValue().getAvailability());
            }
        });
        scheduleTable.getColumns().addAll(timeCol, bookingCol);
//        scheduleTable.setPrefHeight(900);
        showRoomSchedule();

        // Set listeners to update listview and label
        reservableList.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            listFocus(newValue);
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
         * Listener to update listview of rooms and info label
         * @param value
         */
    private void focusState(boolean value) {
        if (!value && validTimes(false)) {
            if (availRoomsBtn.getText().contains("ear")) {
                availRooms();
            }
            else if (bookedRoomsBtn.getText().contains("ear")) {
                bookedRooms();
            }
            showRoomSchedule();

            // Display the current information
            changeResInfo();
        }
        populateMap();
    }

    /**
     * Display the current reservation information for the user
     */
    private void changeResInfo() {
//        resInfoLbl.setText("Location:      " + currentSelection.getSpaceName()
//            + "\n\nDate:            " + datePicker.getValue()
//            + "\n\nStart Time:   " + startTimePicker.getValue()
//            + "\n\nEnd Time:    " + endTimePicker.getValue());
    }

    /**
     * Listener to update label when listview selection changed
     * @param value
     */
    private void listFocus(boolean value) {
        if (!value) {
            // Display the current information
            changeResInfo();
        }
        populateMap();
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
        System.out.println("showing schedule");
        // Clear the previous schedule
        currentSchedule.clear();

        // Get the selected location
        ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();
        currentSelection = curr;

        // Get that date and turn it into gregorian calendars to pass to the database
        LocalDate chosenDate = datePicker.getValue();
        LocalDate endDate = datePicker.getValue().plus(1, ChronoUnit.DAYS);
        GregorianCalendar gcalStart = GregorianCalendar.from(chosenDate.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEnd = GregorianCalendar.from(endDate.atStartOfDay(ZoneId.systemDefault()));

        // Make a list of time and activity labels for the schedule
        ArrayList<ScheduleWrapper> schedToAdd = new ArrayList<>();

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
                String minutes = String.format("%d", timeStepMinutes * j);
                if (Integer.parseInt(minutes) == 0) {
                    minutes = "00";
                }
                String timeInc = time + ":" + minutes + " " + amPm;

                // Add the labels to the lists
                ScheduleWrapper toAdd = new ScheduleWrapper(timeInc);
                schedToAdd.add(toAdd);
                currentSchedule.add(0);    // Default is 0, available
            }
        }

        // Get reservations for this space and these times
        ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);

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
                ScheduleWrapper time = schedToAdd.get(box);
                if (res.getPrivacyLevel() == 0) {
                    time.setAvailability(res.getEventName());
                }
                else {
                    time.setAvailability("Booked");
                }
                currentSchedule.set(box, 1);
            }
        }

        ObservableList<ScheduleWrapper> wrap = FXCollections.observableArrayList();
        wrap.addAll(schedToAdd);
        scheduleTable.setItems(wrap);
        for (int i = 0; i < schedToAdd.size(); i++) {
            System.out.println(scheduleTable.getItems().get(i));
        }
    }

    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() {
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
     * Create the reservation and send it to the database.
     */
    @FXML
    public void createReservation() {
        // Get the times and dates and turn them into gregorian calendars
        ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();

        // Get the privacy level
        int privacy = 0;
        if (privacyLvlBox.getValue().equals("Private")) {
            privacy = 1;
        }

        // Create the new reservation
        Reservation newRes = new Reservation(-1, privacy,Integer.parseInt(employeeID.getText()), eventName.getText(),currentSelection.getSpaceID(),cals.get(0),cals.get(1));
        myDBS.insertReservation(newRes);

        // Reset the screen
        resetView();
        populateMap();
    }

    /**
     * Make gregorian calendars from the currently selected date and time.
     * @return a list of gregorian calendars of the current start time/date and end time/date
     */
    private ArrayList<GregorianCalendar> gCalsFromCurrTimes() {
        LocalDate chosenDate = datePicker.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();
        GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(chosenDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));
        ArrayList<GregorianCalendar> cals = new ArrayList<>();
        cals.add(gcalStart);
        cals.add(gcalEnd);
        return cals;
    }

    /**
     * Show the current schedule, clear errors, and clear user input.
     */
    private void resetView() {
        showRoomSchedule();
        inputErrorLbl.setVisible(false);
//        timeErrorLbl.setVisible(false);
        eventName.setText("");
        employeeID.setText("");
        privacyLvlBox.setValue(null);
        resInfoLbl.setText("");
    }

    /**
     * Check whether the selected times are valid. If not, return false.
     * Valid times must: be within the chosen location's start and end times.
     *      Have an end time greater than the start time.
     *      Not conflict with any existing reservation.
     *      Be in the future.
     * @return true if the selected times are valid, false otherwise
     */
    private boolean validTimes(boolean forRes) {
//        timeErrorLbl.setVisible(false);
        makeMinutesValid();
        // Get the selected times
        int start = startTimePicker.getValue().getHour();
        int mins = startTimePicker.getValue().getMinute()/(timeStepMinutes);
        int index = (start - openTime) * timeStep + mins;
        int end = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute()/(timeStepMinutes);
        int endIndex = (end - openTime) * timeStep + endMins;

        // If the chosen date is in the past, show an error
        if (forRes && datePicker.getValue().atStartOfDay().isBefore(LocalDate.now().atStartOfDay())) {
//            timeErrorLbl.setText(pastDateErrorText);
//            timeErrorLbl.setVisible(true);
            return false;
        }

        // If the times are outside the location's open times
        // or end is greater than start, the times are invalid
        if (endIndex <= index || start < openTime || closeTime < end) {
//            timeErrorLbl.setText(timeErrorText);
//            timeErrorLbl.setVisible(true);
            return false;
        }

        if (forRes) {
            // For each time in the reservation, check whether it is already booked
            for (int i = index; i < endIndex; i++) {
                if (currentSchedule.get(i) == 1) {    // If so, show an error
//                    timeErrorLbl.setText(conflictErrorText);
//                    timeErrorLbl.setVisible(true);
                    return false;
                }
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
    private void makeMinutesValid() {
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
     * Searches for reservable space
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
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) myDBS.getAllReservableSpaces();
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
        boolean valid = validTimes(false);
        if (valid) {
            // Get selected times
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();
            // Get reservations between selected times
            ArrayList<ReservableSpace> availBetween = (ArrayList<ReservableSpace>) myDBS.getAvailableReservableSpacesBetween(cals.get(0), cals.get(1));

            // Set button
            availRoomsBtn.setOnAction(EventHandler -> {
                clearFilter();
            });
            availRoomsBtn.setText(clearFilterText);
            bookedRoomsBtn.setText(bookedRoomsText);
            bookedRoomsBtn.setOnAction(EventHandler -> {bookedRooms();});

            Collections.sort(availBetween);
            resSpaces.clear();
            resSpaces.addAll(availBetween);
            reservableList.setItems(resSpaces);

            // Clear the current schedule
            reservableList.getSelectionModel().select(0);
            reservableList.getFocusModel().focus(0);
            showRoomSchedule();
        }
    }

    /**
     * Display booked rooms for currently selected date and times
     */
    public void bookedRooms() {
        boolean valid = validTimes(false);
        if (valid) {
            populateMap();
            // Get selected times
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes(); // ******* GETS TWO GREG CALENDERS

            // Get reservations between selected times                          ****** shows all rooms that are booked based on selected times
            ArrayList<ReservableSpace> bookedBetween = (ArrayList<ReservableSpace>) myDBS.getBookedReservableSpacesBetween(cals.get(0), cals.get(1));

            Collections.sort(bookedBetween);
            resSpaces.clear();
            resSpaces.addAll(bookedBetween);

            // Set button
            bookedRoomsBtn.setOnAction(EventHandler -> {
                clearFilter();
            });
            bookedRoomsBtn.setText(clearFilterText);
            availRoomsBtn.setText(availRoomsText);
            availRoomsBtn.setOnAction(EventHandler -> {availRooms();});

            reservableList.setItems(resSpaces);

            // Clear the current schedule
            reservableList.getSelectionModel().select(0);
            reservableList.getFocusModel().focus(0);
            showRoomSchedule();
        }
    }

    /**
     * Reset display to show all spaces
     */
    public void clearFilter() {
//        timeErrorLbl.setVisible(false);
        // Set list to all spaces
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) myDBS.getAllReservableSpaces();
        Collections.sort(dbResSpaces);
        resSpaces.clear();
        resSpaces.addAll(dbResSpaces);

        // Clear schedule
        reservableList.getSelectionModel().select(0);
        reservableList.getFocusModel().focus(0);
        showRoomSchedule();

        availRoomsBtn.setOnAction(EventHandler -> {availRooms();});
        bookedRoomsBtn.setOnAction(EventHandler -> {bookedRooms();});
        availRoomsBtn.setText(availRoomsText);
        bookedRoomsBtn.setText(bookedRoomsText);
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



    // MAP STUFF DOWN HERE ****************


    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    ArrayList<ReservableSpace> getBookedNodes() {
        // Get selected times
        ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes(); // ******* GETS TWO GREG CALENDERS

        // Get reservations between selected times                          ****** shows all rooms that are booked based on selected times
        return (ArrayList<ReservableSpace>) myDBS.getBookedReservableSpacesBetween(cals.get(0), cals.get(1));
    }

    boolean isNodeInReservableSpace(ArrayList<ReservableSpace> rs, Node n) {
        for (int i = 0; i < rs.size(); i++) {
            if (rs.get(i).getLocationNodeID().equals(n.getNodeID())) {
                return true;
            }
        }
        return false;
    }

    void populateMap() {
        System.out.println("**************** REPOPULAT MAP");
        zoomGroup.getChildren().removeAll(circleCollection);
        ArrayList<ReservableSpace> bookedRS = getBookedNodes();
        circleCollection = new ArrayList<Circle>();
        for(Node node : nodeCollection) {
            Circle circle = new Circle();
            circle.setRadius(80);
            if (isNodeInReservableSpace(bookedRS, node)) {
                circle.setFill(UNAVAILABLE_COLOR);
            } else {
                circle.setFill(AVAILIBLE_COLOR);
            }
            circle.setCenterX(node.getXcoord());
            circle.setCenterY(node.getYcoord());
            circleCollection.add(circle);
        }
        zoomGroup.getChildren().addAll(circleCollection);
    }

    @FXML
    void mapClickedHandler(MouseEvent e) {
        System.out.println("x: " + e.getX() + ", y: " + e.getY());
    }


}
