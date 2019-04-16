package scheduler.controller;


import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application_state.ApplicationState;
import application_state.Event;

import com.jfoenix.controls.*;
import com.twilio.rest.api.v2010.account.incomingphonenumber.Local;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
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

// todo: cleanup this class
public class ScheduleController {

    // Wrapper to display times in table
    private static class ScheduleWrapper {
        private String time;
        private String sunAvailability;
        private String monAvailability;
        private String tuesAvailability;
        private String wedAvailability;
        private String thursAvailability;
        private String friAvailability;
        private String satAvailability;

        public ScheduleWrapper(String time) {
            this.time = time;
            this.sunAvailability = "-";
            this.monAvailability = "-";
            this.tuesAvailability = "-";
            this.wedAvailability = "-";
            this.thursAvailability = "-";
            this.friAvailability = "-";
            this.satAvailability = "-";
        }

        public void setTime(String value) {
            this.time = value;
        }

        public String getTime() {
            return time;
        }

        /*
        public void setAvailability(String value) { this.availability = value; }
        public String getAvailability() { return availability; }
        */

        public void setDayAvailability(int day, String value) {
            switch(day) {
                case 0:
                    setSunAvailability(value);
                    break;
                case 1:
                    setMonAvailability(value);
                    break;
                case 2:
                    setTuesAvailability(value);
                    break;
                case 3:
                    setWedAvailability(value);
                    break;
                case 4:
                    setThursAvailability(value);
                    break;
                case 5:
                    setFriAvailability(value);
                    break;
                case 6:
                    setSatAvailability(value);
                    break;
                default:
                    System.out.println("You passed an invalid day while setting availability");
                    break;
            }
        }

        public String getDayAvailability(int day) {
            switch(day) {
                case 1:
                    return sunAvailability;
                case 2:
                    return monAvailability;
                case 3:
                    return tuesAvailability;
                case 4:
                    return wedAvailability;
                case 5:
                    return thursAvailability;
                case 6:
                    return friAvailability;
                case 7:
                    return satAvailability;
                default:
                    return "You passed an invalid day while setting availability";
            }
        }

        public String getSunAvailability() {
            return sunAvailability;
        }

        public void setSunAvailability(String value) {
            this.sunAvailability = value;
        }

        public String getMonAvailability() {
            return monAvailability;
        }

        public void setMonAvailability(String monAvailability) {
            this.monAvailability = monAvailability;
        }

        public String getTuesAvailability() {
            return tuesAvailability;
        }

        public void setTuesAvailability(String tuesAvailability) {
            this.tuesAvailability = tuesAvailability;
        }

        public String getWedAvailability() {
            return wedAvailability;
        }

        public void setWedAvailability(String wedAvailability) {
            this.wedAvailability = wedAvailability;
        }

        public String getThursAvailability() {
            return thursAvailability;
        }

        public void setThursAvailability(String thursAvailability) {
            this.thursAvailability = thursAvailability;
        }

        public String getFriAvailability() {
            return friAvailability;
        }

        public void setFriAvailability(String friAvailability) {
            this.friAvailability = friAvailability;
        }

        public String getSatAvailability() {
            return satAvailability;
        }

