package edu.wpi.cs3733d19.teamN.scheduler.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.EntryPopOverPane;
import com.calendarfx.view.popover.PopOverContentPane;
import com.calendarfx.view.popover.PopOverTitledPane;
import com.google.zxing.WriterException;
import com.jfoenix.controls.*;
import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import edu.wpi.cs3733d19.teamN.application_state.Event;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.scheduler.model.ReservableSpace;
import edu.wpi.cs3733d19.teamN.scheduler.model.Reservation;
import edu.wpi.cs3733d19.teamN.service.QRService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;


/**
 * Controls the schedule page of the application. Allows user to view a map of rooms and
 * their availability at the selected time and date, to view a list of all rooms,
 * to view a schedule of the selected room for the selected week, and to
 * view a schedule for the selected date for all rooms.
 * User can then make reservations for a selected location, date, start time,
 * and end time. Reservations must be valid and not conflict with any other reservations.
 */
public class ScheduleController {
    @FXML
    public JFXButton homeBtn, makeReservationBtn, availRoomsBtn, bookedRoomsBtn, deleteReservationBtn;
    @FXML
    public JFXTextField closeTimeTextField, openTimeTextField, minResTextField, snapToTextField;
    @FXML
    private JFXCheckBox closeTimeCheckBox, openTimeCheckBox, minResCheckBox, snapToCheckBox, recurringCheckBox, multidayCheckBox, showContactCheckBox;
    private JFXCheckBox sidePaneRecurrenceCheckBox;
    @FXML
    private TableView<ScheduleWrapper> scheduleTable, dailyScheduleAllRooms;
    @FXML
    private TableView<Reservation> resTable;
    @FXML
    private TableColumn<Reservation, String> eventCol, reserverCol, locationCol, eventStartCol, eventEndCol;
    @FXML
    public JFXListView reservableList;
    @FXML
    public JFXDatePicker datePicker;
    private JFXDatePicker endDatePicker, recurrenceDatePicker;
    @FXML
    public JFXTimePicker startTimePicker, endTimePicker;
    @FXML
    public Label resInfoLbl, inputErrorLbl, schedLbl, errorMessage;
    @FXML
    private SVGPath classroom1, classroom2, classroom3, classroom4, classroom5, classroom6, classroom7, classroom8, classroom9, auditorium;
    @FXML
    private VBox sidePaneVBox;
    @FXML
    private Region sidePaneRegion;
    @FXML
    private AnchorPane subSceneHolder;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Tab weeklyScheduleTab, dailyScheduleTab, settingsTab, allResTab;
    private CalendarView calendarView;
    private JFXComboBox recurrenceComboBox = new JFXComboBox();
    // Workstations
    @FXML
    private SVGPath ws1, ws2, ws3, ws4, ws5, ws6, ws7, ws8, ws9;
    @FXML
    private SVGPath ws10, ws11, ws12, ws13, ws14, ws15, ws16, ws17, ws18, ws19;
    @FXML
    private SVGPath ws20, ws21, ws22, ws23, ws24, ws25, ws26, ws27, ws28, ws29;
    @FXML
    private SVGPath ws30, ws31, ws32, ws33, ws34, ws35, ws36, ws37, ws38, ws39;
    @FXML
    private SVGPath ws40, ws41, ws42, ws43, ws44, ws45, ws46, ws47, ws48, ws49;
    @FXML
    private SVGPath ws50, ws51, ws52, ws53, ws54, ws55, ws56, ws57, ws58, ws59;
    @FXML
    private SVGPath ws60, ws61, ws62, ws63, ws64, ws65, ws66, ws67, ws68, ws69;
    @FXML
    private SVGPath ws70, ws71, ws72, ws73, ws74, ws75, ws76, ws77, ws78, ws79;
    @FXML
    private SVGPath ws80, ws81, ws82, ws83, ws84, ws85, ws86, ws87, ws88, ws89;
    @FXML
    private SVGPath ws90, ws91, ws92, ws93, ws94, ws95, ws96, ws97, ws98, ws99;
    @FXML
    private SVGPath ws100, ws101, ws102, ws103, ws104, ws105, ws106, ws107, ws108, ws109;
    @FXML
    private SVGPath ws110, ws111, ws112, ws113, ws114, ws115, ws116, ws117, ws118, ws119;
    @FXML
    private SVGPath ws120;
    // Map Display items
    public static final Color AVAILABLE_COLOR = Color.rgb(0, 160, 100,0.6);
    public static final Color UNAVAILABLE_COLOR = Color.rgb(255, 82, 30, 0.8);
    public static final Color SELECT_AVAILABLE_COLOR = Color.rgb(13, 160, 100,0.9);
    public static final Color SELECT_UNAVAILABLE_COLOR = Color.rgb(255, 82, 30, 0.9);
    private ArrayList<Node> nodeCollection = new ArrayList<Node>();
    private ArrayList<SVGPath> shapeCollection = new ArrayList<SVGPath>();
    private static ArrayList<SVGPath> workStations = new ArrayList<>();
    private Event event ;    // The current event
    private Thread randStationsThread;    // Random stations thread
    private WorkStationRunner runner;    // Stoppable part
    // Admin settings
    private int openTime = 9;   // hour to start schedule display
    private String openTimeStr = "09:00";
    private int openTimeMinutes = 0;
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private String closeTimeString = "22:00";
    private int closeTimeMinutes = 0;
    private int timeStep = 2;    // Fractions of an hour   todo
    private int timeStepMinutes = 60 / timeStep;    // In Minutes
    private int minRes = 0;    // Minimum reservation time todo
    private static final int NUM_ROOMS = 10;
    private static final int NUM_DAYS_IN_WEEK = 7;
    private boolean boundOpenTime = true;
    private boolean boundCloseTime = true;
    private boolean boundMinRes = true;
    private boolean snapToMinutes = true;
    private boolean allowMultidayRes = false;
    private boolean allowRecurringRes = false;
    private static boolean showContactInfo = true;
    // Currently selected location
    public ReservableSpace currentSelection;
    // Hashmap connecting reservable spaces to their location nodes
    private HashMap<Node, ReservableSpace> nodeToResSpace = new HashMap<>();
    // List of ints representing time blocks, where 0 is available and 1 is booked
    private ArrayList<ArrayList<Integer>> weeklySchedule, dailyScheduleAllRoomsInts;
    // LIst of spaces to display
    private ObservableList<ReservableSpace> resSpaces;
    // List of all reservable spaces
    private ObservableList<ReservableSpace> allResSpaces;
    // Error messages
    private String availRoomsText = "Show Available Spaces";
    private String bookedRoomsText = "Show Booked Spaces";
    private String clearFilterText = "Clear Filter";
    private String conflictErrorText = "Please select times that do not conflict with currently scheduled times.";
    private String pastDateErrorText = "Please select a date that is not in the past.";
    private String endDateErrorText = "Please select an end date that is not before start date.";
    private String recurrenceErrorText = "Please select a repeat-until date at or after the end of your reservation.";
    // Database
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    /**
     * Set up scheduler page.
     */
    @FXML
    public void initialize() {
        setDefaultTimes();
        setUpArrayLists();
        setUpWeeklyTable();
        setUpAllRoomsTable();
        getSettings();
        Platform.runLater(() -> {    // In order to speed up switching scenes
            try {
                setUpCalendar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Don't show errors yet
        inputErrorLbl.setVisible(false);
        inputErrorLbl.setWrapText(true);
        inputErrorLbl.setPrefWidth(450);

        // Set multiday components
        endDatePicker = new JFXDatePicker(LocalDate.now());
        endDatePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            focusState(newValue);
        });
        endDatePicker.setPrefWidth(256);
        if (allowMultidayRes) {
            sidePaneVBox.getChildren().add(3, endDatePicker);
            sidePaneRegion.setPrefHeight(98);
        }

        // Set recurrence components
        sidePaneRecurrenceCheckBox = new JFXCheckBox("Repeat Event");
        sidePaneRecurrenceCheckBox.setOnAction(e -> {
            boolean vis = recurrenceDatePicker.isVisible();
            recurrenceDatePicker.setVisible(!vis);
            recurrenceComboBox.setVisible(!vis);
            recurrenceComboBox.getSelectionModel().select(0);
            recurrenceDatePicker.setValue(LocalDate.now());
            if (vis) {
                sidePaneRecurrenceCheckBox.setText("Repeat Event");
            }
            else {
                sidePaneRecurrenceCheckBox.setText("Repeat Until");
            }
        });
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Daily",
                        "Weekly", "Monthly", "Yearly"
                );
        recurrenceComboBox.setItems(options);
        recurrenceDatePicker = new JFXDatePicker(LocalDate.now());
        recurrenceDatePicker.setPrefWidth(256);
        if (allowRecurringRes) {
            sidePaneVBox.setSpacing(20);
            int insert = sidePaneVBox.getChildren().indexOf(sidePaneRegion);
            sidePaneVBox.getChildren().remove(sidePaneRegion);
            sidePaneVBox.getChildren().add(insert, sidePaneRecurrenceCheckBox);
            sidePaneVBox.getChildren().add(insert + 1, recurrenceDatePicker);
            recurrenceDatePicker.setVisible(false);
            sidePaneVBox.getChildren().add(insert + 2, recurrenceComboBox);
            recurrenceComboBox.setVisible(false);
        }

        // Only allow admin to change settings. Note: doesn't change past reservations.
        tabPane.getTabs().remove(settingsTab);
        tabPane.getTabs().remove(allResTab);
        if (ApplicationState.getApplicationState().getEmployeeLoggedIn() != null && ApplicationState.getApplicationState().getEmployeeLoggedIn().isAdmin()) {
            tabPane.getTabs().add(allResTab);
            tabPane.getTabs().add(settingsTab);
            displaySettings();
            fillResTable();
        }

        // Populate tables
        showRoomSchedule(false);

        // Show map spaces
        populateMap();
        repopulateMap();
        randomWorkstations();
        runner = new WorkStationRunner();
        randomizeSpaces(true);

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
     * Get settings from Application state.
     */
    private void getSettings() {
        ApplicationState currState = ApplicationState.getApplicationState();
        openTime = currState.getOpenTime();   // hour to start schedule display
        openTimeStr = currState.getOpenTimeStr();
        openTimeMinutes = currState.getOpenTimeMinutes();
        closeTime = currState.getCloseTime();    // 24-hours hour to end schedule display
        closeTimeString = currState.getCloseTimeString();
        closeTimeMinutes = currState.getCloseTimeMinutes();
        timeStep = currState.getTimeStep();    // Fractions of an hour
        timeStepMinutes = 60 / timeStep;    // In Minutes
        boundOpenTime = currState.isBoundOpenTime();
        boundCloseTime = currState.isBoundCloseTime();
        boundMinRes = currState.isBoundMinRes();
        minRes = currState.getMinRes();
        snapToMinutes = currState.isSnapToMinutes();
        allowMultidayRes = currState.isAllowMultidayRes();
        allowRecurringRes = currState.isAllowRecurringRes();
        showContactInfo = currState.isShowContactInfo();
    }

    /**
     * Save settings to Application state.
     */
    private void saveSettings() {
        ApplicationState currState = ApplicationState.getApplicationState();
        currState.setOpenTime(openTime);   // hour to start schedule display
        currState.setOpenTimeStr(openTimeStr);
        currState.setOpenTimeMinutes(openTimeMinutes);
        currState.setCloseTime(closeTime);    // 24-hours hour to end schedule display
        currState.setCloseTimeString(closeTimeString);
        currState.setCloseTimeMinutes(closeTimeMinutes);
        currState.setTimeStep(timeStep);    // Fractions of an hour
        currState.setBoundOpenTime(boundOpenTime);
        currState.setBoundCloseTime(boundCloseTime);
        currState.setBoundMinRes(boundMinRes);
        currState.setSnapToMinutes(snapToMinutes);
        currState.setAllowMultidayRes(allowMultidayRes);
        currState.setAllowRecurringRes(allowRecurringRes);
        currState.setShowContactInfo(showContactInfo);
        currState.setMinRes(minRes);
    }

    /**
     * Set up calendar tab.
     */
    private void setUpCalendar() throws IOException {
        // Create calendarView and settings
        calendarView = new CalendarView();
        calendarView.setPrefWidth(1485);
        calendarView.setPrefHeight(915);
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
        calendarView.getStylesheets().add("default.css");

        // Create calendar for each room and set style
        Calendar amphitheaterCal = new Calendar("Amphitheater");
        Calendar classroom1Cal = new Calendar("Classroom 1");
        Calendar classroom2Cal = new Calendar("Classroom 2");
        Calendar classroom3Cal = new Calendar("Classroom 3");
        Calendar computer1Cal = new Calendar("Computer Room 1");
        Calendar computer2Cal = new Calendar("Computer Room 2");
        Calendar computer3Cal = new Calendar("Computer Room 3");
        Calendar computer4Cal = new Calendar("Computer Room 4");
        Calendar computer5Cal = new Calendar("Computer Room 5");
        Calendar computer6Cal = new Calendar("Computer Room 6");
        amphitheaterCal.setStyle(Style.STYLE1);
        amphitheaterCal.setShortName("AMPHI00001");
        classroom1Cal.setStyle(Style.STYLE2);
        classroom1Cal.setShortName("CLASS00001");
        classroom2Cal.setStyle(Style.STYLE3);
        classroom2Cal.setShortName("CLASS00002");
        classroom3Cal.setStyle(Style.STYLE4);
        classroom3Cal.setShortName("CLASS00003");
        computer1Cal.setStyle(Style.STYLE5);
        computer1Cal.setShortName("COMPU00001");
        computer2Cal.setStyle(Style.STYLE6);
        computer2Cal.setShortName("COMPU00002");
        computer3Cal.setStyle(Style.STYLE7);
        computer3Cal.setShortName("COMPU00003");
        computer4Cal.setStyle("style8");
        computer4Cal.setShortName("COMPU00004");
        computer5Cal.setStyle("style9");
        computer5Cal.setShortName("COMPU00005");
        computer6Cal.setStyle("style10");
        computer6Cal.setShortName("COMPU00006");

        // Group calendars by source and add to calendarView
        CalendarSource myCalendarSource = new CalendarSource("Amphitheater");
        myCalendarSource.getCalendars().addAll(amphitheaterCal);
        CalendarSource classRoomSource = new CalendarSource("Classrooms");
        classRoomSource.getCalendars().addAll(classroom1Cal, classroom2Cal, classroom3Cal);
        CalendarSource computerRoomSource = new CalendarSource("Computer Rooms");
        computerRoomSource.getCalendars().addAll(computer1Cal, computer2Cal, computer3Cal, computer4Cal, computer5Cal, computer6Cal);
        calendarView.getCalendarSources().setAll(myCalendarSource, classRoomSource, computerRoomSource);

        // Keep the calendarView updated
        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                System.out.println(this);
                calendarView.setRequestedTime(LocalTime.now());
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
        };
        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

        // Set the calendarView details-popup (on double-click of event)
        calendarView.setEntryDetailsPopOverContentCallback(new MyEntryPopOverContentProvider());
        populateCalendar();
        Platform.runLater(() -> {    // In order to speed up switching scenes
            subSceneHolder.getChildren().add(calendarView);
        });
    }

