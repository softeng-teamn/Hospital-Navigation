package scheduler.controller;

import application_state.ApplicationState;
import application_state.Event;
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
import com.jfoenix.controls.*;
import database.DatabaseService;
import employee.model.Employee;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import map.Node;
import scheduler.model.ReservableSpace;
import scheduler.model.Reservation;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private class WorkStationRunner implements Runnable{
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
            //System.out.println("Thread is stopped....");     can cut
        }

        public void stop(){
            exit = true;
        }
    }


    @FXML
    public JFXButton homeBtn, makeReservationBtn, availRoomsBtn, bookedRoomsBtn;

    @FXML
    public JFXButton submitBtn;

    @FXML
    public JFXTextField eventName, searchBar, employeeID, closeTimeTextField, openTimeTextField, minResTextField;

    @FXML
    private JFXCheckBox closeTimeCheckBox, openTimeCheckBox, minResCheckBox, snapToCheckBox, recurringCheckBox, multidayCheckBox, showContactCheckBox;

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

    @FXML
    public JFXTimePicker startTimePicker, endTimePicker;

    @FXML
    public Label resInfoLbl, inputErrorLbl, schedLbl, errorMessage;

    @FXML
    private SVGPath classroom1, classroom2, classroom3, classroom4, classroom5, classroom6, classroom7, classroom8, classroom9, auditorium;

    private CalendarView calendarView;

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

    @FXML
    private AnchorPane subSceneHolder;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private Tab weeklyScheduleTab, dailyScheduleTab, settingsTab, allResTab;

    private Event event ;    // The current event
    private Thread randStationsThread;    // Random stations thread
    private WorkStationRunner runner;    // Stoppable part

    // Map Stuff
    public static final Color AVAILABLE_COLOR = Color.rgb(0, 160, 100,0.6);
    public static final Color UNAVAILABLE_COLOR = Color.rgb(255, 82, 30, 0.8);
    public static final Color SELECT_AVAILABLE_COLOR = Color.rgb(13, 160, 100,0.9);
    public static final Color SELECT_UNAVAILABLE_COLOR = Color.rgb(255, 82, 30, 0.9);
    ArrayList<Node> nodeCollection = new ArrayList<Node>();
    ArrayList<SVGPath> shapeCollection = new ArrayList<SVGPath>();
    ArrayList<SVGPath> workStations = new ArrayList<>();

    // Admin settings
    private int openTime = 9;   // hour to start schedule display
    private String openTimeStr = "09:00";
    private int openTimeMinutes = 0;
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private String closeTimeString = "22:00";
    private int timeStep = 2;    // Fractions of an hour
    private int timeStepMinutes = 60 / timeStep;    // In Minutes
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
        setUpWeeklyTable();
        setUpAllRoomsTable();
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

        // Only allow admin to change settings. Note: doesn't change past reservations.
        tabPane.getTabs().remove(settingsTab);
        tabPane.getTabs().remove(allResTab);
        if (ApplicationState.getApplicationState().getEmployeeLoggedIn() != null && ApplicationState.getApplicationState().getEmployeeLoggedIn().isAdmin()) {
            tabPane.getTabs().add(allResTab);
            tabPane.getTabs().add(settingsTab);
            displaySettings();     // todo: make settings persist
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
     * Set up calendar tab.
     */
    private void setUpCalendar() throws IOException {

        // Create calendarView and settings
        calendarView = new CalendarView();
        calendarView.setPrefWidth(1485);
        calendarView.setPrefHeight(915);
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
        calendarView.getStylesheets().add("theme.css");

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
        updateTimeThread.start();    // todo: when does this end?

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
        System.out.println("    STARTING TO POPULATE");
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
            if (startMinutes.equals("0")) {
                startMinutes = "00";
            }

            // Get the end time
            String endTimeStr = res.getEndTime().toInstant().toString();
            int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            String endMinutes = Integer.toString((int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60);
            if (endMinutes.equals("0")) {
                endMinutes = "00";
            }

            // Correct dates
            String startDate = startTimeStr.substring(5,7) + "/" + startTimeStr.substring(8,10) + "/" + startTimeStr.substring(0,4);
            String endDate = endTimeStr.substring(5,7) + "/" + endTimeStr.substring(8,10) + "/" + endTimeStr.substring(0,4);
            if (endHour >= 21) {
                endDate = endDate.substring(0,3) + (Integer.parseInt(endDate.substring(3,5)) - 1) + endDate.substring(5);
            }
            if (startHour >= 21) {
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
            this.getChildren().add(both);
        }
    }

    private void displaySettings() {
        showContactCheckBox.setSelected(true);
        showContactCheckBox.setText("On");
        recurringCheckBox.setSelected(false);
        recurringCheckBox.setText("Off");
        multidayCheckBox.setSelected(false);
        multidayCheckBox.setText("Off");
        snapToCheckBox.setSelected(true);
        snapToCheckBox.setText("On");
        minResCheckBox.setSelected(true);
        minResCheckBox.setText("Bound");
        closeTimeCheckBox.setSelected(true);
        closeTimeCheckBox.setText("Bound");
        openTimeCheckBox.setSelected(true);
        openTimeCheckBox.setText("Bound");
        openTimeTextField.setText(openTimeStr);
        openTimeTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        closeTimeTextField.setText(closeTimeString);
        closeTimeTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        minResTextField.setText(timeStepMinutes + "");
        minResTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            timeListener(newValue);
        });
        errorMessage.setVisible(false);
        errorMessage.setText("Please enter a valid time in the format hh:mm");
    }

    /**
     * Listener to update setting times
     *
     * @param value whether it is focused or not
     */
    private void timeListener(boolean value) {
        // If this was selected and loses selection, show the room schedule based on the current filters
        if (!value) {
            closeTimeTextField.setText(closeTimeString);
            openTimeTextField.setText(openTimeStr);
            minResTextField.setText(timeStepMinutes + "");
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
                if (startMinutes.equals("0")) {
                    startMinutes = "00";
                }
                String startDate = startTimeStr.substring(5,7) + "/" + startTimeStr.substring(8,10) + "/" + startTimeStr.substring(0,4);
                if (startHour >= 21) {
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
                if (endMinutes.equals("0")) {
                    endMinutes = "00";
                }
                // Correct dates
                String endDate = endTimeStr.substring(5,7) + "/" + endTimeStr.substring(8,10) + "/" + endTimeStr.substring(0,4);
                if (endHour >= 21) {
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
            int j = 0;
            if (i == openTime) {
                j = openTimeMinutes;
            }
            for (;j < timeStep; j++) {
                String minutes = String.format("%d", timeStepMinutes * j);
                if (Integer.parseInt(minutes) == 0) {
                    minutes = "00";
                }
                String timeInc = time + ":" + minutes + " " + amPm;

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
            selectedDate = selectedDate.plus(-selectedDayOfWeek, ChronoUnit.DAYS);
        }
        LocalDate startDate = selectedDate;
        // Populate each day's availability in the weekly schedule
        for (int dailySchedule = 0; dailySchedule < NUM_DAYS_IN_WEEK; dailySchedule++) {
            LocalDate secondDate = startDate.plus(1, ChronoUnit.DAYS);
            GregorianCalendar gcalStartDay = GregorianCalendar.from(startDate.atStartOfDay(ZoneId.systemDefault()));
            GregorianCalendar gcalEndDay = GregorianCalendar.from(secondDate.atStartOfDay(ZoneId.systemDefault()));

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
            startDate = startDate.plus(1, ChronoUnit.DAYS);

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
            int j = 0;
            if (i == openTime) {
                j = openTimeMinutes;
            }
            for (;j < timeStep; j++) {
                String minutes = String.format("%d", timeStepMinutes * j);
                if (Integer.parseInt(minutes) == 0) {
                    minutes = "00";
                }
                String timeInc = time + ":" + minutes + " " + amPm;

                // Add the labels to the lists
                ScheduleWrapper toAdd = new ScheduleWrapper(timeInc);
                schedToAdd.add(toAdd);
                for (int day = 0; day < NUM_ROOMS; day++) {
                    dailyScheduleAllRoomsInts.get(day).add(0);    // Default is 0, available
                }
            }
        }

        // Start date is start of the selected date, end date is the beginning of the next day
        GregorianCalendar gcalStartDay = GregorianCalendar.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEndDay = GregorianCalendar.from((datePicker.getValue().plus(1, ChronoUnit.DAYS)).atStartOfDay(ZoneId.systemDefault()));

        // Populate each day's availability in the weekly schedule
        for (int roomSchedule = 0; roomSchedule < NUM_ROOMS; roomSchedule++) {

            // Get reservations for this day
            ArrayList<Reservation> reservations = (ArrayList<Reservation>) myDBS.getReservationsBySpaceIdBetween(allResSpaces.get(roomSchedule).getSpaceID(), gcalStartDay, gcalEndDay);

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

        // If everything is okay, create the reservation
        if (valid) {
            // Get the times and dates and turn them into gregorian calendars
            ArrayList<GregorianCalendar> cals = gCalsFromCurrTimes();

            // post event to pass times
            event.setStartAndEndTimes(cals);
            event.setEventName("room");
            event.setRoomId(currentSelection.getSpaceID());
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);

            // switch screen to final stage of scheduler
            randomizeSpaces(false);
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

        if (snapToMinutes) {
            makeMinutesValid();
        }
        // Get the selected times
        int start = startTimePicker.getValue().getHour();
        int mins = startTimePicker.getValue().getMinute() / (timeStepMinutes);
        int index = (start - openTime) * timeStep + mins;
        int end = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute() / (timeStepMinutes);
        int endIndex = (end - openTime) * timeStep + endMins;

        // If the chosen date is in the past, show an error
        if (forRes && datePicker.getValue().atStartOfDay().isBefore(LocalDate.now().atStartOfDay())) {
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(pastDateErrorText);
            return false;
        }

        // If the times are outside the location's open times
        // or end is greater than start, the times are invalid
        if (endIndex <= index || start < openTime || closeTime < end) {
            inputErrorLbl.setVisible(true);
            inputErrorLbl.setText(timeErrorText);
            return false;
        }

        if (forRes) {
            // For each time in the reservation, check whether it is already booked
            ArrayList<Integer> thisDay;
            if (datePicker.getValue().getDayOfWeek().getValue() == 7) {
                thisDay= weeklySchedule.get(0);
            }
            else {
                thisDay = weeklySchedule.get(datePicker.getValue().getDayOfWeek().getValue());
            }
            for (int i = index; i < endIndex; i++) {
                if (thisDay.get(i) == 1) {    // If so, show an error
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

    // Admin functionality, ideally
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
    }

    @FXML
    private void setRecurringRes() {    // todo: other things related to recurring reservations
        if (allowRecurringRes) {
            allowRecurringRes = false;
            recurringCheckBox.setSelected(false);
            recurringCheckBox.setText("Off");
        }
        else {
            allowRecurringRes = true;
            recurringCheckBox.setSelected(true);
            recurringCheckBox.setText("On");
        }
    }

    @FXML
    private void setMultidayRes() {    // todo: other things related to multi-day reservations
        if (allowMultidayRes) {
            allowMultidayRes = false;
            multidayCheckBox.setSelected(false);
            multidayCheckBox.setText("Off");
        }
        else {
            allowMultidayRes = true;
            multidayCheckBox.setSelected(true);
            multidayCheckBox.setText("On");
        }
    }

    @FXML
    private void setSnapTo() {
        if (snapToMinutes) {
            snapToMinutes = false;
            snapToCheckBox.setSelected(false);
            snapToCheckBox.setText("Off");
            timeStep = 60;    // todo: test
            timeStepMinutes = 60 / timeStep;
        }
        else {
            snapToMinutes = true;
            snapToCheckBox.setSelected(true);
            snapToCheckBox.setText("On");
            timeStep = 2;    // todo: test
            timeStepMinutes = 60 / timeStep;
        }
    }

    @FXML
    private void setMinRes() {    // todo: other things related to minimum reservations - esp w/r/t displaying
        if (boundMinRes) {
            boundMinRes = false;
            minResCheckBox.setSelected(false);
            minResCheckBox.setText("Unbound");
            minResTextField.setVisible(false);
            timeStep = 60;
        }
        else {
            boundMinRes = true;
            minResCheckBox.setSelected(true);
            minResCheckBox.setText("Bound");
            minResTextField.setVisible(true);
            minResTextField.setText(timeStepMinutes + "");
        }
    }

    @FXML
    private void setCloseTime() {    // todo: other things related to close time
        if (boundCloseTime) {
            boundCloseTime = false;
            closeTimeCheckBox.setSelected(false);
            closeTimeCheckBox.setText("Unbound");
            closeTimeTextField.setVisible(false);
        }
        else {
            boundCloseTime = true;
            closeTimeCheckBox.setSelected(true);
            closeTimeCheckBox.setText("Bound");
            closeTimeTextField.setVisible(true);
            closeTimeTextField.setText(closeTimeString);
        }
        // todo: check if valid close time: greater than open time
    }

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
        }

        // todo: what if minutes aren't snapTo? - maybe don't have to worry bc will just display really badly but logic still works?
    }

    @FXML
    private void validOpenTime() {    // todo: abstract for closetime
        String valid = validFieldTime(openTimeTextField.getText());

        if (valid.length() > 0) {
            openTimeStr = valid;
            openTime = Integer.parseInt(openTimeStr.substring(0,2));
            if (snapToMinutes) {
                if (Integer.parseInt(valid.substring(3)) % (timeStepMinutes) != 0) {
                    // Then round them down to the nearest timeStep
                    int minutes = ((int) Integer.parseInt(valid.substring(3)) / (timeStepMinutes)) * (timeStepMinutes);
                    String minutesSt = "" + minutes;
                    if (minutesSt.length() < 2) {
                        minutesSt += "0";
                    }
                    openTimeStr = openTimeStr.substring(0,3) + minutesSt;
                }
                openTimeMinutes = (int) (Integer.parseInt(openTimeStr.substring(3)) / timeStepMinutes);    // todo: dammit I broke it - w/r/t reservations?
                System.out.println(openTime);
            }


            showRoomSchedule(true);
        }
        else {
            errorMessage.setVisible(true);
        }
        openTimeTextField.setText(openTimeStr);
    }

    @FXML
    private String validFieldTime(String time) {    // todo: abstract for closetime
        errorMessage.setVisible(false);
        boolean valid = true;
        if (time.length() != 5) {
            valid = false;
        }
        else {
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
            if (valid) {
                if (Integer.parseInt(time.substring(0,2)) >= closeTime) {
                    valid = false;
                }
            }
        }

        if (valid) {
            return time;
        }
        else {
            return "";
        }
    }

}