        public void setSatAvailability(String satAvailability) {
            this.satAvailability = satAvailability;
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
    public Label resInfoLbl, inputErrorLbl, schedLbl;

    @FXML
    private SVGPath classroom1, classroom2, classroom3, classroom4, classroom5, classroom6, classroom7, classroom8, classroom9, auditorium;

    private Event event ;    // The current event

    // Map Stuff
    static final Color AVAILABLE_COLOR = Color.rgb(67, 160, 71,0.6);
    static final Color UNAVAILABLE_COLOR = Color.rgb(255, 82, 59, 0.6);
    static final Color SELECT_AVAILABLE_COLOR = Color.rgb(67, 160, 71,0.9);
    static final Color SELECT_UNAVAILABLE_COLOR = Color.rgb(255, 82, 59, 0.9);
    ArrayList<Node> nodeCollection = new ArrayList<Node>();
    ArrayList<SVGPath> shapeCollection = new ArrayList<SVGPath>();

    // Table display information
    private int openTime = 9;   // hour to start schedule display
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private int timeStep = 2;    // Fractions of an hour
    private int timeStepMinutes = 60 / timeStep;    // In Minutes

    // Currently selected location
    public ReservableSpace currentSelection;
    private HashMap<Node, ReservableSpace> nodeToResSpace = new HashMap<>();
    // List of ints representing time blocks, where 0 is available and 1 is booked
    private ArrayList<ArrayList<Integer>> weeklySchedule;
    // LIst of spaces to display
    private ObservableList<ReservableSpace> resSpaces;
    // Error messages
    private String timeErrorText = "Please enter a valid time - note that rooms are only available for booking 9 AM - 10 PM";
    private String availRoomsText = "Show Available Spaces";
    private String bookedRoomsText = "Show Booked Spaces";
    private String clearFilterText = "Clear Filter";
    private String conflictErrorText = "Please select times that do not conflict with currently scheduled times.";
    private String pastDateErrorText = "Please select a date that is not in the past.";
    // Database
    static DatabaseService myDBS = DatabaseService.getDatabaseService();


    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {
        setDefaultTimes();
        setUpArrayLists();

        //resInfoLbl.setText("");
        // Don't show errors yet
//        timeErrorLbl.setVisible(false);
        inputErrorLbl.setVisible(false);
        inputErrorLbl.setWrapText(true);


        // Create table columns, set what they display, and add to the table
        TableColumn<ScheduleWrapper, String> timeCol = new TableColumn<>("Time");
        TableColumn<ScheduleWrapper, String> sunday = new TableColumn<>("Sunday");
        TableColumn<ScheduleWrapper, String> monday = new TableColumn<>("Monday");
        TableColumn<ScheduleWrapper, String> tuesday = new TableColumn<>(" Tuesday");
        TableColumn<ScheduleWrapper, String> wednesday = new TableColumn<>("Wednesday");
        TableColumn<ScheduleWrapper, String> thursday = new TableColumn<>("Thursday");
        TableColumn<ScheduleWrapper, String> friday = new TableColumn<>("Friday");
        TableColumn<ScheduleWrapper, String> saturday = new TableColumn<>("Saturday");

        // Set up table
        scheduleTable.getColumns().addAll(timeCol, sunday, monday, tuesday, wednesday, thursday, friday, saturday);
        scheduleTable.setEditable(false);   // Schedule is not editable
        scheduleTable.setStyle("-fx-table-cell-border-color: black;");
        scheduleTable.setStyle("-fx-table-column-rule-color: black;");

        // Set up each column's value and color scheme
        for (int i = 0; i < scheduleTable.getColumns().size(); i++) {
            final int finInt = i;
            TableColumn<ScheduleWrapper, String> col = (TableColumn<ScheduleWrapper, String>) scheduleTable.getColumns().get(i);
            if (i == 0) {
                col.setPrefWidth(177);
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyStringWrapper(p.getValue().getTime());
                    }
                });
            }
            else {
                col.setPrefWidth(185);
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyStringWrapper(p.getValue().getDayAvailability(finInt));

                    }
                });
                col.setCellFactory(column -> {
                    return new TableCell<ScheduleWrapper, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                // Format date.
                                setText(item);

                                // Style all dates in March with a different color.
                                if (item.equals("-")) {
                                    setStyle("-fx-background-color: #98FB98");
                                } else {
                                    setTextFill(Color.BLACK);
                                    setStyle("-fx-background-color: #ff6347");
                                }
                            }
                        }
                    };
                });
            }
            col.setResizable(false);
        }

