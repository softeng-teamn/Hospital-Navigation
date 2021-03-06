package edu.wpi.cs3733d19.teamN.home;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import edu.wpi.cs3733d19.teamN.application_state.Event;
import edu.wpi.cs3733d19.teamN.application_state.Observer;
import com.google.zxing.WriterException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.map.MapController;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.service.QRService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.TextingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static edu.wpi.cs3733d19.teamN.application_state.ApplicationState.getApplicationState;

/**
 * Textual directions edu.wpi.cs3733d19.teamN.controller
 */
public class DirectionsController implements Observer {
    private Event event;

    @FXML
    private JFXButton home_btn, unitSwitch_btn, textingButton, viewQRCodeBtn;

    @FXML
    private JFXListView<HBox> directionsView;

    @FXML
    private JFXTextField phoneNumber;

    @FXML
    private VBox qrCodeVbox;

    @FXML
    private ImageView qrView;

    //text message global variable
    private String units = "feet";    // Feet or meters conversion
    private HashMap<String, Integer> floors = new HashMap<String, Integer>();
    private ArrayList<Node> path;
    private ArrayList<Node> startNodes = new ArrayList<>();    // List of nodes for clicking directions


    /**
     * Initializes the directions edu.wpi.cs3733d19.teamN.controller
     */
    @FXML
    void initialize() {
        // Register as an observer
        ApplicationState.getApplicationState().getObservableBus().register("directionsContoller",this);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();

        // Get the current path and display it
        path = event.getPath();
        ArrayList<String> allDirs = (makeDirections(path));
        printDirections(allDirs);

        // Set whether the send-text button is enabled
        if (getApplicationState().getEmployeeLoggedIn() != null) {
            textingButton.setDisable(getApplicationState().getEmployeeLoggedIn().getPhone().equals(""));
            if (!getApplicationState().getEmployeeLoggedIn().getPhone().equals("")) {
                phoneNumber.setText(getApplicationState().getEmployeeLoggedIn().getPhone());
            }
        }
        else {
            textingButton.setDisable(true);
        }

        // Generate the QR code
        Platform.runLater(() -> {
            try {
                generateQRCode(allDirs);
                generateARQR(path);
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }
        });
        qrView.setVisible(false);
    }

