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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.ReservableSpace;
import model.Reservation;
import service.ResourceLoader;
import service.StageManager;

public class ScheduleController extends Controller {

    @FXML
    private JFXButton homeBtn, filterRoomBtn, makeReservationBtn, instructionsBtn, errorBtn, exitConfBtn, submitBtn;

    @FXML
    private VBox roomList, schedule, checks;

    @FXML
    private JFXTextField numRooms, eventName, employeeID;

    @FXML
    private JFXListView reservableList;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXTimePicker startTimePicker, endTimePicker;

    @FXML
    private Label instructionsLbl, errorLbl, timeLbl, confErrorLbl;

    @FXML
    private AnchorPane errorDlg, confirmationPane;

    @FXML
    private StackPane stackP;

    @FXML
    private JFXComboBox<String> privacyLvlBox;

    private int openTime = 9;   // hour to start schedule dislay TODO: change these depending on the room
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private int timeStep = 2;    // Fractions of an hour
    private int timeStepMinutes = 60/timeStep;    // In Minutes

    private ReservableSpace currentSelection;
    private ArrayList<Integer> currentSchedule;

    // TODO: get rid of
    private  ArrayList<Reservation> reservations;
    private ArrayList<ReservableSpace> dbResSpaces;
    private final int confPage = 1;
    private final int homePage = 0;

    /**
     * Set up room list.
     */
    @FXML
    public void initialize() {
        // Create the instructions and error message
        instructionsLbl.setVisible(false);
        instructionsLbl.setText("Instructions for Making a Reservation:\n" +
                "1. Select desired date of reservation on the left.\n" +
                "2. Select a location in the middle menu to view its schedule\n" +
                "   on that date. \n" +
                "3. Select the start and end times for your reservation on the left.\n" +
                "4. Select \"Make Reservation\" at bottom left (you must have selected\n" +
                "   a location in order to make a reservation).\n" +
                "5. Confirm your reservation and fill the required information:\n" +
                "   Event Name, etc.");
        errorDlg.setVisible(false);
        confirmationPane.setVisible(false);
        confErrorLbl.setVisible(false);

        // Set event privacy options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Public",
                        "Private"
                );
        privacyLvlBox.getItems().addAll(options);

        currentSchedule = new ArrayList<Integer>();

        ObservableList<ReservableSpace> resSpaces = FXCollections.observableArrayList();
        makeReservationBtn.setDisable(true);

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
        // Note: when I run this with spaces I make, it works
        // Currently, I get nothing - is getAllResSpaces functional? are we loading data?
        // ?? Do we have data for the reservable spaces??
       // ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) dbs.getAllReservableSpaces();
        //resSpaces.addAll(dbResSpaces);

        // fake but here we go: TODO
        ReservableSpace A = new ReservableSpace("ID A", "Conf room A", "CONF", "location A", new GregorianCalendar(), new GregorianCalendar());
        ReservableSpace B = new ReservableSpace("ID B", "Conf room B", "CONF", "location B", new GregorianCalendar(), new GregorianCalendar());
        dbResSpaces = new ArrayList<ReservableSpace>();
        dbResSpaces.add(A);
        dbResSpaces.add(B);
        resSpaces.addAll(dbResSpaces);
        reservations = new ArrayList<Reservation>();
        reservations.add(new Reservation(123,0,456, "Party A", "location",
                new GregorianCalendar(2019, 4, 1, 10, 0),
                new GregorianCalendar(2019,4,1,15,0)));

        // Add the node to the listview

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

    @FXML
    public void showInstructions() {
        instructionsLbl.setVisible(!instructionsLbl.isVisible());
    }

    private void makeTimeValid() {
        if (startTimePicker.getValue().getMinute()%(timeStepMinutes)!= 0) {

            System.out.println("change start time");
            int minutes = ((int) startTimePicker.getValue().getMinute()/(timeStepMinutes))*(timeStepMinutes);
            //when it not matches the pattern (1.0 - 6.0)
            //set the textField empty
            startTimePicker.setValue(LocalTime.of(startTimePicker.getValue().getHour(), minutes));
        }
        if (endTimePicker.getValue().getMinute()%(timeStepMinutes)!= 0) {
            System.out.println("change end time");
            int minutes = ((int) endTimePicker.getValue().getMinute()/(timeStepMinutes))*(timeStepMinutes);
            //when it not matches the pattern (1.0 - 6.0)
            //set the textField empty
            endTimePicker.setValue(LocalTime.of(endTimePicker.getValue().getHour(), minutes));
        }
    }