//        scheduleTable.setPrefHeight(900);
        showRoomSchedule(false);
        // Show map nodes
        populateMap();
        repopulateMap();

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
     * Set default times and date.
     */
    private void setDefaultTimes() {
        // Set default date to today's date
        LocalDate date = LocalDate.now();
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
    }

    /**
     * Set up arrayLists and reservation listview.
     */
    private void setUpArrayLists() {
        // Create arraylists
        weeklySchedule = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 7; i++) {
            weeklySchedule.add(new ArrayList<Integer>());
        }
        resSpaces = FXCollections.observableArrayList();

        //  Pull spaces from database, sort, add to list and listview
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) myDBS.getAllReservableSpaces();
        Collections.sort(dbResSpaces);
        for (ReservableSpace rs : dbResSpaces) {
            Node n = DatabaseService.getDatabaseService().getNode(rs.getLocationNodeID());
            nodeCollection.add(n);
            nodeToResSpace.put(n, rs);
        }

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
                    setOnMouseClicked(EventHandler -> {
                        showRoomSchedule(false);
                        repopulateMap();
                    });
                }
            }
        });

        // Select the first item and display its schedule
        reservableList.getSelectionModel().select(0);
        reservableList.getFocusModel().focus(0);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Listener to update listview of rooms and info label
     *
     * @param value
     */
    private void focusState(boolean value) {
        if (!value && validTimes(false)) {
            if (availRoomsBtn.getText().contains("ear")) {
                availRooms();
            } else if (bookedRoomsBtn.getText().contains("ear")) {
                bookedRooms();
            }
            showRoomSchedule(false);
            repopulateMap();

            // Display the current information
            changeResInfo();
        }
        repopulateMap();
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
     *
     * @param value
     */
    private void listFocus(boolean value) {
        if (!value) {
            // Display the current information
            changeResInfo();
        }
        repopulateMap();
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
     * switches final window of scheduler
     *
     * @throws Exception
     */
    public void showConfirsmationStage() throws Exception {
        Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.confirmScheduler);
        StageManager.changeExistingWindow(stage, root, "Confirm Reservations");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * On room button click, show the schedule for that room and date.
     */
    @FXML
    public void showRoomSchedule(boolean alreadySelected) {
        // Clear the previous schedule
        for (int i = 0; i < 7; i++) {
            weeklySchedule.get(i).clear();
        }

        // Get the selected location
        if (!alreadySelected) {
            ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();
            currentSelection = curr;
        }

        // Get that date and turn it into gregorian calendars to pass to the database
        LocalDate chosenDate = datePicker.getValue();

        // set label of weekly scheduler based on date
        String date = chosenDate.toString();
        String name = currentSelection.getSpaceName();

        // TODO: 2019-04-16 fix date print out
        // format date
        //schedLbl.setTextFill(Color.WHITE);
        GregorianCalendar calendar = new GregorianCalendar();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        String formattedDate = dateFormat.format(new Date());
        int date1 = chosenDate.getDayOfMonth();
        String day1 = chosenDate.getDayOfWeek().toString();

        String month = chosenDate.getMonth().toString();
        String curTime = String.format(formattedDate + "\n" + day1 + ", the %02dth of " + month, date1);

        schedLbl.setText("Book " + name + "\nfor the Week of\n" + date);
        schedLbl.setTextAlignment(TextAlignment.CENTER);


        // Make a list of time and activity labels for the schedule
        ArrayList<ScheduleWrapper> schedToAdd = new ArrayList<>();

        // For every hour between the time the room closes and the time it opens
        for (int i = openTime; i < closeTime; i++) {
            String amPm = "AM";    // Half of the day
            if (((int) i / 12) == 1) {    // If in the afternoon, use PM
                amPm = "PM";
            }
            inputErrorLbl.setVisible(true);

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
                for (int day = 0; day < 7; day++) {
                    weeklySchedule.get(day).add(0);    // Default is 0, available
                }
            }
        }

        LocalDate selectedDate = datePicker.getValue();
        int selectedDayOfWeek = datePicker.getValue().getDayOfWeek().getValue();    // 1 is Monday, 7 is Sunday
        if (selectedDayOfWeek != 7) {
            selectedDate = selectedDate.plus(-selectedDayOfWeek, ChronoUnit.DAYS);
        }
        LocalDate startDate = selectedDate;
        // Populate each day's availability in the weekly schedule
        for (int dailySchedule = 0; dailySchedule < 7; dailySchedule++) {
            LocalDate secondDate = startDate.plus(1, ChronoUnit.DAYS);
            GregorianCalendar gcalStartDay = GregorianCalendar.from(startDate.atStartOfDay(ZoneId.systemDefault()));
            GregorianCalendar gcalEndDay = GregorianCalendar.from(secondDate.atStartOfDay(ZoneId.systemDefault()));

            System.out.println("Res start and end dates: " + gcalStartDay.toInstant() + gcalEndDay.toInstant());
            // Get reservations for this day
            ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(currentSelection.getSpaceID(), gcalStartDay, gcalEndDay);

            for (Reservation res : reservations) {
                // Get the start time
                int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
                int startFrac = startMinutes / (int) (timeStepMinutes);

                // Get the end time
                int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
                int endFrac = endMinutes / (int) (timeStepMinutes);

                // For every time between the start and end of the reservation,
                // Mark it as booked, color it red, and display the event name
                // or "Booked" depending on its privacy level
                for (int box = (startHour - openTime) * timeStep + startFrac; box < (endHour - openTime) * timeStep + endFrac; box++) {
                    // gets the time slot?
                    ScheduleWrapper time = schedToAdd.get(box);
                    if (res.getPrivacyLevel() == 0) {
                        //time.setAvailability(res.getEventName());
                        //if (res.getStartTime().get(Calendar.DAY_OF_WEEK)
                        time.setDayAvailability(dailySchedule, res.getEventName());
                        // if private event
                    } else {
                        time.setDayAvailability(dailySchedule, "Booked");
                    }
                    // what does this do (set to booked?)
                    weeklySchedule.get(dailySchedule).set(box, 1);
                }
            }
            startDate = startDate.plus(1, ChronoUnit.DAYS);

        }

        ObservableList<ScheduleWrapper> wrap = FXCollections.observableArrayList();
        // schedToAdd = an array list of ScheduleWrapper
        wrap.addAll(schedToAdd);
        scheduleTable.setItems(wrap);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() throws Exception {
        boolean valid = validTimes(true);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent() ;

        // If evreything is okay, create the reservation
        if (valid) {
            // pass relevant info to next screen using event bus

            // Get the times and dates and turn them into gregorian calendars
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();

            // post event to pass times
            event.setEventName("times");
            event.setStartAndEndTimes(cals);
            System.out.println("Calendars being passed: " + event.getStartAndEndTimes());
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);

            // post event to pass room id
            event.setEventName("room");
            event.setRoomId(currentSelection.getSpaceID());
            System.out.println("Room id being passed: " + event.getRoomId());
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);

            // switch screen to final stage of scheduler
            Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(ResourceLoader.confirmScheduler);
            StageManager.changeExistingWindow(stage, root, "Confirm Reservations");
        } else {
            repopulateMap();
        }

    }


    /**
     * Make gregorian calendars from the currently selected date and time.
     *
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
        showRoomSchedule(false);
        repopulateMap();
        inputErrorLbl.setVisible(false);
//        timeErrorLbl.setVisible(false);
//        resInfoLbl.setText("");
    }


    /**
     * Check whether the selected times are valid. If not, return false.
     * Valid times must: be within the chosen location's start and end times.
     * Have an end time greater than the start time.
     * Not conflict with any existing reservation.
     * Be in the future.
     *
     * @return true if the selected times are valid, false otherwise
     */
    private boolean validTimes(boolean forRes) {    // todo: broken. or something is broken.

        // reset label
        inputErrorLbl.setText("");

        makeMinutesValid();
        // Get the selected times
        int start = startTimePicker.getValue().getHour();
        int mins = startTimePicker.getValue().getMinute() / (timeStepMinutes);
        int index = (start - openTime) * timeStep + mins;
        int end = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute() / (timeStepMinutes);
        int endIndex = (end - openTime) * timeStep + endMins;

        // If the chosen date is in the past, show an error
        if (forRes && datePicker.getValue().atStartOfDay().isBefore(LocalDate.now().atStartOfDay())) {
//            timeErrorLbl.setText(pastDateErrorText);
//            timeErrorLbl.setVisible(true);
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(pastDateErrorText);
            return false;
        }

        // If the times are outside the location's open times
        // or end is greater than start, the times are invalid
        if (endIndex <= index || start < openTime || closeTime < end) {
//            timeErrorLbl.setText(timeErrorText);
//            timeErrorLbl.setVisible(true);
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(timeErrorText);
            return false;
        }

        if (forRes) {
            // For each time in the reservation, check whether it is already booked
            ArrayList<Integer> thisDay = weeklySchedule.get(datePicker.getValue().getDayOfWeek().getValue()-1);
            for (int i = index; i < endIndex; i++) {
                if (thisDay.get(i) == 1) {    // If so, show an error
//                    timeErrorLbl.setText(conflictErrorText);
//                    timeErrorLbl.setVisible(true);
                    inputErrorLbl.setVisible(true);
                    inputErrorLbl.setText(conflictErrorText);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * If the minutes selected are not an available time step
     * (ex. if timeStep is 2, so you can only select half-hour time incremenets,
     * then :30 and :00 are valid but :04 and :15, etc. are not),
     * round them down to the closest time step.
     */
    private void makeMinutesValid() {
        // If the start minutes selected are not divisible by the timeStep (ex. 30 minutes)
        if (startTimePicker.getValue().getMinute() % (timeStepMinutes) != 0) {
            // Then round them down to the nearest timeStep
            int minutes = ((int) startTimePicker.getValue().getMinute() / (timeStepMinutes)) * (timeStepMinutes);
            startTimePicker.setValue(LocalTime.of(startTimePicker.getValue().getHour(), minutes));
        }
        // If the end minutes selected are not divisible by the timeStep (ex. 30 minutes)
        if (endTimePicker.getValue().getMinute() % (timeStepMinutes) != 0) {
            // Rund them down to the nearest timeStep
            int minutes = ((int) endTimePicker.getValue().getMinute() / (timeStepMinutes)) * (timeStepMinutes);
            endTimePicker.setValue(LocalTime.of(endTimePicker.getValue().getHour(), minutes));
        }
    }

    /**
     * Searches for reservable space
     *
     * @param e
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = searchBar.getText();
        filterList(search);
    }

    /**
     * Filters the ListView based on the string
     */
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            reservableList.setItems(resSpaces);
            System.out.println(resSpaces);
        }
        else {
            //Get List of all nodes
            ObservableList<ReservableSpace> original = resSpaces;
            System.out.println(resSpaces);

            //Get Sorted list of nodes based on search value
            List<ExtractedResult> filtered = FuzzySearch.extractSorted(findStr, convertList(original, ReservableSpace::getSpaceName), 75);

            // Map to nodes based on index
            Stream<ReservableSpace> stream = filtered.stream().map(er -> {
                return original.get(er.getIndex());
            });

            // Convert to list and then to observable list
            List<ReservableSpace> filteredSpaces = stream.collect(Collectors.toList());
            ObservableList<ReservableSpace> toShow = FXCollections.observableList(filteredSpaces);

            // Add to view
            reservableList.setItems(toShow);
        }
    }

    /**
     * for lists
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
            bookedRoomsBtn.setOnAction(EventHandler -> {
                bookedRooms();
            });

            Collections.sort(availBetween);
            resSpaces.clear();
            resSpaces.addAll(availBetween);
            reservableList.setItems(resSpaces);

            // Clear the current schedule
            reservableList.getSelectionModel().select(0);
            reservableList.getFocusModel().focus(0);
            showRoomSchedule(false);
        }
    }

    /**
     * Display booked rooms for currently selected date and times
     */
    public void bookedRooms() {
        boolean valid = validTimes(false);
        if (valid) {
            repopulateMap();
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
            availRoomsBtn.setOnAction(EventHandler -> {
                availRooms();
            });

            reservableList.setItems(resSpaces);

            // Clear the current schedule
            reservableList.getSelectionModel().select(0);
            reservableList.getFocusModel().focus(0);
            showRoomSchedule(false);
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
        showRoomSchedule(false);
        repopulateMap();

        availRoomsBtn.setOnAction(EventHandler -> {
            availRooms();
        });
        bookedRoomsBtn.setOnAction(EventHandler -> {
            bookedRooms();
        });
        availRoomsBtn.setText(availRoomsText);
        bookedRoomsBtn.setText(bookedRoomsText);
        availRoomsBtn.setDisable(false);
        bookedRoomsBtn.setDisable(false);
    }


    /**
     * Currently unused:
     * returns a list of roomIDs which have a max capacity of less than nPeople
     *
     * @param nPeople
     * @return
     */
    ArrayList<String> getMaxPeople(int nPeople) {
        ArrayList<String> a = new ArrayList<>();
        return a;
    }

    // MAP STUFF DOWN HERE ****************

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

    private void populateMap() {
        System.out.println("**************** POPULATE MAP");
        ArrayList<ReservableSpace> bookedRS = getBookedNodes();
        shapeCollection = new ArrayList<SVGPath>();
        shapeCollection.addAll(Arrays.asList(auditorium, classroom4, classroom6, classroom8, classroom1, classroom2, classroom3, classroom5, classroom7, classroom9));
        for (int i = 0; i < nodeCollection.size(); i++) {
            Node node = nodeCollection.get(i);
            SVGPath svg = shapeCollection.get(i);
            svg.setStroke(Color.BLACK);    // todo: change colors
            svg.setFill(AVAILABLE_COLOR);
            svg.setStrokeWidth(0);
            svg.setStrokeType(StrokeType.INSIDE);
            svg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    currentSelection = nodeToResSpace.get(node);
                    showRoomSchedule(true);
                    repopulateMap();
                }
            });
            shapeCollection.add(svg);
        }
    }

    /**
     * Recolor the shapes on the map for the given time if they are booked.
     */
    private void repopulateMap() {
        System.out.println("**************** REPOPULATE MAP");
        ArrayList<ReservableSpace> bookedRS = getBookedNodes();
        for (int i = 0; i < nodeCollection.size(); i++) {
            Node node = nodeCollection.get(i);
            SVGPath svg = shapeCollection.get(i);
            svg.setStrokeWidth(0);
            if (isNodeInReservableSpace(bookedRS, node)) {
                svg.setFill(UNAVAILABLE_COLOR);
            } else {
                svg.setFill(AVAILABLE_COLOR);
            }
            if (nodeToResSpace.get(node).equals(currentSelection)) {
                svg.setStrokeWidth(1);
                if (svg.getFill().equals(AVAILABLE_COLOR)) {
                    svg.setFill(SELECT_AVAILABLE_COLOR);
                }
                else {
                    svg.setFill(SELECT_UNAVAILABLE_COLOR);
                }
            }
        }
    }

    @FXML
    void mapClickedHandler(MouseEvent e) {
        System.out.println("x: " + e.getX() + ", y: " + e.getY());
    }

    // Admin functionality, ideally
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
}