    /** Close the drawer.
     * @param e FXML event that calls this method
     */
    @FXML
    void closeThisDrawer(ActionEvent e) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("closeDrawer");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** recieve a notification from the observed object
     * @param o event recieved
     */
    @Override
    public void notify(Object o) {
        event = (Event) o;
        switch (event.getEventName()) {
            case "printText":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Create and display directions
                        path = event.getPath();
                        ArrayList<String> dirs = makeDirections(path);
                        printDirections(dirs);

                        // Generate QR code
                        Platform.runLater(() -> {
                            try {
                                generateQRCode(dirs);
                                generateARQR(path);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }

    }


    /**
     * Create textual instructions for the given path.
     * @param path the list of nodes in the path
     * @return a String of the directions
     */
    public ArrayList<String> makeDirections(ArrayList<Node> path) {
        floors.put("L2", -2);
        floors.put("L1", -1);
        floors.put("G", 0);
        floors.put("1", 1);
        floors.put("2", 2);
        floors.put("3", 3);
        floors.put("4", 4);
        floors.put("FL", 5);

        // Don't do anything if it's not a valid path
        if (path == null || path.size() < 2) {
            return null;
        }

        ArrayList<Integer> directionsToEdgesMap = new ArrayList<>();

        final int NORTH_I = 1122 - 1886;    // Measurements from maps
        final int NORTH_J = 642 - 1501;    // Measurements from maps

        ArrayList<String> directions = new ArrayList<>();    // Collection of instructions
        directions.add("Start at " + path.get(0).getLongName() + ".");    // First instruction
        directionsToEdgesMap.add(1);

        // Make the first instruction cardinal, or up/down if it is a floor connector
        String oldFloor = path.get(0).getFloor();
        String newFloor = path.get(1).getFloor();
        if (!floors.get(oldFloor).equals(floors.get(newFloor))) {
            directions.add(upDownConverter(oldFloor, newFloor, path.get(0).getNodeType()));
            directionsToEdgesMap.add(1);
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("ELEV")) || (path.size() > 2 && path.get(1).getNodeType().equals("ELEV") && path.get(2).getNodeType().equals("ELEV"))) {
            directions.add("I");
            directionsToEdgesMap.add(1);
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("STAI")) || (path.size() > 2 && path.get(1).getNodeType().equals("STAI") && path.get(2).getNodeType().equals("STAI"))) {
            directions.add("J");
            directionsToEdgesMap.add(1);
        }
        else {
            directions.add(convertToCardinal(csDirPrint(path.get(0).getXcoord() + NORTH_I, path.get(0).getYcoord() + NORTH_J, path.get(0), path.get(1))));
            directionsToEdgesMap.add(1);
        }

        // Make startNodes correspond to directions
        startNodes.clear();
        startNodes.add(path.get(0));
        for (int i = 0; i < path.size(); i++) {
            startNodes.add(path.get(i));
        }

        boolean afterFloorChange = false;    // If we've just changed floors, give a cardinal direction
        for (int i = 0; i < path.size() - 2; i++) {    // For each node in the path, make a direction
            String oldFl = (path.get(i+1).getFloor());
            String newFl = (path.get(i+2).getFloor());
            // If just changed floors, give a cardinal direction
            if (afterFloorChange && !path.get(i + 2).getNodeType().equals("ELEV") && !path.get(i + 2).getNodeType().equals("STAI")) {
                afterFloorChange = false;
                directions.add(convertToCardinal(csDirPrint(path.get(i+1).getXcoord() + NORTH_I, path.get(i+1).getYcoord() + NORTH_J, path.get(i+1), path.get(i+2))));
                directionsToEdgesMap.add(1);
            }
            else if(!path.get(i+1).getNodeType().equals("ELEV") && !path.get(i+1).getNodeType().equals("STAI") && (path.get(i+2).getNodeType().equals("ELEV") || path.get(i+2).getNodeType().equals("STAI"))
                    && ((i < path.size() - 3 && (path.get(i+3).getNodeType().equals("ELEV") || path.get(i+3).getNodeType().equals("STAI"))) || i == path.size() -3)) {    // If next node is elevator, say so
                if (path.get(i+2).getNodeType().equals("ELEV")) {
                    directions.add("I");
                    directionsToEdgesMap.add(1);
                } else {
                    directions.add("J");
                    directionsToEdgesMap.add(1);
                }
            }
            else if (!floors.get(oldFl).equals(floors.get(newFl))) {    // Otherwise if we're changing floors, give a floor change direction
                directions.add(upDownConverter(oldFl, newFl, path.get(i+1).getNodeType()));
                directionsToEdgesMap.add(1);
                afterFloorChange = true;
            }
            else {    // Otherwise provide a normal direction
                directions.add(csDirPrint(path.get(i), path.get(i+1), path.get(i+2)));
                directionsToEdgesMap.add(1);
                afterFloorChange = false;
            }
        }

        // Simplify directions that continue approximately straight from each other
        for (int i = 1; i < directions.size(); i++) {
            String currDir = directions.get(i);
            String currOne = currDir.substring(0,1);
            String prevDir = directions.get(i-1);
            String prevOne = prevDir.substring(0,1);
            String newDir = "";
            boolean changed = false;
            if (currOne.equals("A") && !"IJNOPQ".contains(prevOne)) {    // If the current direction contains straight, get the distance substring
                int prevDist = Integer.parseInt(prevDir.substring(1,6));
                int currDist = Integer.parseInt(currDir.substring(1,6));
                double totalDist = prevDist + currDist;    // Combine the distance of this direction with the previous one
                newDir = prevOne + padWithZeros(totalDist) + currDir.substring(6);
                changed = true;
            }
            else if ("NOPQ".contains(currOne) && currOne.equals(prevOne)) {    // If changing floors, save the start and end floors
                newDir = currOne + prevDir.substring(1, 2) + currDir.substring(2, 3);
                changed = true;
            }
            if (changed) {    // Remove this direction from the direction list and list of nodes
                directions.remove(i);
                directions.remove(i-1);
                directions.add(i-1, newDir);

                directionsToEdgesMap.set(i-1, directionsToEdgesMap.get(i-1) + directionsToEdgesMap.get(i));
                directionsToEdgesMap.remove(i);

                startNodes.remove(i-1);
                i--;
            }
        }
        // Add the final direction
        directions.add("You have arrived at " + path.get(path.size() - 1).getLongName() + ".");
        directionsToEdgesMap.add(1);

        directions.add(";");

        int segmentNumber = 0;
        String lastFloor = path.get(0).getFloor();
        for(Node n : path) {
            if(!n.getFloor().equals(lastFloor)) {
                lastFloor = n.getFloor();
                segmentNumber++;
            }
            directions.add(String.format("%d;%d;%s-%d", n.getXcoord(), n.getYcoord(), n.getFloor(), segmentNumber));
        }

        directions.add(";");

        for (Integer i : directionsToEdgesMap) {
            directions.add(i.toString());
        }

        ApplicationState.getApplicationState().setEndNode(path.get(path.size() - 1));
        return directions;
    }

    /**
     * Convert two floors into an up/down elevator/stairs instruction
     * @param f1 the first floor
     * @param f2 the second floor
     * @param type the nodeType of the first node
     * @return the instruction for up/down stairs/elevator
     */
    public String upDownConverter(String f1, String f2, String type) {
        HashMap<String, String> floorsQR = new HashMap<>();
        floorsQR.put("L2", "A");
        floorsQR.put("L1", "B");
        floorsQR.put("G", "C");
        floorsQR.put("1", "D");
        floorsQR.put("2", "E");
        floorsQR.put("3", "F");
        floorsQR.put("4", "G");
        floorsQR.put("5", "H");

        String ret = "";

        if (floors.get(f1) < floors.get(f2)) {
            if (type.equals("ELEV")) {
                ret = ("N" + floorsQR.get(f1) + floorsQR.get(f2));
            } else {
                ret = ("P" + floorsQR.get(f1) + floorsQR.get(f2));
            }
        }
        else {
            if (type.equals("ELEV")) {
                ret = ("O" + floorsQR.get(f1) + floorsQR.get(f2));
            } else {
                ret = ("Q" + floorsQR.get(f1) + floorsQR.get(f2));
            }
        }
        return ret;
    }

    /**
     * Populate the listview and turn the list of directions into one printable string.
     * @param rawDirs the list of directions as strings
     * @return a String that is the sum of all the directions
     */
    public String printDirections(ArrayList<String> rawDirs) {
        // Remove the coordinates at the end
        ArrayList<String> ds = new ArrayList<>();
        for (String s : rawDirs) {
            if (s.equals(";")) break;
            ds.add(s);
        }

        // Set up floor conversions
        HashMap<String, String> backToFloors = new HashMap<>();
        backToFloors.put("A", "L2");
        backToFloors.put("B", "L1");
        backToFloors.put("C", "G");
        backToFloors.put("D", "1");
        backToFloors.put("E", "2");
        backToFloors.put("F", "3");
        backToFloors.put("G", "4");

        // Set up arrayLists
        ArrayList<String> directions = new ArrayList<>();
        directions.add(ds.get(0));
        ObservableList<HBox> dirs = FXCollections.observableArrayList();
        ArrayList<HBox> labels = new ArrayList<>();

        // Starting floor - create hierarchy
        String floor = startNodes.get(0).getFloor();
        startNodes.add(0,startNodes.get(0));
        HBox firstFloorBox = new HBox();
        Label first = new Label("Floor " + floor + ":");
        first.setWrapText(true);
        first.setTextFill(Color.WHITE);
        first.setStyle("-fx-font-weight: bold");
        firstFloorBox.getChildren().add(first);
        labels.add(firstFloorBox);

        // Add the first direction
        HBox firstBox = new HBox();
        first = new Label(ds.get(0));
        first.setWrapText(true);
        first.setTextFill(Color.WHITE);
        firstBox.getChildren().add(first);
        labels.add(firstBox);

        for (int i = 1; i < ds.size() - 1; i++) {    // Add each subsequent direction
            String direct = ds.get(i);
            Image image = null;
            switch(direct.substring(0,1)) {
                case "A":
                    direct = "Walk straight for " + Integer.parseInt(direct.substring(1,6)) + " " + units + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.continue_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "B":
                    direct = "Turn left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_left_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "C":
                    direct = "Turn slightly left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_slight_left_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "D":
                    direct = "Turn sharply left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_sharp_left_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "E":
                    direct = "Turn right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_right_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "F":
                    direct = "Turn slightly right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_slight_right_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "G":
                    direct = "Turn sharply right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.turn_sharp_right_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "H":
                    direct = "Turn around and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.uturn_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "I":
                    direct = "Walk to the elevator.\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "J":
                    direct = "Walk to the stairs.\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "N":
                    direct = "Take the elevator up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    try {
                        image = new Image(ResourceLoader.elevator_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "O":
                    direct = "Take the elevator down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    try {
                        image = new Image(ResourceLoader.elevator_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "P":
                    direct = "Take the stairs up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    try {
                        image = new Image(ResourceLoader.stairs_up_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Q":
                    direct = "Take the stairs down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    try {
                        image = new Image(ResourceLoader.stairs_down_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "S":
                    direct = "Walk north for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "T":
                    direct = "Walk north west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "U":
                    direct = "Walk west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "V":
                    direct = "Walk south west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "W":
                    direct = "Walk south for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "X":
                    direct = "Walk south east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Y":
                    direct = "Walk east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Z":
                    direct = "Walk north east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    try {
                        image = new Image(ResourceLoader.walking_icon.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    direct = "Houston we have a problem";
                    break;
            }
            // Add this direction to the list
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER_LEFT);
            if(image != null) {
                ImageView iv = new ImageView(image);
                iv.setFitHeight(30);
                iv.setFitWidth(30);
                box.getChildren().add(iv);
            }
            Label l = new Label(direct);
            l.setWrapText(true);
            l.setTextFill(Color.WHITE);
            box.getChildren().add(l);
            labels.add(box);

            // If this is a new floor, create a floor header and add a corresponding node
            if ("NOPQ".contains(ds.get(i).substring(0,1))) {
                startNodes.add(i+2, startNodes.get(i+2));
                String newFloor = backToFloors.get(ds.get(i).substring(2, 3));
                HBox newFloorBox = new HBox();
                Label newFloorLabel = new Label("Floor " + newFloor + ":");
                newFloorLabel.setWrapText(true);
                newFloorLabel.setTextFill(Color.WHITE);
                newFloorLabel.setStyle("-fx-font-weight: bold");
                newFloorBox.getChildren().add(newFloorLabel);
                labels.add(newFloorBox);
            }

            directions.add(direct);
        }
        directions.add(ds.get(ds.size() -1));

        // Add the last direction
        HBox lastBox = new HBox();
        Label last = new Label(ds.get(ds.size() - 1));
        last.setWrapText(true);
        last.setTextFill(Color.WHITE);
        lastBox.getChildren().add(last);
        labels.add(lastBox);

        dirs.addAll(labels);
        directionsView.setItems(dirs);

        // Return the directions
        directions.add(ds.get(ds.size() -1));
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < directions.size(); ++i) {
            buf.append(directions.get(i));
        }
        String total = buf.toString();
        System.out.println(total);    // todo cut
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("showDestination");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        return total;
    }

    /**
     * Convert this direction to a cardinal direction
     * ONLY WHEN MADE WITH SOUTH AS THE FIRST VECTOR
     * @param cardinal the direction with the first vector going south
     * @return the direction as a cardinal direction
     */
    String convertToCardinal(String cardinal) {
        if (cardinal.contains("C")) {
            cardinal = "X" + cardinal.substring(1);
        }
        else if (cardinal.contains("F")) {
            cardinal = "V" + cardinal.substring(1);
        }
        else if (cardinal.contains("D")) {
            cardinal = "Z" + cardinal.substring(1);
        }
        else if (cardinal.contains("G")) {
            cardinal = "T" + cardinal.substring(1);
        }
        else if (cardinal.contains("B")) {
            cardinal = "Y" + cardinal.substring(1);
        }
        else if (cardinal.contains("E")) {
            cardinal = "U" + cardinal.substring(1);
        }
        else if (cardinal.contains("A")) {
            cardinal = "W" + cardinal.substring(1);
        }
        else if (cardinal.contains("I") || cardinal.contains("J")) {
            // Leave as is
        }
        else {
            cardinal = "S" + cardinal.substring(1);
        }
        return cardinal;
    }

    /**
     * Call the directions function given and x,y location and the next two nodes
     * @param x   x coordinate for prev node
     * @param y   y coordinate for prev node
     * @param curr  current node
     * @param next  next node
     * @return  direction between these 3 locations
     */
    private String csDirPrint(int x, int y, Node curr, Node next) {
        Node n1 = new Node("ID", x, y, "HALL");
        return csDirPrint(n1, curr, next);
    }

    /**
     * Compute the direction turned and distance between the middle and last point for the given 3 points.
     * @param prev the previous node
     * @param curr the current node
     * @param next the next node
     * @return the direction for someone walking from points 1 to 3 with the turn direction and distance
     *      *          between the middle and last point
     */
    private String csDirPrint(Node prev, Node curr, Node next) {
        double prevXd, prevYd, currXd, currYd, nextXd, nextYd;
        prevXd = prev.getXcoord();
        prevYd = prev.getYcoord();
        currXd = curr.getXcoord();
        currYd = curr.getYcoord();
        nextXd = next.getXcoord();
        nextYd = next.getYcoord();

        final double THRESHOLD = .0001;   // Double comparison standard

        // The slopes for the two vectors and y-intercept for the second vector as a line
        double slope1, slope2, intercept;
        slope1 = (currYd - prevYd) / (currXd - prevXd);
        slope2 = (nextYd - currYd) / (nextXd - currXd);
        intercept = nextYd - slope2 * nextXd;

        // The vector components for both vectors and their lengths
        double oldI, oldJ, newI, newJ, lengthOld, lengthNew;
        oldI = currXd - prevXd;
        oldJ = currYd - prevYd;
        newI = nextXd - currXd;
        newJ = nextYd - currYd;
        lengthOld = Math.sqrt(oldI*oldI + oldJ*oldJ);
        lengthNew = Math.sqrt(newI*newI + newJ * newJ);

        // Distance in feet based on measurements from the map: 260 pixels per 85 feet
        double distance;
        if (units.equals("feet")) {
            distance = lengthNew /260 * 85;    // Pixels to feet
        }
        else {
            distance = lengthNew / 260 * 25.908;    // Pixels to meters
        }

        // Compute the angle, theta, between the old and new vector
        double uDotV = oldI * newI + oldJ * newJ;
        double theta, alpha, plus, minus;
        theta = Math.acos(uDotV/(lengthNew*lengthOld));
        alpha = Math.atan(slope1);    // Compute the angle, alpha, between the old vector and horizontal
        plus = theta + alpha;    // The sum of the two angles
        minus = alpha - theta;    // The difference between the two angles

        double computedY1 = currYd + Math.tan(plus);    // Guess which side of the old vector we turned to

        double expectedVal = (currXd + 1) * slope2 + intercept;    // The actual side of the old vector we turned to

        if (Math.abs(newI) < THRESHOLD) {    // If the next vector is vertical, make sure it does what it's supposed to
            if ((nextYd > currYd && prevXd < currXd) || (nextYd < currYd && prevXd > currXd)) {
                expectedVal = 1;
                computedY1 = 1;
            }
            else {
                expectedVal = 1;
                computedY1 = 0;
            }
        }
        // If the next vector is horizontal and this one was vertical, make it give the correct direction
        if (Math.abs(oldI) < THRESHOLD && Math.abs(newJ) < THRESHOLD) {
            if ((currYd > prevYd && nextXd > currXd) || (currYd < prevYd && currXd > nextXd)) {
                expectedVal = 1;
                computedY1 = 0;
            }
            else {
                expectedVal = 1;
                computedY1 = 1;
            }
        }

        String turn = "";

        if (Math.abs(plus - minus) < Math.PI/8) {    // Say straight within a small angle
            turn = "A";
        }
        else if (Math.abs(theta - Math.PI) < THRESHOLD) {    // Turn around if theta is to behind you
            turn = "H";
        }
        else if (Math.abs(expectedVal - computedY1) < THRESHOLD) {    // Otherwise turn the correct direction
            if (theta <= Math.PI/4) {
                turn = "F";
            }
            else if (theta >= Math.PI*3/4) {
                turn = "G";
            }
            else {
                turn = "E";
            }
        }
        else {
            if (theta <= Math.PI/4) {
                turn = "C";
            }
            else if (theta >= Math.PI*3/4) {
                turn = "D";
            }
            else {
                turn = "B";
            }
        }

        String landmark = "";
        ArrayList<Node> closeNodes = MapController.getNodesConnectedTo(next);
        for (Node n: closeNodes) {
            if (n.equals(curr)) {/* do nothing*/  }
            else if (!landmark.contains("towards") && n.getNodeType().equals("HALL")) {
                landmark = " down the hall";
            }
           else if (!n.getNodeType().equals("HALL") && !n.getNodeType().equals("ELEV") && !n.getNodeType().equals("STAI")) {
                landmark = " towards " + n.getLongName();
            }
        }
        if (!next.getNodeType().equals("HALL") && !next.getNodeType().equals("ELEV") && !next.getNodeType().equals("STAI")) {
            landmark = " towards " + next.getLongName();
        }

        // Create and return the direction
        String distPadded = padWithZeros(distance);    // Pad direction with zeros so always same length
        String direction = turn + distPadded + landmark;
        return direction;
    }

    /**
     * Pad a distance with zeros so all distances are the same length
     * @param distance the distance to pad with zeros
     * @return the padded distance as a string
     */
    private String padWithZeros(double distance) {
        if (units.equals("feet")) {
            int fives = (int) distance / 5;
            distance = fives * 5;
            if (distance == 0) {
                distance = 1;
            }
        }
        else {
            int twos = (int) distance / 2;
            distance = twos * 2;
            if (distance == 0) {
                distance = 1;
            }
        }
        String orig = String.format("%.0f", distance);
        for (int i = 0; i < 6; i++) {    // Assume max distance is less than 99999 feet
            if (orig.length() < i) {    // Pad with zeroes while string length is less than 6
                orig = "0" + orig;
            }
        }
        return orig;
    }

    /**
     * Get the current units
     * @return current units: feet or meters
     */
    public String getUnits() {
        return units;
    }

    /**
     * Set the current units as feet or meters
     */
    public void setUnits() {
        if (unitSwitch_btn.getText().equals("Meters")) {
            units = "meters";
            unitSwitch_btn.setText("Feet");
        }
        else {
            units = "feet";
            unitSwitch_btn.setText("Meters");
        }
        printDirections(makeDirections(path));
        try {
            generateARQR(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void setPath(ArrayList<Node> thePath) {
        this.path = thePath;
    }

    /**
     * When a directio is clicked, scroll to that direction on the map.
     */
    public void directionClicked() {
        Node directionStart = startNodes.get(directionsView.getSelectionModel().getSelectedIndex());
        Event e = ApplicationState.getApplicationState().getObservableBus().getEvent();
        e.setEventName("scroll-to-direction");
        e.setDirectionsNode(directionStart);
        ApplicationState.getApplicationState().getObservableBus().updateEvent(e);
    }

    /**
     * Check whether the user has input a valid phone number.
     */
    public void validPhone(){
        if (getApplicationState().getEmployeeLoggedIn() != null) {
            textingButton.setDisable(getApplicationState().getEmployeeLoggedIn().getPhone().equals(""));
        }
        for (char c: phoneNumber.getText().toCharArray()) {
            if (!Character.isDigit(c)) {
                textingButton.setDisable(true);
            }
        }
        if (phoneNumber.getText().length() != 10) {
            textingButton.setDisable(true);
        }
        else {
            textingButton.setDisable(false);
        }
    }

    /**
     * Send a text message with the URL of the map
     */
    public void sendMapToPhone(){
        TextingService textSender = new TextingService();
        ArrayList<String> directions = makeDirections(path);
        //this grabs the employee ID from the Application state and uses that to get the employee from the database, whose phone we want to use. and sends them the directions.
        if(!(phoneNumber.getText().equals(""))){
            String url = "https://softeng-teamn.github.io/index.html?dirs="+ convertDirectionsToParameterString(directions);
            textSender.textMap(phoneNumber.getText(), QRService.shortenURL(url));
        }
        else if(!(getApplicationState().getEmployeeLoggedIn().getPhone().equals(""))) {
            String url = "https://softeng-teamn.github.io/index.html?dirs="+ convertDirectionsToParameterString(directions);
            textSender.textMap(getApplicationState().getEmployeeLoggedIn().getPhone(), QRService.shortenURL(url));
        }
        else{
            System.out.println("NO PHONE NUMBER");
        }
    }

    /**
     * Compress a given set of directions into a series of characters
     * to be used in a QR code
     * Format: <Instruction> <Distance/Floor> <Hint>
     * @return the String to use in the QR code
     */
    private String convertDirectionsToParameterString(ArrayList<String> directions) {
        StringBuilder res = new StringBuilder();

        for (int i = 1; i < directions.size(); i++) {
            res.append(directions.get(i));
            res.append(",");
        }
        res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 1);

        return res.toString().replaceAll(" ", "\\$").replaceAll("&", "|");
    }

    /**
     * Given a set of directions, generate and place a QR code on the list of directions
     * @param directions the textual directions to generate a QR code from
     * @throws WriterException if the output fails
     * @throws IOException if the file is not found or cannot be written
     */
    public void generateQRCode(ArrayList<String> directions) throws WriterException, IOException {
        String url = "https://softeng-teamn.github.io/index.html?dirs="+ convertDirectionsToParameterString(directions);
        qrView.setImage(QRService.generateQRCode(url, true));
    }


    private void generateARQR(ArrayList<Node> path) throws IOException, WriterException {
        StringBuilder text = new StringBuilder();
        for (Node n : path) {
            if (!n.getFloor().equals("FL")) return;

            text.append(n.getNodeID());
            text.append("$");
        }
        text.deleteCharAt(text.lastIndexOf("$"));


        qrView.setImage(QRService.generateQRCode(text.toString(), false));
    }

    /**
     * Set whether the QR code is visible or not.
     */
    @FXML
    public void showQRCode() {
        if (qrCodeVbox.getMaxHeight() == 0) {
            qrCodeVbox.setPrefHeight(350);
            qrCodeVbox.setMaxHeight(350);
            qrView.setVisible(true);
        }
        else {
            qrCodeVbox.setPrefHeight(0);
            qrCodeVbox.setMaxHeight(0);
            qrView.setVisible(false);
        }
    }
}
