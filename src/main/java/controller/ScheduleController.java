package controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ReservableSpace;
import model.Reservation;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

public class ScheduleController extends Controller {

    @FXML
    private JFXButton homeBtn;

    @FXML
    private VBox roomList, schedule, checks;

    @FXML
    private JFXTextField numRooms;

    @FXML
    private JFXListView reservableList;

    @FXML
    private JFXDatePicker datePicker;

    private int openTime = 9;   // hour to start schedule dislay
    private int closeTime = 10;    // 24-hours hour to end schedule display
    private int timeStep = 2;    // Fractions of an hour

    /**
     * Set up room list.
     */
    @FXML
    public void initialize() {
        ObservableList<ReservableSpace> resSpaces = FXCollections.observableArrayList();

        // Set default date to today's date
        LocalDate date =  LocalDate.now();
        datePicker.setValue(date);

        //  Pull spaces from database
        // Note: when I run this with spaces I make, it works
        // Currently, I get nothing - is getAllResSpaces functional? are we loading data?
        // ?? Do we have data for the reservable spaces??
       // ArrayList<ReservableSpace> dbResSpaces = (ArrayList<ReservableSpace>) dbs.getAllReservableSpaces();
        //resSpaces.addAll(dbResSpaces);

        // fake but here we go: TODO
        ReservableSpace A = new ReservableSpace("ID A", "Conf room A", "CONF", "location", new GregorianCalendar(), new GregorianCalendar());
        ReservableSpace B = new ReservableSpace("ID B", "Conf room B", "CONF", "location", new GregorianCalendar(), new GregorianCalendar());
        ArrayList<ReservableSpace> dbResSpaces = new ArrayList<ReservableSpace>();
        dbResSpaces.add(A);
        dbResSpaces.add(B);
        resSpaces.addAll(dbResSpaces);

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

    /**
     *   On room button click, show the schedule for that room
      */
    public void showRoomSchedule() {
        ReservableSpace curr = (ReservableSpace) reservableList.getSelectionModel().getSelectedItem();

        LocalDate chosenDate = datePicker.getValue();
        LocalDate endDate = chosenDate.plus(1, ChronoUnit.DAYS);
        GregorianCalendar gcalStart = GregorianCalendar.from(chosenDate.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar gcalEnd = GregorianCalendar.from(endDate.atStartOfDay(ZoneId.systemDefault()));

       // ArrayList<Reservation> reservations = (ArrayList<Reservation>) dbs.getReservationBySpaceIdBetween(curr.getSpaceID(), gcalStart, gcalEnd);
        // TODO comment back in

        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        reservations.add(new Reservation(123,0,456, "Party A", "location",
                new GregorianCalendar(2019, 4, 1, 10, 0),
                new GregorianCalendar(2019,4,1,15,0)));

        // clear the previous schedule
        // Note: there is a better way to display this info,
        // This is just what I have working for now.
        schedule.getChildren().clear();
        checks.getChildren().clear();

        ArrayList<HBox> schedToAdd = new ArrayList<HBox>();
        ArrayList<CheckBox> checksToAdd = new ArrayList<CheckBox>();

        for (int i = openTime; i < closeTime; i++) {
            int time = i % 12;
            if (time == 0) {
                time = 12;
            }

            for (int j = 0; j < timeStep; j++) {
                JFXCheckBox check = new JFXCheckBox("Reserve Time");
                String minutes = "00";
                if (j > 0) {
                    minutes = String.format("%d", (60 / timeStep));
                }

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                Label timeInc = new Label(time + ":" + minutes);
                timeInc.setMinWidth(68);
               // timeInc.setTextAlignment(TextAlignment.RIGHT);
                timeInc.setTextFill(Color.web("#FFFEFE"));
                timeInc.setStyle("-fx-background-color: #0f9d58; ");

                hBox.getChildren().add(timeInc);

                schedToAdd.add(hBox);
                checksToAdd.add(check);
            }
        }

        // need to error check this TODO
        // TODO: figure out display sizing
        for (Reservation res : reservations) {
            System.out.println(res.getEventName());

            int startHour = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60 * 60)) % 24 - 4;
            int startMinutes = (int) (res.getStartTime().getTimeInMillis() / (1000 * 60)) % 60;
            int startFrac = startMinutes/(int)(60/timeStep);
            System.out.println(startHour + ":" + startMinutes + ", " + startFrac);

            int endHour = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60 * 60)) % 24 - 4;
            int endMinutes = (int) (res.getEndTime().getTimeInMillis() / (1000 * 60)) % 60;
            int endFrac = endMinutes/(int)(60/timeStep);
            System.out.println(endHour + ":" + endMinutes + ", " + endFrac);

            // you need to better understand gregorian calendar

           // for (int box = 0; box < 3; box++){
            for (int box = (startHour - openTime)*timeStep + startFrac; box < (endHour - openTime)*timeStep + endFrac; box++) {
                Label time = (Label) schedToAdd.get(box).getChildren().get(0);
                time.setStyle("-fx-background-color: #9b0f16; ");
                CheckBox check = (CheckBox) checksToAdd.get(box);
                check.setDisable(true);
            }
        }

        schedule.getChildren().addAll(schedToAdd);
        checks.getChildren().addAll(checksToAdd);
    }

    // TODO
    //asks the database to update a room schedule for a particular room
    //returns true if success
    boolean bookRoom(String roomID, String day, String time){
        return false;
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
