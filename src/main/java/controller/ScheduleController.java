package controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Node;
import model.Reservation;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.Date;

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
    private int closeTime = 20;    // 24-hours hour to end schedule display
    private double timeStep = 2;    // Fractions of an hour

    /**
     * Set up room list.
     */
    @FXML
    public void initialize() {
        ObservableList<Node> nodes = FXCollections.observableArrayList();

        //  Pull nodes from database
        // Note: when I run this with nodes I make, it works
        // Currently, I get nothing - is getAllNodes functional?
        ArrayList<Node> DBnodes = dbs.getAllNodes();
        nodes.addAll(DBnodes);

        // Add the node to the listview
        for (Node node : nodes) {
            reservableList.setItems(nodes);

            // Set the cell to display only the long name of the room
            reservableList.setCellFactory(param -> new ListCell<Node>() {
                @Override
                protected void updateItem(Node item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getLongName() == null) {
                        setText(null);
                    } else {
                        setText(item.getLongName());
                    }
                }
            });

        }
        reservableList.setEditable(false);
    }

    /**
     *   On room button click, show the schedule for that room
      */
    public void showRoomSchedule() {
        Node curr = (Node) reservableList.getSelectionModel().getSelectedItem();
        // TODO make it so that the below doesn't happen for scroll bar selection

        // TODO get date from DatePicker
        // Note: Data seems like not a very functional class.
        // Is there a better class we can use, or just use strings?

        // curr.getResBetween(start:Date, end:Date, RoomID:String):Collection<Reservations>
        // TODO get unavail times - based on chosen node, date, and end of that date
        // TODO generate end date

        // clear the previous schedule
        // Note: there is a better way to display this info,
        // This is just what I have working for now.
        schedule.getChildren().clear();
        checks.getChildren().clear();

        // For every hour between open and close, or startDate/endDate
        // Create a button (can change this to label) and checkbox for that time
        for (int i = openTime; i < closeTime; i++) {
            int time = i % 12;
            if (time == 0) {
                time = 12;
            }

            // For every time incremenb in that hour
            for (int j = 0; j < timeStep; j++) {
                JFXCheckBox check = new JFXCheckBox("Reserve Time");
                String minutes = "00";
                if (j > 0) {
                    minutes = String.format("%.0f",(60/timeStep));
                }
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                JFXButton timeInc = new JFXButton(time + ":" + minutes);
                timeInc.setStyle("-fx-background-color: #4BC06E; ");

                // Some kind of check for reservations -> turn the button red
                // And disable checkbox
//                if (... this time is already reserved...) {
//                    timeInc.setStyle("-fx-background-color: #CA3637; ");
//                    check.setDisable(true);
//                }
                hBox.getChildren().add(timeInc);
                schedule.getChildren().add(hBox);
                checks.getChildren().add(check);
            }
        }
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

    //asks the database to update a room schedule for a particular room
    //returns true if success
    boolean bookRoom(String roomID, String day, String time){
        return false;
    }

}
