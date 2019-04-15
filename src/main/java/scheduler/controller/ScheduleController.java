package scheduler.controller;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application_state.ApplicationState;
import application_state.Event;
import application_state.EventBusFactory;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.*;
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

public class ScheduleController {


    private static class ScheduleWrapper {
        private String time;
        //private String availability;
        // could i change availability to sun - sat avavail?
        // if works - changed to private and add getters/setters
        public String sunAvailability;
        public String monAvailability;
        public String tuesAvailability;
        public String wedAvailability;
        public String thursAvailability;
        public String friAvailability;
        public String satAvailability;




        // color
       public String color ;




        public ScheduleWrapper(String time) {
            this.time = time;
            //this.availability = "Available";
            // me fucking around
            this.sunAvailability = "-" ;
            this.monAvailability = "-" ;
            this.tuesAvailability = "-" ;
            this.wedAvailability = "-" ;
            this.thursAvailability = "-" ;
            this.friAvailability = "-" ;
            this.satAvailability = "-" ;

        }

        public void setTime(String value) { this.time = value; }
        public String getTime() { return time; }

        /*
        public void setAvailability(String value) { this.availability = value; }
        public String getAvailability() { return availability; }
        */

        public String getSunAvailability() { return sunAvailability; }
        public void setSunAvailability(String value) {this.sunAvailability = value ; }


        public String getMonAvailability() { return monAvailability; }
        public void setMonAvailability(String monAvailability) { this.monAvailability = monAvailability; }

        public String getTuesAvailability() { return tuesAvailability; }
        public void setTuesAvailability(String tuesAvailability) { this.tuesAvailability = tuesAvailability; }

        public String getWedAvailability() { return wedAvailability; }
        public void setWedAvailability(String wedAvailability) { this.wedAvailability = wedAvailability; }

        public String getThursAvailability() { return thursAvailability; }
        public void setThursAvailability(String thursAvailability) { this.thursAvailability = thursAvailability; }

        public String getFriAvailability() { return friAvailability; }
        public void setFriAvailability(String friAvailability) { this.friAvailability = friAvailability; }

        public String getSatAvailability() { return satAvailability; }
        public void setSatAvailability(String satAvailability) { this.satAvailability = satAvailability; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color /*this.setStyle("-fx-background-color: #98FB98")*/;
            ; }


        /*
        @Override
        public String toString() {
            return "ScheduleWrapper{" +
                    "time='" + time + '\'' +
                    ", availability='" + availability + '\'' +
                    '}';
        }
        */
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


    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();


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

        // add event bus to pass info to the final 'make reservation' screen
        eventBus.register(this);


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




        //resInfoLbl.setText("");
        timeErrorText = "Please enter a valid time - note that rooms are only available for booking 9 AM - 10 PM";
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







        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Select the first item and display its schedule
        reservableList.getSelectionModel().select(0);
        reservableList.getFocusModel().focus(0);



        // Create table columns, set what they display, and add to the table
        TableColumn<ScheduleWrapper, String> timeCol = new TableColumn<>("Time");
        TableColumn<ScheduleWrapper, String> sunday = new TableColumn<>("Sunday");
        TableColumn<ScheduleWrapper, String> monday = new TableColumn<>("Monday");
        TableColumn<ScheduleWrapper, String> tuesday = new TableColumn<>(" Tuesday");
        TableColumn<ScheduleWrapper, String> wednesday = new TableColumn<>("Wednesday");
        TableColumn<ScheduleWrapper, String> thursday = new TableColumn<>("Thursday");
        TableColumn<ScheduleWrapper, String> friday = new TableColumn<>("Friday");
        TableColumn<ScheduleWrapper, String> saturday = new TableColumn<>("Saturday");

        // not doing anything??
        // set max width for each col
        timeCol.setPrefWidth(124);
        sunday.setPrefWidth(126);
        monday.setPrefWidth(126);
        tuesday.setPrefWidth(126);
        wednesday.setPrefWidth(126);
        thursday.setPrefWidth(126);
        friday.setPrefWidth(126);
        saturday.setPrefWidth(126);


        // make sure each column is uneditable
        timeCol.setResizable(false);
        sunday.setResizable(false);
        monday.setResizable(false);
        tuesday.setResizable(false);
        wednesday.setResizable(false);
        thursday.setResizable(false);
        friday.setResizable(false);
        saturday.setResizable(false);


        // cannot edit!
        scheduleTable.setEditable(false);