    /**
     *   On room button click, show the schedule for that room
      */
    @FXML
    public void showRoomSchedule() {
        makeReservationBtn.setDisable(false);
        ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();
        currentSelection = curr;

        LocalDate chosenDate = datePicker.getValue();
        LocalDate endDate = chosenDate.plus(1, ChronoUnit.DAYS);
        GregorianCalendar gcalStart = GregorianCalendar.from(chosenDate.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEnd = GregorianCalendar.from(endDate.atStartOfDay(ZoneId.systemDefault()));

       // ArrayList<Reservation> reservations = (ArrayList<Reservation>) dbs.getReservationBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);
        // TODO comment back in

        // clear the previous schedule
        // Note: there is a better way to display this info,
        // This is just what I have working for now.
        schedule.getChildren().clear();
        checks.getChildren().clear();

        ArrayList<HBox> schedToAdd = new ArrayList<HBox>();
        ArrayList<Label> checksToAdd = new ArrayList<Label>();

        for (int i = openTime; i < closeTime; i++) {
            String amPm = "AM";
            int time = i % 12;
            if (time == 0) {
                time = 12;
            }

            for (int j = 0; j < timeStep; j++) {
                Label check = new Label("Available");
                String minutes = "00";
                if (j > 0) {
                    minutes = String.format("%d", (60 / timeStep));
                }
                if (((int) i / 12) == 1) {
                    amPm = "PM";
                }

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                Label timeInc = new Label(time + ":" + minutes + " " + amPm);
                timeInc.setMinWidth(68);
                timeInc.setTextAlignment(TextAlignment.CENTER);
                timeInc.setTextFill(Color.web("#FFFEFE"));
                timeInc.setStyle("-fx-background-color: #0f9d58; ");

                hBox.getChildren().add(timeInc);

                schedToAdd.add(hBox);
                checksToAdd.add(check);
                currentSchedule.add(0);
            }
        }

        // TODO: figure out display sizing
        for (Reservation res : reservations) {
            System.out.println(res.getEventName());

            int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
            int startFrac = startMinutes/(int)(timeStepMinutes);
            System.out.println(startHour + ":" + startMinutes + ", " + startFrac);

            int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60) - 4) % 24;
            int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
            int endFrac = endMinutes/(int)(timeStepMinutes);
            System.out.println(endHour + ":" + endMinutes + ", " + endFrac);

            // you need to better understand gregorian calendar

           // for (int box = 0; box < 3; box++){
            for (int box = (startHour - openTime)*timeStep + startFrac; box < (endHour - openTime)*timeStep + endFrac; box++) {
                Label time = (Label) schedToAdd.get(box).getChildren().get(0);
                time.setStyle("-fx-background-color: #9b0f16; ");
                Label check = (Label) checksToAdd.get(box);
                if (res.getPrivacyLevel() == 0) {
                    check.setText(res.getEventName());
                }
                else {
                    check.setText("Booked");
                }
                currentSchedule.set(box, 1);
            }
        }