    /**
     * Show reservations in calendar.
     */
    private void populateCalendar() {
        for (int s = 0; s < calendarView.getCalendarSources().size(); s++) {    // For each calendar source
            CalendarSource source = calendarView.getCalendarSources().get(s);
            for (int i = 0; i < source.getCalendars().size(); i++) {    // For each calendar
                Calendar currCal = source.getCalendars().get(i);
                currCal.setReadOnly(true);    // Don't let users edit it
                ArrayList<Reservation> roomRes = (ArrayList<Reservation>) myDBS.getReservationsBySpaceId(currCal.getShortName());
                for (Reservation r : roomRes) {     // Get each reservation for that calendar
                    GregorianCalendar startCal = r.getStartTime();
                    GregorianCalendar endCal = r.getEndTime();
                    LocalDateTime startLDT = startCal.toZonedDateTime().toLocalDateTime();
                    LocalDateTime endLDT = endCal.toZonedDateTime().toLocalDateTime();
                    LocalDate startDate = startLDT.toLocalDate();
                    LocalDate endDate = endLDT.toLocalDate();
                    LocalTime startTime = startLDT.toLocalTime();
                    LocalTime endTime = endLDT.toLocalTime();
                    Interval time = new Interval(startDate, startTime, endDate, endTime);
                    String name = r.getEventName();
                    if (r.getPrivacyLevel() != 0) {
                        name = "Booked";
                    }
                    Entry e = new Entry(name, time);    // Create an entry for that reservation and add it to the calendar
                    e.setLocation(currCal.getName());
                    e.setId(Integer.toString(r.getEventID()));
                    currCal.addEntry(e);
                }
            }
        }
    }

    /**
     * If admin, display settings tab.
     */
    private void displaySettings() {
        showContactCheckBox.setSelected(showContactInfo);
        if (showContactInfo) {
            showContactCheckBox.setText("On");
        }
        else {
            showContactCheckBox.setText("Off");
        }
        recurringCheckBox.setSelected(allowRecurringRes);
        if (allowRecurringRes) {
            recurringCheckBox.setText("On");
        }
        else {
            recurringCheckBox.setText("Off");
        }
        multidayCheckBox.setSelected(allowMultidayRes);
        if (allowMultidayRes) {
            multidayCheckBox.setText("On");
        }
        else {
            multidayCheckBox.setText("Off");
        }
        snapToCheckBox.setSelected(snapToMinutes);
        if (snapToMinutes) {
            snapToCheckBox.setText("On");
            snapToTextField.setVisible(true);
        }
        else {
            snapToCheckBox.setText("Off");
            snapToTextField.setVisible(false);
        }
        minResCheckBox.setSelected(boundMinRes);
        if (boundMinRes) {
            minResCheckBox.setText("Bound");
        }
        else {
            minResCheckBox.setText("Unbound");
            minResTextField.setVisible(false);
        }
        closeTimeCheckBox.setSelected(boundCloseTime);
        if (boundCloseTime) {
            closeTimeCheckBox.setText("Bound");
        }
        else {
            closeTimeCheckBox.setText("Unbound");
            closeTimeTextField.setVisible(false);
        }
        openTimeCheckBox.setSelected(boundOpenTime);
        if (boundOpenTime) {
            openTimeCheckBox.setText("Bound");
        }
        else {
            openTimeCheckBox.setText("Unbound");
            openTimeTextField.setVisible(false);
        }
        // Set listeners for time to show current settings
        openTimeTextField.setText(openTimeStr);
        openTimeTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        closeTimeTextField.setText(closeTimeString);
        closeTimeTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        minResTextField.setText(minRes + "");
        minResTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        snapToTextField.setText(timeStepMinutes + "");
        snapToTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        errorMessage.setVisible(false);
        errorMessage.setText("Please enter a valid time in the format hh:mm.\n" +
                "For minimum reservation and snap to minutes, \nenter a number of minutes between 1 and 60\nthat is a divisor of 60 as mm.");
    }