        // HERE!! IMPORTANT!! MAYBE!!
        // i have no idea what this means (but im going to apply to all new cols anyways - whoever wrote pls help
        timeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return new ReadOnlyStringWrapper(p.getValue().getTime());
            }
        });
        sunday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                sunday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().sunAvailability);

            }
        });
        monday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                monday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().monAvailability);

            }
        });
        tuesday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                tuesday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().tuesAvailability);

            }
        });
        wednesday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                wednesday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().wedAvailability);

            }
        });
        thursday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                thursday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().thursAvailability);

            }
        });
        friday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                friday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().friAvailability);

            }
        });
        saturday.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                //return new ReadOnlyStringWrapper(p.getValue().getAvailability());
                saturday.setStyle("-fx-background-color: #98FB98");
                return new ReadOnlyStringWrapper(p.getValue().satAvailability);

            }
        });


        scheduleTable.getColumns().addAll(timeCol, sunday, monday, tuesday, wednesday, thursday, friday, saturday);
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



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





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
     *
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
    public void showConfirmationStage() throws Exception {
        Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.confirmScheduler);
        StageManager.changeExistingWindow(stage, root, "Confirm Reservations");
    }







    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * On room button click, show the schedule for that room and date.
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
                currentSchedule.add(0);    // Default is 0, available
            }
        }

        // Get reservations for this space and these times
        ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);




        ///////// WORK IN PROGRESS FOR WEEKLY CALENDAR DISPLAY ///////////////////////////



        // list of reservations for this space for the week of the selected time
        ArrayList<Reservation> reservationsForWeek = reservations ;

        System.out.println("gCalStart1 = " + gcalStart.toInstant());
        // new gcal to reset time
        GregorianCalendar gcalStart2 = gcalStart ;
        System.out.println("gcalStart2 = " + gcalStart2.toInstant());
        GregorianCalendar gcalEnd2 = gcalEnd ;
        System.out.println("gcalEnd2 = " + gcalEnd2.toInstant());

        // get reservations for surrounding days
        int day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
        System.out.println("DAY OF WEEK = " + gcalStart.get(Calendar.DAY_OF_WEEK));
        // always want to get full week
        int newStartDay = 1;
        int newEndDay = 7 ;

        // set correct start week (so it doesnt switch)

        // set corect start day of week
        gcalStart2.set(Calendar.DAY_OF_WEEK, newStartDay) ;

        System.out.println("DAY OF WEEK = " + gcalStart.get(Calendar.DAY_OF_WEEK));
        // if sunday do not subtract 1
        if(day == 1 ) {
            gcalStart2.set(Calendar.WEEK_OF_MONTH, gcalStart2.get(Calendar.WEEK_OF_MONTH) ) ;

        } else if(day != 1 ) {
            System.out.println("Has entered else - noice");
            gcalStart2.set(Calendar.WEEK_OF_MONTH, gcalStart2.get(Calendar.WEEK_OF_MONTH) - 1 ) ;

        }

        // else (any other week) subtract 1 to keep the correct week number

       // System.out.println("GCALSTART2 WEEK OF MONTH = " + gcalStart2.get(Calendar.WEEK_OF_MONTH));
        //gcalStart2.set(Calendar.WEEK_OF_MONTH, gcalStart2.get(Calendar.WEEK_OF_MONTH) - 1 ) ;
        System.out.println("GCALSTART2 NEW WEEK OF MONTH = " + gcalStart2.get(Calendar.WEEK_OF_MONTH));

        gcalEnd2.set(Calendar.DAY_OF_WEEK, newEndDay) ;

        System.out.println("new gCalStart2 = " + gcalStart2.toInstant());
        System.out.println("new gCalEnd2 = " + gcalEnd2.toInstant());

        ArrayList<Reservation> databaseRes = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart2, gcalEnd2) ;

        // addall?
        //reservationsForWeek = databaseRes ;
        reservationsForWeek.addAll(databaseRes) ;


        //ArrayList<Reservation> databaseRes = reservations;
        // switch case based on day of week
        /*
        switch(day) {
            // if day is a sunday

            case 1:
                // get reservations for following mon - sat
                // for sunday - week start on that day
                //day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                //newStartDay = day ;
                newStartDay = 1 ;
                // week ends 6 days after
                //newEndDay = day + 6 ;
                newEndDay = 7 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
               // databaseRes = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd) ;

                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                System.out.println("SUNDAY BLOCK");
                reservationsForWeek.addAll(databaseRes );
                System.out.println("RESERVATIONS FOR WEEK: " + reservationsForWeek);

                break ;
            case 2:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                // get reservations for following mon - sat
                System.out.println("MONDAY BLOCK");
                // week starts on sunday (monday - 1)

                //newStartDay = day - 1 ;
                //newEndDay = day + 5 ;

                newStartDay = 1 ;
                // week ends 6 days after
                //newEndDay = day + 6 ;
                newEndDay = 7 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );

                System.out.println("RESERVATIONS FOR WEEK: " + reservationsForWeek);

                break ;
            case 3:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                System.out.println("TUESDAY BLOCK");

                //newStartDay = day - 2 ;
                //newEndDay = day + 4 ;
                newStartDay = 1 ;
                // week ends 6 days after
                //newEndDay = day + 6 ;
                newEndDay = 7 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );

                System.out.println("RESERVATIONS FOR WEEK: " + reservationsForWeek);
                break ;
            case 4:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                newStartDay = day - 3 ;
                newEndDay = day + 3 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );
                break ;
            case 5:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                newStartDay = day - 4 ;
                newEndDay = day + 2 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );
               break ;
            case 6:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                newStartDay = day - 5 ;
                newEndDay = day + 1 ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );
                break ;
            case 7:
                day = gcalStart.get(Calendar.DAY_OF_WEEK) ;
                newStartDay = day - 6 ;
                newEndDay = day ;
                gcalEnd.set(Calendar.DAY_OF_WEEK, newEndDay) ;
                System.out.println("end day of week: " + gcalEnd.get(Calendar.DAY_OF_WEEK));
                databaseRes.addAll((ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd)) ;
                // reservationsForWeek = databaseRes ;
                reservationsForWeek.addAll(databaseRes );
                break ;
            default:
                System.out.println("should never get here");
                break ;
        }
        */

        System.out.println("reservation for week of selected time: " + reservationsForWeek) ;



        /**
         * For each of this location's reservations, mark it booked on the schedule
         */
        // CHNAGED FROM RESERVATION
        for (Reservation res : reservationsForWeek) {
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
                    switch (res.getStartTime().get(Calendar.DAY_OF_WEEK)) {
                        case 1:
                            System.out.println("inside sun avail switch");
                            time.setSunAvailability(res.getEventName());
                            break ;
                        case 2:
                            System.out.println("inside mon avail switch");

                            time.setMonAvailability(res.getEventName());
                            break ;
                        case 3:
                            System.out.println("inside tues avail switch");

                            time.setTuesAvailability(res.getEventName());
                            break ;
                        case 4:
                            time.setWedAvailability(res.getEventName());
                            break ;
                        case 5:
                            time.setThursAvailability(res.getEventName());
                            break ;
                        case 6:
                            time.setFriAvailability(res.getEventName());
                            break ;
                        case 7:
                            time.setSatAvailability(res.getEventName());
                            break ;
                        default:
                            System.out.println("should never reach here - panic if you see this message");
                            break ;
                    }


                } else {
                    switch (res.getStartTime().get(Calendar.DAY_OF_WEEK)) {
                        case 1:
                            time.setSunAvailability("Booked");
                            //time.setSunAvailability("");
                            // set to red
                            //TableCell cell = new TableCell()

                            break ;
                        case 2:
                            time.setMonAvailability("Booked");
                            time.setColor("red") ;
                            break ;
                        case 3:
                            time.setTuesAvailability("Booked");
                            break ;
                        case 4:
                            time.setWedAvailability("Booked");
                            break ;
                        case 5:
                            time.setThursAvailability("Booked");
                            break ;
                        case 6:
                            time.setFriAvailability("Booked");
                            break ;
                        case 7:
                            time.setSatAvailability("Booked");
                            break ;
                        default:
                            System.out.println("should never reach here - panic if you see this message");
                            break ;
                    }
                    //time.setAvailability("Booked");
                    // time.setSunAvailability("Booked");


/////////////////
                    /*
                    // Set the cell to display only the name of the reservableSpace
                    scheduleTable.cellFactoryProperty(param -> new ListCell<ReservableSpace>() {
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
                    */
///////////////////////////////////////


                }
                // what does this do (set to booked?)
                currentSchedule.set(box, 1);
            }
        }

        ObservableList<ScheduleWrapper> wrap = FXCollections.observableArrayList();
        // schedToAdd = and array list of ScheduleWrapper
        wrap.addAll(schedToAdd);
        // where??? what columns and how can i change that???
        scheduleTable.setItems(wrap);
        for (int i = 0; i < schedToAdd.size(); i++) {
            System.out.println(scheduleTable.getItems().get(i));
        }



    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() throws Exception {

        boolean valid = validTimes(true);


        // If evreything is okay, create the reservation
        if (valid) {

            // pass relevant info to next screen using event bus

            // Get the times and dates and turn them into gregorian calendars
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();


            // post event to pass times
            event.setEventName("times");
            event.setStartAndEndTimes(cals);
            System.out.println("Calendars being passed: " + event.getStartAndEndTimes());
            eventBus.post(event);


            // post event to pass room id
            event.setEventName("room");
            event.setRoomId(currentSelection.getSpaceID());
            System.out.println("Room id being passed: " + event.getRoomId());
            eventBus.post(event);

            // switch screen to final stage of scheduler
            Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(ResourceLoader.confirmScheduler);
            StageManager.changeExistingWindow(stage, root, "Confirm Reservations");

        }

        else {
            populateMap();
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
        showRoomSchedule();
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
    private boolean validTimes(boolean forRes) {

        // reset label
        inputErrorLbl.setText("");


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
            for (int i = index; i < endIndex; i++) {
                if (currentSchedule.get(i) == 1) {    // If so, show an error
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
     *
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