        schedule.getChildren().addAll(schedToAdd);
        checks.getChildren().addAll(checksToAdd);
    }

    private boolean validTimes() {
        int start = startTimePicker.getValue().getHour();
        int mins = startTimePicker.getValue().getMinute()/(timeStepMinutes);
        int index = (start - openTime) * timeStep + mins;
        int end = endTimePicker.getValue().getHour();
        int endMins = endTimePicker.getValue().getMinute()/(timeStepMinutes);
        int endIndex = (end - openTime) * timeStep + endMins;

        if (end <= start || start < openTime || closeTime < end) {
            return false;
        }

        for (int i = index; i < endIndex; i++) {
            if (currentSchedule.get(i) == 1) {
                return false;
            }
        }
        return true;
    }

    @FXML
    public void closeError() {
        errorDlg.setVisible(false);
    }

    public void closeConf() {
        confirmationPane.toFront();
        confirmationPane.setVisible(false);
        stackP.getChildren().get(homePage).setDisable(false);
        confErrorLbl.setVisible(false);
        eventName.setText("");
        employeeID.setText("");
        privacyLvlBox.setValue(null);
    }

    @FXML
    public void makeReservation() {
        makeTimeValid();
        boolean valid = validTimes();

        if (!valid) {
            errorLbl.setText("Please enter valid start and end times " +
                    "for this location.\n" +
                    "Start and end times must not conflict with any " +
                    "currently scheduled reservations.");
            errorDlg.setVisible(true);
        }
        else {
            stackP.getChildren().get(homePage).setDisable(true);
            stackP.getChildren().get(homePage).toBack();
            confirmationPane.setVisible(true);
            showConf();
        }
    }

    private void showConf() {
        LocalTime start = startTimePicker.getValue();
        LocalTime end = endTimePicker.getValue();
        String startAmPm = " AM";
        String endAmPm = " PM";
        if (start.getHour() >= 12) {
            startAmPm = " PM";
        }
        if (end.getHour() >= 12) {
            endAmPm = " PM";
        }

        // TODO fix timestamps
        timeLbl.setText("Reservation Location:      " + currentSelection.getSpaceName()
                  + "\n\nReservation Date:            " + datePicker.getValue()
                  + "\n\nReservation Start Time:   " + start + startAmPm
                  + "\n\nReservation End Time:    " + end + startAmPm);
    }

    @FXML
    public void submit() {
        confErrorLbl.setVisible(false);
        String id = employeeID.getText();
        boolean badId = false;
        for (char letter: id.toCharArray()) {
            if (!Character.isDigit(letter)) {
                badId = true;
            }
        }
        if (eventName.getText().length() < 1 || employeeID.getText().length() < 1 || privacyLvlBox.getValue() == null) {
            confErrorLbl.setText("Error: Please fill out all fields to make a reservation.");
            confErrorLbl.setVisible(true);
        }
        // TODO: get from database
        else if (badId /* || dbs.getEmployee(Integer.parseInt(employeeID.getText())) == null*/) {
            confErrorLbl.setText("Error: Please provide a valid employee ID number.");
            confErrorLbl.setVisible(true);
        }
        else {
            createReservation();
        }
    }

    @FXML
    private void createReservation() {
        LocalDate chosenDate = datePicker.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();
        GregorianCalendar gcalStart = GregorianCalendar.from(ZonedDateTime.from((chosenDate.atTime(startTime)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar gcalEnd = GregorianCalendar.from(ZonedDateTime.from(chosenDate.atTime(endTime).atZone(ZoneId.of("America/New_York"))));
        int privacy = 0;
        if (privacyLvlBox.getValue() == "Private") {
            privacy = 1;
        }

        Reservation newRes = new Reservation(-1, privacy,Integer.parseInt(employeeID.getText()), eventName.getText(),currentSelection.getLocationNodeID(),gcalStart,gcalEnd);
        //TODO: dbs.insertReservation(newRes);
        reservations.add(newRes);
        showRoomSchedule();  // TODO: for this room
        System.out.println("done " + newRes);
        closeConf();
    }

    // TODO
    public void filterRooms() {

    }

    // TODO
    public void clearFilter() {

    }

    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }




    //returns a list of roomIDs which have a max capacity of less than nPeople
    ArrayList<String> getMaxPeople(int nPeople){
        ArrayList<String> a = new ArrayList<>();
        return a;
    }

    //returns a string representing a day asked for by the user
    //in format mmddyyyy
    String getDay(){
        return "";
    }

    //returns the roomID of the room asked for by the user
    String getRoom(){
        return "";
    }
    //returns the roomID of the workstations asked for by the user
    String getWorkStation(){
        return "";
    }

    //asks the database for available times for a particular room on a particular day
    //in format startTime-endTime;startTime-endTime; and so on
    //start and end time are hh:mm
    String getRoomSched(String roomID, String day){
        return "";
    }


}