    /**
     * Listener to update setting times
     * @param value whether it is focused or not
     */
    private void timeListener(boolean value) {
        // If this was selected and loses selection, show the room schedule based on the current filters
        if (!value) {
            closeTimeTextField.setText(closeTimeString);
            openTimeTextField.setText(openTimeStr);
            minResTextField.setText(minRes + "");
            snapToTextField.setText(timeStepMinutes + "");
            errorMessage.setVisible(false);
        }
    }

    /**
     * Randomly color spaces.
     * @param start  true to start, false to end
     */
    private void randomizeSpaces(boolean start) {
        randStationsThread = new Thread(runner, "T1");
        randStationsThread.setPriority(Thread.MIN_PRIORITY);
        randStationsThread.setDaemon(true);
        if (start) {
            randStationsThread.start();
        }
        else {
            runner.stop();
        }
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
        // Create arraylists for tables
        weeklySchedule = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            weeklySchedule.add(new ArrayList<Integer>());
        }
        dailyScheduleAllRoomsInts = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < NUM_ROOMS; i++) {
            dailyScheduleAllRoomsInts.add(new ArrayList<Integer>());
        }

        // Initialize arrayLists
        resSpaces = FXCollections.observableArrayList();
        allResSpaces = FXCollections.observableArrayList();

        //  Pull spaces from database, sort, add to list and listview
        ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) myDBS.getAllReservableSpaces();
        Collections.sort(dbResSpaces);
        for (ReservableSpace rs : dbResSpaces) {
            Node n = DatabaseService.getDatabaseService().getNode(rs.getLocationNodeID());
            nodeCollection.add(n);
            nodeToResSpace.put(n, rs);
        }

        resSpaces.addAll(dbResSpaces);
        allResSpaces.addAll(dbResSpaces);
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

    /**
     * Set up the weekly schedule table.
     */
    private void setUpWeeklyTable() {
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
                // Set the text for the first column to time
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        return new ReadOnlyStringWrapper(p.getValue().getTime());
                    }
                });
            }
            else {
                col.setPrefWidth(185);
                // Set the text for every other column to the availability for that day
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        return new ReadOnlyStringWrapper(p.getValue().getDayAvailability(finInt));

                    }
                });
                // Set the coloring to green for available and red for booked
                col.setCellFactory(column -> {
                    return new TableCell<ScheduleWrapper, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                if (item.equals("-")) {    // Available = green
                                    setStyle("-fx-background-color: #98FB98");
                                } else {    // Not available = red
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
    }

    /**
     * Set up the all rooms daily schedule table.
     */
    private void setUpAllRoomsTable() {
        // Daily schedule all rooms table
        TableColumn<ScheduleWrapper, String> daytimeCol = new TableColumn<>("Time");
        TableColumn<ScheduleWrapper, String> audit = new TableColumn<>("Auditorium");
        TableColumn<ScheduleWrapper, String> cla1 = new TableColumn<>("Classroom 1");
        TableColumn<ScheduleWrapper, String> cla2 = new TableColumn<>("Classroom 2");
        TableColumn<ScheduleWrapper, String> cla3 = new TableColumn<>("Classroom 3");
        TableColumn<ScheduleWrapper, String> comp1 = new TableColumn<>("Computer Room 1");
        TableColumn<ScheduleWrapper, String> comp2 = new TableColumn<>("Computer Room 2");
        TableColumn<ScheduleWrapper, String> comp3 = new TableColumn<>("Computer Room 3");
        TableColumn<ScheduleWrapper, String> comp4 = new TableColumn<>("Computer Room 4");
        TableColumn<ScheduleWrapper, String> comp5 = new TableColumn<>("Computer Room 5");
        TableColumn<ScheduleWrapper, String> comp6 = new TableColumn<>("Computer Room 6");

        // Set up table
        dailyScheduleAllRooms.getColumns().addAll(daytimeCol, audit, cla1, cla2, cla3, comp1, comp2, comp3, comp4, comp5, comp6);
        dailyScheduleAllRooms.setEditable(false);   // Schedule is not editable
        dailyScheduleAllRooms.setStyle("-fx-table-cell-border-color: black;");
        dailyScheduleAllRooms.setStyle("-fx-table-column-rule-color: black;");

        // Set up each column's value and color scheme
        for (int i = 0; i < dailyScheduleAllRooms.getColumns().size(); i++) {
            final int finInt = i;
            TableColumn<ScheduleWrapper, String> col = (TableColumn<ScheduleWrapper, String>) dailyScheduleAllRooms.getColumns().get(i);
            if (i == 0) {
                col.setPrefWidth(70);
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyStringWrapper(p.getValue().getTime());
                    }
                });
            }
            else {
                col.setPrefWidth(140);
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduleWrapper, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduleWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyStringWrapper(p.getValue().getRoomAvailability(finInt));

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
    }

    /**
     * Randomly assign available or unavailable to workstations.
     */
    private void randomWorkstations() {
        workStations.addAll(Arrays.asList(ws1, ws2, ws3, ws4, ws5, ws6, ws7, ws8, ws9,
                ws10, ws11, ws12, ws13, ws14, ws15, ws16, ws17, ws18, ws19,
                ws20, ws21, ws22, ws23, ws24, ws25, ws26, ws27, ws28, ws29,
                ws30, ws31, ws32, ws33, ws34, ws35, ws36, ws37, ws38, ws39,
                ws40, ws41, ws42, ws43, ws44, ws45, ws46, ws47, ws48, ws49,
                ws50, ws51, ws52, ws53, ws54, ws55, ws56, ws57, ws58, ws59,
                ws60, ws61, ws62, ws63, ws64, ws65, ws66, ws67, ws68, ws69,
                ws70, ws71, ws72, ws73, ws74, ws75, ws76, ws77, ws78, ws79,
                ws80, ws81, ws82, ws83, ws84, ws85, ws86, ws87, ws88, ws89,
                ws90, ws91, ws92, ws93, ws94, ws95, ws96, ws97, ws98, ws99,
                ws100, ws101, ws102, ws103, ws104, ws105, ws106, ws107, ws108, ws109,
                ws110, ws111, ws112, ws113, ws114, ws115, ws116, ws117, ws118, ws119, ws120));
        Random rand = new Random();
        for (int i = 0; i < workStations.size(); i++) {   // For each workstation,
            int n = rand.nextInt(2);
            SVGPath ws = workStations.get(i);
            if (n <1) {
                ws.setFill(AVAILABLE_COLOR);    // Set it as available
            }
            else {
                ws.setFill(UNAVAILABLE_COLOR);    // Or not
            }
        }
    }

    /**
     * Listener to update listview of rooms and info label
     *
     * @param value whether it is focused or not
     */
    private void focusState(boolean value) {
        // If this was selected and loses selection, show the room schedule based on the current filters
        if (!value && validTimes(false)) {
            if (availRoomsBtn.getText().contains("ear")) {
                availRooms();
            } else if (bookedRoomsBtn.getText().contains("ear")) {
                bookedRooms();
            }
            showRoomSchedule(false);
            repopulateMap();
        }
    }

    /**
     * If not full screen, set the calendar so that the top edge can be seen.
     */
    @FXML
    private void setCalInset() {
        if (!((Stage)makeReservationBtn.getScene().getWindow()).isFullScreen()) {
            calendarView.setPadding(new Insets(30,0,5,0));
        }
        else {
            calendarView.setPadding(new Insets(0,0,5,0));
        }
    }

    /**
     * Listener to update label when listview selection changed
     *
     * @param value
     */
    private void listFocus(boolean value) {
        if (!value) {
            showRoomSchedule(false);
        }
    }

    /**
     * switches window to home screen
     * @throws Exception if FXML fails to load
     */
    public void showHome() throws Exception {
        randomizeSpaces(false);    // Stop thread
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
        saveSettings();
    }

    /**
     * Fill the table of all reservations.
     */
    private void fillResTable() {
        ArrayList<Reservation> allRes = myDBS.getAllReservations();
        ObservableList<Reservation> observRes = FXCollections.observableArrayList();

        eventCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        locationCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> p) {
                return new ReadOnlyStringWrapper(myDBS.getReservableSpace(p.getValue().getLocationID()).getSpaceName());
            }
        });
        reserverCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> p) {
                Employee emp = myDBS.getEmployee(p.getValue().getEmployeeId());
                return new ReadOnlyStringWrapper(emp.getFirstName() + " " + emp.getLastName());
            }
        });
        eventStartCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> p) {
                // Get the start time
                Reservation res = p.getValue();
                String startTimeStr = res.getStartTime().toInstant().toString();
                int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                String startMinutes = Integer.toString((int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60);
                if (startMinutes.length() < 2) {
                    startMinutes = "0" + startMinutes;
                }
                String startDate = startTimeStr.substring(5,7) + "/" + startTimeStr.substring(8,10) + "/" + startTimeStr.substring(0,4);
                if (startHour >= 20) {
                    startDate = startDate.substring(0,3) + (Integer.parseInt(startDate.substring(3,5)) - 1) + startDate.substring(5);
                }
                startTimeStr = startHour + ":" + startMinutes + " on " + startDate;
                return new ReadOnlyStringWrapper(startTimeStr);
            }
        });
        eventEndCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> p) {
                // Get the end time
                Reservation res = p.getValue();
                String endTimeStr = res.getEndTime().toInstant().toString();
                int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                String endMinutes = Integer.toString((int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60);
                if (endMinutes.length() < 2) {
                    endMinutes = "0" + endMinutes;
                }
                // Correct dates
                String endDate = endTimeStr.substring(5,7) + "/" + endTimeStr.substring(8,10) + "/" + endTimeStr.substring(0,4);
                if (endHour >= 20) {
                    endDate = endDate.substring(0,3) + (Integer.parseInt(endDate.substring(3,5)) - 1) + endDate.substring(5);
                }
                endTimeStr = endHour + ":"  + endMinutes + " on " + endDate;
                return new ReadOnlyStringWrapper(endTimeStr);
            }
        });

        observRes.addAll(allRes);
        resTable.setItems(observRes);
        resTable.setEditable(false);
    }

    /**
     * On room button click, show the schedule for that room and date.
     */
    @FXML
    public void showRoomSchedule(boolean alreadySelected) {
        // Get the selected location if not selected on map
        if (!alreadySelected) {
            ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();
            currentSelection = curr;
        }
        reservableList.getSelectionModel().select(currentSelection);

        showWeeklySchedule();
        showDailySchedule();
        saveSettings();
    }

    /**
     * Set the display for the weekly schedule table.
     */
    private void showWeeklySchedule() {
        // Clear the previous schedule
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            weeklySchedule.get(i).clear();
        }
        // Get that date and turn it into gregorian calendars to pass to the database
        LocalDate chosenDate = datePicker.getValue();

        // set label of weekly scheduler based on date
        String name = currentSelection.getSpaceName();

        // format date
        int date1 = chosenDate.getDayOfMonth();
        String month = chosenDate.getMonth().toString();
        month = month.toLowerCase() ;
        month = month.substring(0, 1).toUpperCase() + month.substring(1);
        String curTime = String.format(month + " %02dth", date1);

        // Print the info in the label on the list
        schedLbl.setText("Book " + name + "\nfor the Week of\n" + curTime);
        schedLbl.setTextAlignment(TextAlignment.CENTER);
        schedLbl.setWrapText(true);

        // Make a list of time and activity labels for the schedule
        ArrayList<ScheduleWrapper> schedToAdd = new ArrayList<>();

        // For every hour between the time the room closes and the time it opens
        int closeOnHour = closeTime;
        if (closeTimeMinutes > 0) {
            closeOnHour = closeTime + 1;
        }
        for (int i = openTime; i < closeOnHour; i++) {
            // For each time step in that hour, create a time label and activity label
            int j = 0;
            if (i == openTime) {
                j = openTimeMinutes;
            }
            int bound = timeStep;
            if (i == closeTime) {
                bound = closeTimeMinutes;
            }
            for (;j < bound; j++) {
                String minutes = String.format("%d", timeStepMinutes * j);
                if (Integer.parseInt(minutes) < 10) {
                    minutes = "0" + minutes;
                }
                String timeInc = i + ":" + minutes;

                // Add the labels to the lists
                ScheduleWrapper toAdd = new ScheduleWrapper(timeInc);
                schedToAdd.add(toAdd);
                for (int day = 0; day < NUM_DAYS_IN_WEEK; day++) {
                    weeklySchedule.get(day).add(0);    // Default is 0, available
                }
            }
        }

        // Set the date to start on
        LocalDate selectedDate = datePicker.getValue();
        int selectedDayOfWeek = datePicker.getValue().getDayOfWeek().getValue();    // 1 is Monday, 7 is Sunday
        if (selectedDayOfWeek != 7) {
            selectedDate = selectedDate.plus(-selectedDayOfWeek, DAYS);
        }
        LocalDate startDate = selectedDate;
        // Populate each day's availability in the weekly schedule
        for (int dailySchedule = 0; dailySchedule < NUM_DAYS_IN_WEEK; dailySchedule++) {
            LocalDate secondDate = startDate.plus(1, DAYS);
            GregorianCalendar gcalStartDay = GregorianCalendar.from(startDate.atStartOfDay(ZoneId.of("America/New_York")));
            GregorianCalendar gcalEndDay = GregorianCalendar.from(secondDate.atStartOfDay(ZoneId.of("America/New_York")));

            // Get reservations for this day
            ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceId(currentSelection.getSpaceID());

            for (Reservation res : reservations) {
                boolean sameDay = false;
                if ((res.getStartTime().toInstant().isBefore(gcalStartDay.toInstant()) && res.getEndTime().toInstant().isAfter(gcalStartDay.toInstant())) ||
                        (res.getStartTime().toInstant().isAfter(gcalStartDay.toInstant()) && res.getStartTime().toInstant().isBefore(gcalEndDay.toInstant()))) {
                    sameDay = true;
                }
                if (sameDay) {
                    // Get the start time
                    int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                    int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
                    if (res.getStartTime().toInstant().isBefore(gcalStartDay.toInstant())) {
                        startHour = 0;
                        startMinutes = 0;
                    }
                    int startFrac = startMinutes / (int) (timeStepMinutes);

                    // Get the end time
                    int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                    int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
                    if (res.getEndTime().toInstant().isAfter(gcalEndDay.toInstant())) {
                        endHour = 24;
                        endMinutes = 0;
                    }
                    int endFrac = endMinutes / (int) (timeStepMinutes);

                    // For every time between the start and end of the reservation,
                    // Mark it as booked, color it red, and display the event name
                    // or "Booked" depending on its privacy level
                    for (int box = (startHour - openTime) * timeStep - openTimeMinutes + startFrac; box < (endHour - openTime) * timeStep + endFrac; box++) {
                        if (box >= 0 && box < schedToAdd.size()) {
                            ScheduleWrapper time = schedToAdd.get(box);
                            if (res.getPrivacyLevel() == 0) {
                                time.setDayAvailability(dailySchedule, res.getEventName());
                                // if private event
                            } else {
                                time.setDayAvailability(dailySchedule, "Booked");
                            }
                            // what does this do (set to booked?)
                            weeklySchedule.get(dailySchedule).set(box, 1);
                        }
                    }
                }
            }
            startDate = startDate.plus(1, DAYS);

        }

        ObservableList<ScheduleWrapper> wrap = FXCollections.observableArrayList();
        wrap.addAll(schedToAdd);
        scheduleTable.setItems(wrap);
        weeklyScheduleTab.setText("Weekly Schedule for " + currentSelection.getSpaceName());
    }

    /**
     * Set the daily schedule table.
     */
    private void showDailySchedule() {
        // Clear the previous schedule
        for (int i = 0; i < NUM_ROOMS; i++) {
            dailyScheduleAllRoomsInts.get(i).clear();
        }

        // Make a list of time and activity labels for the schedule
        ArrayList<ScheduleWrapper> schedToAdd = new ArrayList<>();

        // For every hour between the time the room closes and the time it opens
        int closeOnHour = closeTime;
        if (closeTimeMinutes > 0) {
            closeOnHour = closeTime + 1;
        }
        for (int i = openTime; i < closeOnHour; i++) {
            // For each time step in that hour, create a time label and activity label
            int j = 0;
            if (i == openTime) {
                j = openTimeMinutes;
            }
            int bound = timeStep;
            if (i == closeTime) {
                bound = closeTimeMinutes;
            }
            for (;j < bound; j++) {
                String minutes = String.format("%d", timeStepMinutes * j);
                if (Integer.parseInt(minutes) < 10) {
                    minutes = "0" + minutes;
                }
                String timeInc = i + ":" + minutes;

                // Add the labels to the lists
                ScheduleWrapper toAdd = new ScheduleWrapper(timeInc);
                schedToAdd.add(toAdd);
                for (int day = 0; day < NUM_ROOMS; day++) {
                    dailyScheduleAllRoomsInts.get(day).add(0);    // Default is 0, available
                }
            }
        }

        // Start date is start of the selected date, end date is the beginning of the next day
        GregorianCalendar gcalStartDay = GregorianCalendar.from(datePicker.getValue().atStartOfDay(ZoneId.of("America/New_York")));
        GregorianCalendar gcalEndDay = GregorianCalendar.from((datePicker.getValue().plus(1, DAYS)).atStartOfDay(ZoneId.of("America/New_York")));

        // Populate each day's availability in the weekly schedule
        for (int roomSchedule = 0; roomSchedule < NUM_ROOMS; roomSchedule++) {

            // Get reservations for this day
            ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceId(allResSpaces.get(roomSchedule).getSpaceID());

            for (Reservation res : reservations) {
                boolean sameDay = false;
                if ((res.getStartTime().toInstant().isBefore(gcalStartDay.toInstant()) && res.getEndTime().toInstant().isAfter(gcalStartDay.toInstant())) ||
                        (res.getStartTime().toInstant().isAfter(gcalStartDay.toInstant()) && res.getStartTime().toInstant().isBefore(gcalEndDay.toInstant()))) {
                    sameDay = true;
                }
                if (sameDay) {
                    // Get the start time
                    int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                    int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
                    if (res.getStartTime().toInstant().isBefore(gcalStartDay.toInstant())) {
                        startHour = 0;
                        startMinutes = 0;
                    }
                    int startFrac = startMinutes / (int) (timeStepMinutes);

                    // Get the end time
                    int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
                    int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
                    if (res.getEndTime().toInstant().isAfter(gcalEndDay.toInstant())) {
                        endHour = 24;
                        endMinutes = 0;
                    }
                    int endFrac = endMinutes / (int) (timeStepMinutes);

                    // For every time between the start and end of the reservation,
                    // Mark it as booked, color it red, and display the event name
                    // or "Booked" depending on its privacy level
                    for (int box = (startHour - openTime) * timeStep - openTimeMinutes + startFrac; box < (endHour - openTime) * timeStep + endFrac; box++) {
                        if (box >= 0 && box < schedToAdd.size()) {
                            ScheduleWrapper time = schedToAdd.get(box);
                            if (res.getPrivacyLevel() == 0) {
                                time.setRoomAvailability(roomSchedule, res.getEventName());
                                // if private event
                            } else {
                                time.setRoomAvailability(roomSchedule, "Booked");
                            }
                            // what does this do (set to booked?)
                            dailyScheduleAllRoomsInts.get(roomSchedule).set(box, 1);
                        }
                    }
                }
            }
        }

        ObservableList<ScheduleWrapper> wrap = FXCollections.observableArrayList();
        // schedToAdd = an array list of ScheduleWrapper
        wrap.addAll(schedToAdd);

        // Print out a nice tab title
        dailyScheduleAllRooms.setItems(wrap);
        String month = datePicker.getValue().getMonth() + "";
        month = month.substring(0,1) + month.substring(1).toLowerCase();
        dailyScheduleTab.setText("Day Schedule All Rooms: " + month + " " + datePicker.getValue().getDayOfMonth() + ", " + datePicker.getValue().getYear());
    }

    /**
     * Called by the Make Reservation button.
     * Checks whether location, date, and time are valid.
     */
    @FXML
    public void makeReservation() throws Exception {
        boolean valid = validTimes(true);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent() ;

        if (valid && allowRecurringRes && recurrenceDatePicker.isVisible()) {    // Check whether recurrences are valid
            ArrayList<Reservation> conflicts = new ArrayList<>();
            // Set the chosen reservation
            ArrayList<Reservation> existingReservations = (ArrayList) myDBS.getReservationsBySpaceId(currentSelection.getSpaceID());
            LocalDate origEndDate = datePicker.getValue();
            if (allowMultidayRes) {
                origEndDate = endDatePicker.getValue();
            }
            LocalDate currentDate = datePicker.getValue();
            long differece = DAYS.between(currentDate, origEndDate);
            while(!currentDate.isAfter(recurrenceDatePicker.getValue())) {    // For each repeat reservation
                LocalDate currentEndDate = currentDate.plus(differece, ChronoUnit.DAYS);
                LocalDateTime startDateTime = LocalDateTime.of(currentDate, startTimePicker.getValue());
                LocalDateTime endDateTime = LocalDateTime.of(currentEndDate, endTimePicker.getValue());
                boolean conflict = false;
                for (Reservation res: existingReservations) { // Check whether it conflicts with existing reservations
                    if (!res.getStartTime().toInstant().isAfter(startDateTime.atZone(ZoneId.of("America/New_York")).toInstant()) && res.getEndTime().toInstant().isAfter(startDateTime.atZone(ZoneId.of("America/New_York")).toInstant()) ||
                            (!res.getStartTime().toInstant().isBefore(startDateTime.atZone(ZoneId.of("America/New_York")).toInstant()) && res.getStartTime().toInstant().isBefore(endDateTime.atZone(ZoneId.of("America/New_York")).toInstant()))) {
                        conflicts.add(res);    // If there is a conflict, save the conflicting reservation
                        conflict = true;
                    }
                }
                if (!conflict) {    // If there isn't a conflict, save this reservation
                    LocalTime startTime = startTimePicker.getValue();
                    LocalTime endTime = endTimePicker.getValue();
                    GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((currentDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
                    GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(currentEndDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));
                    event.getRepeatReservations().add(new Reservation(-1,0,0,"",currentSelection.getSpaceID(),gcalStart,gcalEnd));
                }
                // Go to the next repeat event
                if (recurrenceComboBox.getSelectionModel().getSelectedIndex() == 0) {
                    currentDate = currentDate.plus(1, DAYS);
                }
                else if (recurrenceComboBox.getSelectionModel().getSelectedIndex() == 1) {
                    currentDate = currentDate.plus(1, ChronoUnit.WEEKS);
                }
                else if (recurrenceComboBox.getSelectionModel().getSelectedIndex() == 2) {
                    currentDate = currentDate.plus(1, ChronoUnit.MONTHS);
                }
                else {
                    currentDate = currentDate.plus(1, ChronoUnit.YEARS);
                }
            }

            if (conflicts.size() > 0) {    // If there are conflicts, print them and don't allow the reservation
                valid = false;
                String conflictStr = "Your reservation conflicts on dates: ";
                StringBuffer buf = new StringBuffer();
                for (Reservation conflictRes : conflicts) {
                    String conflictDate = "" + conflictRes.getStartTime().getTime();
                    conflictDate = conflictDate.substring(0, 10) + ", " + conflictDate.substring(24);
                    buf.append(conflictDate);
                    buf.append(", ");
                }
                String s = buf.toString();
                conflictStr += s;
                conflictStr = conflictStr.substring(0, conflictStr.length() - 2);
                inputErrorLbl.setVisible(true);
                inputErrorLbl.setText(conflictStr);
            }
        }

        // If everything is okay, create the reservation
        if (valid) {
            // Get the times and dates and turn them into gregorian calendars
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();

            if (allowRecurringRes && recurrenceDatePicker.isVisible()) {    // If recurring event, tell the event/application state
                GregorianCalendar gcalRec = GregorianCalendar.from(ZonedDateTime.from((recurrenceDatePicker.getValue().atTime(endTimePicker.getValue())).atZone(ZoneId.of("America/New_York"))));
                cals.add(gcalRec);
                event.setActuallyRecurring(true);
                event.setFrequency(recurrenceComboBox.getSelectionModel().getSelectedItem().toString());
            }

            // post event to pass times
            event.setStartAndEndTimes(cals);
            event.setEventName("room");
            event.setRoomId(currentSelection.getSpaceID());
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);

            // switch screen to final stage of scheduler
            randomizeSpaces(false);
            saveSettings();
            Stage stage = (Stage) makeReservationBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(ResourceLoader.confirmScheduler);
            StageManager.changeExistingWindow(stage, root, "Confirm Reservations");
        }
    }

    /**
     * Make gregorian calendars from the currently selected date and time.
     *
     * @return a list of gregorian calendars of the current start time/date and end time/date
     */
    private ArrayList<GregorianCalendar> gCalsFromCurrTimes() {
        LocalDate chosenDate = datePicker.getValue();
        LocalDate endDate = datePicker.getValue();
        if (allowMultidayRes) {
            endDate = endDatePicker.getValue();
        }
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();
        GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(endDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));
        ArrayList<GregorianCalendar> cals = new ArrayList<>();
        cals.add(gcalStart);
        cals.add(gcalEnd);
        return cals;
    }

    /**
     * Check whether the selected times are valid. If not, return false.
     * Valid times must: be within the chosen location's start and end times.
     *                  Have an end time greater than the start time.
     *                  Not conflict with any existing reservation.
     *                  Be in the future.
     *
     * @return true if the selected times are valid, false otherwise
     */
    private boolean validTimes(boolean forRes) {

        // reset label
        inputErrorLbl.setText("");

        if (snapToMinutes) {    // Snap to minutes if that setting is on
            makeMinutesValid();
        }
        // Get the selected times
        int startHour = startTimePicker.getValue().getHour();
        int startMins = startTimePicker.getValue().getMinute();
        int endHour = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute();

        // If the chosen date is in the past, show an error
        if (forRes && datePicker.getValue().atStartOfDay().isBefore(LocalDate.now().atStartOfDay())) {
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(pastDateErrorText);
            return false;
        }

        // If the chosen end date is before the start date, show an error
        if (allowMultidayRes && forRes && endDatePicker.getValue().atStartOfDay().isBefore(datePicker.getValue().atStartOfDay())) {
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(endDateErrorText);
            return false;
        }

        // If the times are outside the location's open times
        // or end is greater than start, the times are invalid
        if ((!allowMultidayRes || (allowMultidayRes && endDatePicker.getValue().equals(datePicker.getValue())))
                && ((endHour < startHour || endHour == startHour && endMins <= startMins)|| (startHour < openTime || startHour == openTime && startMins < openTimeMinutes)
                || (closeTime < endHour || closeTime == endHour && endMins > closeTimeMinutes))) {
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText("Please enter a valid time - note that rooms are only available for booking " + openTimeStr + " to " + closeTimeString);
            return false;
        }

        // If the recurring date is in the past, show an error
        if (allowRecurringRes && recurrenceDatePicker.isVisible()) {
            if (allowMultidayRes && recurrenceDatePicker.getValue().isBefore(endDatePicker.getValue())) {
                inputErrorLbl.setVisible(true);
                inputErrorLbl.setText(recurrenceErrorText);
                return false;
            }
            else if (recurrenceDatePicker.getValue().isBefore(datePicker.getValue())) {
                inputErrorLbl.setVisible(true);
                inputErrorLbl.setText(recurrenceErrorText);
                return false;
            }
        }

        if (forRes) {    // If this is for a reservation, check whether it conflicts with any current reservations
            ArrayList<GregorianCalendar> gcals = gCalsFromCurrTimes();
            ArrayList<ReservableSpace> bookedSpace = (ArrayList) myDBS.getBookedReservableSpacesBetween(gcals.get(0), gcals.get(1));
            if (bookedSpace.contains(currentSelection)) {    // Make sure this space is available
                inputErrorLbl.setVisible(true);
                inputErrorLbl.setText(conflictErrorText);
                return false;
            }
            // Make sure this reservation exceeds the minimum reservation time
            if (boundMinRes && gcals.get(0).toZonedDateTime().getDayOfYear() == gcals.get(1).toZonedDateTime().getDayOfYear()) {
                if((gcals.get(0).toZonedDateTime().getHour() == gcals.get(1).toZonedDateTime().getHour()
                        && (gcals.get(1).toZonedDateTime().getMinute() - gcals.get(0).toZonedDateTime().getMinute() < minRes))
                || (gcals.get(0).toZonedDateTime().getHour() == gcals.get(1).toZonedDateTime().getHour() -1
                        && (60 - gcals.get(0).toZonedDateTime().getMinute() + gcals.get(1).toZonedDateTime().getMinute() < minRes))) {
                        inputErrorLbl.setVisible(true);
                        inputErrorLbl.setText("Reservations must be at least " + timeStepMinutes + " minutes long.");
                        return false;
                }
            }
            // Don't allow multi-day reservations to repeat daily
            if (gcals.get(0).toZonedDateTime().getDayOfYear() != gcals.get(1).toZonedDateTime().getDayOfYear()) {
                if (recurrenceComboBox.isVisible() && recurrenceComboBox.getSelectionModel().getSelectedIndex() == 0) {
                    inputErrorLbl.setVisible(true);
                    inputErrorLbl.setText("Multiday reservations cannot repeat daily.");
                    return false;
                }
            }
            if (gcals.get(0).get(java.util.Calendar.WEEK_OF_YEAR) != gcals.get(1).get(java.util.Calendar.WEEK_OF_YEAR)) {
                System.out.println("weeks... + " + gcals.get(0).get(java.util.Calendar.WEEK_OF_YEAR) + gcals.get(1).get(java.util.Calendar.WEEK_OF_YEAR));
                if (recurrenceComboBox.isVisible() && recurrenceComboBox.getSelectionModel().getSelectedIndex() < 2) {
                    inputErrorLbl.setVisible(true);
                    inputErrorLbl.setText("Multiweek reservations cannot repeat daily or weekly.");
                    return false;
                }
            }
            if (gcals.get(0).toZonedDateTime().getMonth() != gcals.get(1).toZonedDateTime().getMonth()) {
                if (recurrenceComboBox.isVisible() && recurrenceComboBox.getSelectionModel().getSelectedIndex() < 3) {
                    inputErrorLbl.setVisible(true);
                    inputErrorLbl.setText("Multi-month reservations cannot repeat daily, weekly, or monthly.");
                    return false;
                }
            }
            if (gcals.get(0).toZonedDateTime().getYear() != gcals.get(1).toZonedDateTime().getYear()) {
                if (recurrenceComboBox.isVisible()) {
                    inputErrorLbl.setVisible(true);
                    inputErrorLbl.setText("Multi-year reservations cannot repeat.");
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
            // Round them down to the nearest timeStep
            int minutes = ((int) endTimePicker.getValue().getMinute() / (timeStepMinutes)) * (timeStepMinutes);
            endTimePicker.setValue(LocalTime.of(endTimePicker.getValue().getHour(), minutes));
        }
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
     * Get the nodes that are booked for the selected date and time.
     * @return the list of booked nodes for the selected date and time
     */
    private ArrayList<ReservableSpace> getBookedNodes() {
        // Get selected times
        ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();
        // Get reservations between selected times
        return (ArrayList<ReservableSpace>) myDBS.getBookedReservableSpacesBetween(cals.get(0), cals.get(1));
    }

    /**
     * Check whether this node is in this list of reservable spaces
     * @param rs  the list of reservable spaces
     * @param n   the node to check
     * @return  true if the node is in the list, false otherwise
     */
    private boolean isNodeInReservableSpace(ArrayList<ReservableSpace> rs, Node n) {
        for (int i = 0; i < rs.size(); i++) {
            if (rs.get(i).getLocationNodeID().equals(n.getNodeID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Populate the map the first time.
     */
    private void populateMap() {
        // Add all the shapes to a list
        shapeCollection = new ArrayList<SVGPath>();
        shapeCollection.addAll(Arrays.asList(auditorium, classroom4, classroom6, classroom8, classroom1, classroom2, classroom3, classroom5, classroom7, classroom9));
        for (int i = 0; i < nodeCollection.size(); i++) {    // For each shape, associate it with a node
            Node node = nodeCollection.get(i);
            SVGPath svg = shapeCollection.get(i);
            svg.setStroke(Color.BLACK);     // Set the default color and design
            svg.setFill(AVAILABLE_COLOR);
            svg.setStrokeWidth(0);
            svg.setStrokeType(StrokeType.INSIDE);
            svg.setOnMouseClicked(new EventHandler<MouseEvent>() {    // When this node is clicked, select it and show its schedules
                @Override public void handle(MouseEvent e) {
                    currentSelection = nodeToResSpace.get(node);
                    showRoomSchedule(true);
                    repopulateMap();
                }
            });
        }
    }

    /**
     * Recolor the shapes on the map for the given time if they are booked.
     */
    private void repopulateMap() {
        ArrayList<ReservableSpace> bookedRS = getBookedNodes();
        for (int i = 0; i < nodeCollection.size(); i++) {    // For each space, check whether it is available or booked
            Node node = nodeCollection.get(i);
            SVGPath svg = shapeCollection.get(i);
            svg.setStrokeWidth(0);
            if (isNodeInReservableSpace(bookedRS, node)) {    // If it's booked, change the color
                svg.setFill(UNAVAILABLE_COLOR);
            } else {
                svg.setFill(AVAILABLE_COLOR);
            }
            if (nodeToResSpace.get(node).equals(currentSelection)) {    // If this space is selected, change its color
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

    /**
     * Set whether the calednar displays contact info for reservers.
     */
    @FXML
    private void setShowContact() {
        if (showContactInfo) {
            showContactInfo = false;
            showContactCheckBox.setSelected(false);
            showContactCheckBox.setText("Off");
        }
        else {
            showContactInfo = true;
            showContactCheckBox.setSelected(true);
            showContactCheckBox.setText("On");
        }
        showRoomSchedule(true);
    }

    /**
     * Set whether recurring reservations are allowed.
     */
    @FXML
    private void setRecurringRes() {
        if (allowRecurringRes) {
            allowRecurringRes = false;
            recurringCheckBox.setSelected(false);
            recurringCheckBox.setText("Off");
            sidePaneVBox.setSpacing(48);
            int index = sidePaneVBox.getChildren().indexOf(sidePaneRecurrenceCheckBox);
            sidePaneVBox.getChildren().add(index, sidePaneRegion);
            sidePaneVBox.getChildren().remove( sidePaneRecurrenceCheckBox);
            sidePaneVBox.getChildren().remove( recurrenceDatePicker);
            sidePaneVBox.getChildren().remove( recurrenceComboBox);
        }
        else {
            allowRecurringRes = true;
            recurringCheckBox.setSelected(true);
            recurringCheckBox.setText("On");
            sidePaneRecurrenceCheckBox.setSelected(false);
            sidePaneVBox.setSpacing(20);
            int index = sidePaneVBox.getChildren().indexOf(sidePaneRegion);
            sidePaneVBox.getChildren().remove(sidePaneRegion);
            sidePaneVBox.getChildren().add(index, sidePaneRecurrenceCheckBox);
            sidePaneVBox.getChildren().add(index + 1, recurrenceDatePicker);
            recurrenceDatePicker.setVisible(false);
            sidePaneVBox.getChildren().add(index + 2, recurrenceComboBox);
            recurrenceComboBox.setVisible(false);
        }
        showRoomSchedule(true);
    }

    /**
     * Set whether multiday reservations are allowed.
     * If they are, open and close times are unbound.
     */
    @FXML
    private void setMultidayRes() {
        if (allowMultidayRes) {
            allowMultidayRes = false;
            multidayCheckBox.setSelected(false);
            multidayCheckBox.setText("Off");
            sidePaneVBox.getChildren().remove(endDatePicker);
            sidePaneRegion.setPrefHeight(139);
        }
        else {
            allowMultidayRes = true;
            multidayCheckBox.setSelected(true);
            multidayCheckBox.setText("On");
            sidePaneVBox.getChildren().add(3, endDatePicker);
            sidePaneRegion.setPrefHeight(98);

            boundCloseTime = false;
            closeTimeCheckBox.setSelected(false);
            closeTimeCheckBox.setText("Unbound");
            closeTimeTextField.setVisible(false);
            closeTime = 23;
            closeTimeMinutes = 60;
            closeTimeString = "23:59";

            boundOpenTime = false;
            openTimeCheckBox.setSelected(false);
            openTimeCheckBox.setText("Unbound");
            openTimeTextField.setVisible(false);
            openTime = 0;
            openTimeMinutes = 0;
            openTimeStr = "00:00";
        }
        showRoomSchedule(true);
    }

    /**
     * Set whether to snap minutes to the closest timestep.
     */
    @FXML
    private void setSnapTo() {
        if (snapToMinutes) {
            snapToMinutes = false;
            snapToCheckBox.setSelected(false);
            snapToCheckBox.setText("Off");
            timeStep = 60;
            timeStepMinutes = 60 / timeStep;
            snapToTextField.setVisible(false);
        }
        else {
            snapToMinutes = true;
            snapToCheckBox.setSelected(true);
            snapToCheckBox.setText("On");
            timeStep = 2;
            timeStepMinutes = 60 / timeStep;
            snapToTextField.setVisible(true);
            snapToTextField.setText(timeStepMinutes + "");
        }
        showRoomSchedule(true);
    }

    @FXML
    private void changeSnapToMinutes() {
        errorMessage.setVisible(false);
        String min = snapToTextField.getText();
        boolean valid = true;
        if (min.length() != 2) {    // Check whether this is minutes in the form mm from 00 to 60.
            valid = false;
        }
        if (min.length() == 2) {
            String first = min.substring(0,1);
            String second = min.substring(1,2);
            if (!"0123456".contains(first) || !"0123456789".contains(second)) {
                valid = false;
            }
        }
        if (valid) {
            int mins = Integer.parseInt(min);
            if (60 % mins != 0 || mins < 1) {
                valid = false;
            }
            else {
                timeStepMinutes = mins;
                timeStep = 60 / timeStepMinutes;
            }
        }
        if (!valid) {
            errorMessage.setVisible(true);
        }
        snapToTextField.setText("" + timeStepMinutes);
        showRoomSchedule(true);
    }

    /**
     * Set whether there is a minimum reservation time.
     */
    @FXML
    private void setMinRes() {
        if (boundMinRes) {
            boundMinRes = false;
            minResCheckBox.setSelected(false);
            minResCheckBox.setText("Unbound");
            minResTextField.setVisible(false);
            minRes = 1;
        }
        else {
            boundMinRes = true;
            minResCheckBox.setSelected(true);
            minResCheckBox.setText("Bound");
            minResTextField.setVisible(true);
            minResTextField.setText(minRes + "");
        }
        showRoomSchedule(true);
    }

    /**
     * On text field enter, if input is valid, set it as the new
     * minimum reservation time.
     */
    @FXML
    private void changeMinimumReservation() {
        errorMessage.setVisible(false);
        String min = minResTextField.getText();
        boolean valid = true;
        if (min.length() != 2) {    // Check whether this is minutes in the form mm from 00 to 60.
            valid = false;
        }
        if (min.length() == 2) {
            String first = min.substring(0,1);
            String second = min.substring(1,2);
            if (!"0123456".contains(first) || !"0123456789".contains(second)) {
                valid = false;
            }
        }
        if (valid) {
            int mins = Integer.parseInt(min);
            if (60 % mins != 0 || mins < 1) {
                valid = false;
            }
            else {
                minRes = mins;
            }
        }
        if (!valid) {
            errorMessage.setVisible(true);
        }
        minResTextField.setText("" + minRes);
        showRoomSchedule(true);
    }

    /**
     * Set whether close time is bound.
     */
    @FXML
    private void setCloseTime() {
        if (boundCloseTime) {
            boundCloseTime = false;
            closeTimeCheckBox.setSelected(false);
            closeTimeCheckBox.setText("Unbound");
            closeTimeTextField.setVisible(false);
            closeTime = 23;
            closeTimeMinutes = 60;
            closeTimeString = "23:59";
        }
        else {
            boundCloseTime = true;
            closeTimeCheckBox.setSelected(true);
            closeTimeCheckBox.setText("Bound");
            closeTimeTextField.setVisible(true);
            closeTimeTextField.setText(closeTimeString);
            allowMultidayRes = true;
            setMultidayRes();
        }
        showRoomSchedule(true);
    }

    /**
     * On close time text field enter, if inpus is valid,
     * set close time to input.
     */
    @FXML
    private void validCloseTime() {
        // Validate input
        String valid = validFieldTime(closeTimeTextField.getText());
        if (valid.length() > 0) {
            if (Integer.parseInt(valid.substring(0,2)) <= openTime) {
                valid = "";
            }
        }

        if (valid.length() > 0) {
            closeTimeString = valid;
            closeTime = Integer.parseInt(closeTimeString.substring(0,2));
            closeTimeMinutes = Integer.parseInt(closeTimeString.substring(3));
            if (snapToMinutes) {    // Snap to minutes if setting on
                if (Integer.parseInt(valid.substring(3)) % (timeStepMinutes) != 0) {
                    // Then round them down to the nearest timeStep
                    int minutes = ((int) Integer.parseInt(valid.substring(3)) / (timeStepMinutes)) * (timeStepMinutes);
                    String minutesSt = "" + minutes;
                    if (minutesSt.length() < 2) {
                        minutesSt += "0";
                    }
                    closeTimeString = closeTimeString.substring(0,3) + minutesSt;
                }
                closeTimeMinutes = (int) (Integer.parseInt(closeTimeString.substring(3)) / timeStepMinutes);
            }
            showRoomSchedule(true);
        }
        else {
            errorMessage.setVisible(true);
        }
        closeTimeTextField.setText(closeTimeString);
        showRoomSchedule(true);
    }

    /**
     * Set whether open time is bound.
     */
    @FXML
    private void setOpenTime() {
        if (boundOpenTime) {
            boundOpenTime = false;
            openTimeCheckBox.setSelected(false);
            openTimeCheckBox.setText("Unbound");
            openTimeTextField.setVisible(false);
            openTime = 0;
            openTimeMinutes = 0;
            openTimeStr = "00:00";
            showRoomSchedule(true);
        }
        else {
            boundOpenTime = true;
            openTimeCheckBox.setSelected(true);
            openTimeCheckBox.setText("Bound");
            openTimeTextField.setVisible(true);
            openTimeTextField.setText(openTimeStr);
            showRoomSchedule(true);
            allowMultidayRes = true;
            setMultidayRes();
        }
        showRoomSchedule(true);
    }

    /**
     * On open time text field enter,
     * if valid input set open time to input
     */
    @FXML
    private void validOpenTime() {
        // Validate input
        String valid = validFieldTime(openTimeTextField.getText());
        if (valid.length() > 0) {
            if (Integer.parseInt(valid.substring(0,2)) >= closeTime) {
                valid = "";
            }
        }

        if (valid.length() > 0) {
            openTimeStr = valid;
            openTime = Integer.parseInt(openTimeStr.substring(0,2));
            openTimeMinutes = Integer.parseInt(openTimeStr.substring(3));
            if (snapToMinutes) {    // Snap to minutes if setting on
                if (Integer.parseInt(valid.substring(3)) % (timeStepMinutes) != 0) {
                    // Then round them down to the nearest timeStep
                    int minutes = ((int) Integer.parseInt(valid.substring(3)) / (timeStepMinutes)) * (timeStepMinutes);
                    String minutesSt = "" + minutes;
                    if (minutesSt.length() < 2) {
                        minutesSt += "0";
                    }
                    openTimeStr = openTimeStr.substring(0,3) + minutesSt;
                }
                openTimeMinutes = (int) (Integer.parseInt(openTimeStr.substring(3)) / timeStepMinutes);
            }
            showRoomSchedule(true);
        }
        else {
            errorMessage.setVisible(true);
        }
        openTimeTextField.setText(openTimeStr);
        showRoomSchedule(true);
    }

    /**
     * Check whether this input text is a valid time in the format hh:mm.
     * @param time  the input
     * @return true if valid input format, false otherwise
     */
    @FXML
    private String validFieldTime(String time) {
        errorMessage.setVisible(false);
        boolean valid = true;
        if (time.length() != 5) {    // Verify length
            valid = false;
        }
        else {    // Make sure the hours are in 24 hour time and minutes are 00 < mm < 60
            time = time.substring(0, 5);
            String first = time.substring(0, 1);
            String second = time.substring(1, 2);
            String third = time.substring(2, 3);
            String fourth = time.substring(3, 4);
            String fifth = time.substring(4, 5);
            if (!"012".contains(first)) {
                valid = false;
            }
            if (first.equals("2") && !"0123".contains(second)) {
                valid = false;
            } else if (!"0123456789".contains(second)) {
                valid = false;
            }
            if (!":".equals(third)) {
                valid = false;
            }
            if (!"012345".contains(fourth)) {
                valid = false;
            }
            if (!"0123456789".contains(fifth)) {
                valid = false;
            }
        }
        if (valid) {
            return time;
        }
        else {
            return "";
        }
    }

    /**
     * Delete a reservation from the table and database.
     */
    @FXML
    private void deleteReservation() {
        Reservation res = resTable.getSelectionModel().getSelectedItem();
        resTable.getItems().remove(res);
        myDBS.deleteReservation(res);
        populateCalendar();
        showRoomSchedule(true);
    }


    /************************************************************************************************************
     * Nested classes
     ************************************************************************************************************/

    /**
     * Wrapper to display times in tables.
     */
    private static class ScheduleWrapper {
        private String time;
        private String sunAvailability;
        private String monAvailability;
        private String tuesAvailability;
        private String wedAvailability;
        private String thursAvailability;
        private String friAvailability;
        private String satAvailability;
        private String cla1Availability, cla2Availability, cla3Availability, comp1Availability, comp2Availability, comp3Availability, comp4Availability, comp5Availability, comp6Availability, auditoriumAvailability;

        /**
         * Initialize all availabilities to "-", ie available.
         * @param time the time of this scheduleWrapper
         */
        public ScheduleWrapper(String time) {
            this.time = time;
            this.sunAvailability = "-";
            this.monAvailability = "-";
            this.tuesAvailability = "-";
            this.wedAvailability = "-";
            this.thursAvailability = "-";
            this.friAvailability = "-";
            this.satAvailability = "-";
            this.auditoriumAvailability = "-";
            this.cla1Availability = "-";
            this.cla2Availability = "-";
            this.cla3Availability = "-";
            this.comp1Availability = "-";
            this.comp2Availability = "-";
            this.comp3Availability = "-";
            this.comp4Availability = "-";
            this.comp5Availability = "-";
            this.comp6Availability = "-";
        }

        public void setTime(String value) {
            this.time = value;
        }

        public String getTime() {
            return time;
        }

        /**
         * Set the availability for the given day to the given value.
         * @param day  the day to set the availability for
         * @param value  the availability or event name
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

        /**
         * Set the availability for the given room to the given value.
         * @param day  the room to set the availability for
         * @param value  the availability or event name
         */
        public void setRoomAvailability(int day, String value) {
            switch(day) {
                case 0:
                    auditoriumAvailability = value;
                    break;
                case 1:
                    cla1Availability = value;
                    break;
                case 2:
                    cla2Availability = value;
                    break;
                case 3:
                    cla3Availability = value;
                    break;
                case 4:
                    comp1Availability = value;
                    break;
                case 5:
                    comp2Availability = value;
                    break;
                case 6:
                    comp3Availability = value;
                    break;
                case 7:
                    comp4Availability = value;
                    break;
                case 8:
                    comp5Availability = value;
                    break;
                case 9:
                    comp6Availability = value;
                    break;
                default:
                    System.out.println("You passed an invalid room while setting availability");
                    break;
            }
        }

        /**
         * Get the availability for the given day.
         * @param day  the room to get the availability for
         */
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

        /**
         * Get the availability for the given room.
         * @param day  the room to get the availability for
         */
        public String getRoomAvailability(int day) {
            switch(day) {
                case 1:
                    return auditoriumAvailability;
                case 2:
                    return cla1Availability;
                case 3:
                    return cla2Availability;
                case 4:
                    return cla3Availability;
                case 5:
                    return comp1Availability;
                case 6:
                    return comp2Availability;
                case 7:
                    return comp3Availability;
                case 8:
                    return comp4Availability;
                case 9:
                    return comp5Availability;
                case 10:
                    return comp6Availability;
                default:
                    return "You passed an invalid room while setting availability";
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

    /**
     * Randomly color workstations while running.
     */
    private static class WorkStationRunner implements Runnable{
        private volatile boolean exit = false;    // Whether to stop running

        public void run() {    // Note that these numbers are all adjustable: sleep time, rem, bound
            int rem = 0;
            while(!exit){    // Whil running
                final int r = rem;
                try {
                    Thread.sleep(2000);    // Wait a little
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Random rand = new Random();
                        for (int i = 0; i < workStations.size(); i++) {   // For each workstation,
                            if (i % 5 == r) {
                                int n = rand.nextInt(5);
                                SVGPath ws = workStations.get(i);
                                if (n < 1) {
                                    if (ws.getFill().equals(AVAILABLE_COLOR)) {
                                        ws.setFill(UNAVAILABLE_COLOR);    // Set it as whatever it was not available
                                    } else {
                                        ws.setFill(AVAILABLE_COLOR);
                                    }
                                }
                            }
                        }
                    }
                });
                rem++;
                if (rem == 5) {
                    rem = 0;
                }
            }
        }

        public void stop(){
            exit = true;
        }
    }

    /**
     * Provide a customized popover for calendar entries.
     */
    private static class MyEntryPopOverContentProvider implements Callback<DateControl.EntryDetailsPopOverContentParameter, javafx.scene.Node> {
        @Override
        public javafx.scene.Node call(DateControl.EntryDetailsPopOverContentParameter param) {
            Entry entry = param.getEntry();
            return new MyCustomPopOverContentNode(entry, param.getDateControl().getCalendars());
        }
    }

    /**
     * Customized popover with customized details pane.
     */
    private static class MyCustomPopOverContentNode extends PopOverContentPane {
        public MyCustomPopOverContentNode(Entry entry, ObservableList<Calendar> allCalendars) {
            requireNonNull(entry);

            EntryHeaderView header = new EntryHeaderView(entry, allCalendars);
            MyEntryDetailsView details = new MyEntryDetailsView(entry);

            PopOverTitledPane detailsPane = new PopOverTitledPane("Details", details);
            getPanes().addAll(detailsPane);

            setHeader(header);
            setExpandedPane(detailsPane);
        }
    }

    /**
     * Customized details pane for popover.
     * Provides event name, location, start and end times, and contact information
     * for public events.
     */
    private static class MyEntryDetailsView extends EntryPopOverPane {
        private Entry entry;

        public MyEntryDetailsView(Entry entry) {
            this.entry = entry;

            // Get the reservation associated with this entry
            Reservation res = myDBS.getReservation(Integer.parseInt(entry.getId()));

            // Get the name and contact info depending on privacy level
            String nameStr = res.getEventName();
            Employee emp = myDBS.getEmployee(res.getEmployeeId());
            String contactStr = emp.getFirstName() + " " + emp.getLastName();
            if (res.getPrivacyLevel() > 0) {
                nameStr = "Private Event";
                contactStr = "Private Organizer";
            }

            // Get the start time
            String startTimeStr = res.getStartTime().toInstant().toString();
            int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            String startMinutes = Integer.toString((int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60);
            if (startMinutes.length() < 2) {
                startMinutes = "0" + startMinutes;
            }

            // Get the end time
            String endTimeStr = res.getEndTime().toInstant().toString();
            int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            String endMinutes = Integer.toString((int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60);
            if (endMinutes.length() < 2) {
                endMinutes = "0" + endMinutes;
            }

            // Correct dates
            String startDate = startTimeStr.substring(5,7) + "/" + startTimeStr.substring(8,10) + "/" + startTimeStr.substring(0,4);
            String endDate = endTimeStr.substring(5,7) + "/" + endTimeStr.substring(8,10) + "/" + endTimeStr.substring(0,4);
            if (endHour >= 20) {
                endDate = endDate.substring(0,3) + (Integer.parseInt(endDate.substring(3,5)) - 1) + endDate.substring(5);
            }
            if (startHour >= 20) {
                startDate = startDate.substring(0,3) + (Integer.parseInt(startDate.substring(3,5)) - 1) + startDate.substring(5);
            }

            // Create labels for all info
            startTimeStr = startHour + ":" + startMinutes + " on " + startDate;
            endTimeStr = endHour + ":"  + endMinutes + " on " + endDate;
            Label leftName = new Label("Event: ");
            Label rightName = new Label(nameStr);
            Label leftStartTime = new Label("Start Time: ");
            Label rightStartTime = new Label(startTimeStr);
            Label leftEndTime = new Label("End Time: ");
            Label rightEndTime = new Label(endTimeStr);
            Label leftContact = new Label("Contact: ");
            Label rightContact = new Label(contactStr);
            Label leftLoc = new Label("Location: ");
            Label rightLoc = new Label(this.entry.getLocation());

            // Add labels to vbox and hbox and put into pane
            VBox left = new VBox();
            left.getChildren().addAll(leftName, leftLoc, leftStartTime, leftEndTime);
            VBox right = new VBox();
            right.getChildren().addAll(rightName, rightLoc, rightStartTime, rightEndTime);
            if (showContactInfo) {
                left.getChildren().add(leftContact);
                right.getChildren().add(rightContact);
            }
            HBox both = new HBox();
            both.getChildren().addAll(left, right);
            VBox total = new VBox();
            total.getChildren().add(both);

            // Create QR code popup if public event
            if (res.getPrivacyLevel() == 0) {
                ImageView QRcode = null;
                try {
                    QRcode = new ImageView(QRService.generateQRCode("https://softeng-teamn.github.io/cal.html?eventName=" + res.getEventName() +
                            "&eventLocation=" + myDBS.getReservableSpace(res.getLocationID()).getSpaceName() + "&eventOrganizer="
                            + myDBS.getEmployee(res.getEmployeeId()).getFirstName() + "&" + myDBS.getEmployee(res.getEmployeeId()).getLastName()
                            + "&startTime=" + res.getStartTime().getTimeInMillis() / 1000 + "&endTime=" + res.getEndTime().getTimeInMillis() / 1000, true));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                total.getChildren().add(QRcode);
            }
            this.getChildren().add(total);
        }
    }
}