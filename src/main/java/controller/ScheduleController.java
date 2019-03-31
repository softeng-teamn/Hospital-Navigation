package controller;

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

    private int openTime = 9; // arbitrary
    private int closeTime = 17;
    private double timeStep = 2;    // Fractions of an hour

    @FXML
    public void initialize() {
        ObservableList<Node> nodes = FXCollections.observableArrayList();

        // !!! alter to pull from database
//        ArrayList<Node> DBnodes = dbs.getAllNodes();
//        nodes.addAll(DBnodes);


        nodes.add(new Node("nid", 20, 40, "1", "bdg", "nType", "Room A", "short"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room B", "shortr"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room C", "shortr"));
        nodes.add(new Node("nid", 20, 40, "1", "bdg", "nType", "Room D", "short"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room E", "shortr"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room F", "shortr"));
        nodes.add(new Node("nid", 20, 40, "1", "bdg", "nType", "Room G", "short"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room H", "shortr"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room J", "shortr"));
        nodes.add(new Node("nid", 20, 40, "1", "bdg", "nType", "Room I", "short"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room K", "shortr"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room L", "shortr"));
        nodes.add(new Node("nid", 20, 40, "1", "bdg", "nType", "Room M", "short"));
        nodes.add(new Node("nid2",10, 4, "2", "bldg", "nTypes", "Room N", "shortr"));

        for (Node node : nodes) {
            reservableList.setItems(nodes);

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

    // On room button click, show the schedule for that room
    // !!! change to reflect data fetched from database
    public void showRoomSchedule() {
        Node curr = (Node) reservableList.getSelectionModel().getSelectedItem();

        // !!! ^ not for scroll bar

        //curr.getResBetween(start:Date, end:Date, RoomID:String):Collection<Reservations>
        // !!! get unavail times and mark

        schedule.getChildren().clear();
        checks.getChildren().clear();
        for (int i = openTime; i < closeTime; i++) {
            int time = i % 12;
            if (time == 0) {
                time = 12;
            }

            for (int j = 0; j < timeStep; j++) {
                String minutes = "00";
                if (j > 0) {
                    minutes = String.format("%.0f",(60/timeStep));
                }
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                JFXButton timeInc = new JFXButton(time + ":" + minutes);
                timeInc.setStyle("-fx-background-color: #4BC06E; ");
                hBox.getChildren().add(timeInc);
                // item.setOnAction(...);
                schedule.getChildren().add(hBox);
                JFXCheckBox check = new JFXCheckBox("Reserve Time");
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
