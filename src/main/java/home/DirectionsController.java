package home;

import application_state.ApplicationState;
import application_state.Observer;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import application_state.Event;
import map.MapController;
import map.Node;
import service.TextingService;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectionsController implements Observer {
    private Event event;

    @FXML
    private JFXButton home_btn, unitSwitch_btn;

    @FXML
    private JFXListView<Label> directionsView;

    @FXML
    private JFXButton textingButton;

    //text message global variable
    private String units = "feet";    // Feet or meters conversion
    private HashMap<String, Integer> floors = new HashMap<String, Integer>();
    private ArrayList<Node> path;


    @FXML
    void initialize() {
        ApplicationState.getApplicationState().getFeb().register("directionsContoller",this);
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        System.out.println("initialize: " + this);
        path = event.getPath();
        printDirections(makeDirections(path));
    }

    @FXML
    void showSearchList(ActionEvent e) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("showSearch");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    @Override
    public void notify(Object o) {
        event = (Event) o;
        switch (event.getEventName()) {
            case "printText":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        path = event.getPath();
                        printDirections(makeDirections(path));
                    }
                });
                break;
            default:
                break;
        }

    }


    // TODO: why does 1st instruction turn into ... if not scrollable?
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

        if (path == null || path.size() < 2) {
            return null;
        }

        final int NORTH_I = 1122 - 1886;    // Measurements from maps
        final int NORTH_J = 642 - 1501;    // Measurements from maps

        ArrayList<String> directions = new ArrayList<>();    // Collection of instructions
        directions.add("\nStart at " + path.get(0).getLongName() + ".\n");    // First instruction

        // Make the first instruction cardinal, or up/down if it is a floor connector
        String oldFloor = path.get(0).getFloor();
        String newFloor = path.get(1).getFloor();
        if (!floors.get(oldFloor).equals(floors.get(newFloor))) {
            directions.add(upDownConverter(oldFloor, newFloor, path.get(0).getNodeType()));
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("ELEV")) || (path.size() > 2 && path.get(1).getNodeType().equals("ELEV") && path.get(2).getNodeType().equals("ELEV"))) {
            directions.add("I");
        }
        else if ((path.size() == 2 && path.get(1).getNodeType().equals("STAI")) || (path.size() > 2 && path.get(1).getNodeType().equals("STAI") && path.get(2).getNodeType().equals("STAI"))) {
            directions.add("J");
        }
        else {
            directions.add(convertToCardinal(csDirPrint(path.get(0).getXcoord() + NORTH_I, path.get(0).getYcoord() + NORTH_J, path.get(0), path.get(1))));
        }

        boolean afterFloorChange = false;    // If we've just changed floors, give a cardinal direction
        for (int i = 0; i < path.size() - 2; i++) {    // For each node in the path, make a direction
            String oldFl = (path.get(i+1).getFloor());
            String newFl = (path.get(i+2).getFloor());
            if (afterFloorChange && !path.get(i + 2).getNodeType().equals("ELEV") && !path.get(i + 2).getNodeType().equals("STAI")) {
                afterFloorChange = false;
                directions.add(convertToCardinal(csDirPrint(path.get(i+1).getXcoord() + NORTH_I, path.get(i+1).getYcoord() + NORTH_J, path.get(i+1), path.get(i+2))));
            }
            else if(!path.get(i+1).getNodeType().equals("ELEV") && !path.get(i+1).getNodeType().equals("STAI") && (path.get(i+2).getNodeType().equals("ELEV") || path.get(i+2).getNodeType().equals("STAI"))
                    && ((i < path.size() - 3 && (path.get(i+3).getNodeType().equals("ELEV") || path.get(i+3).getNodeType().equals("STAI"))) || i == path.size() -3)) {    // If next node is elevator, say so
                if (path.get(i+2).getNodeType().equals("ELEV")) {
                    directions.add("I");
                } else {
                    directions.add("J");
                }
            }
            else if (!floors.get(oldFl).equals(floors.get(newFl))) {    // Otherwise if we're changing floors, give a floor change direction
                directions.add(upDownConverter(oldFl, newFl, path.get(i+1).getNodeType()));
                afterFloorChange = true;
            }
            else {    // Otherwise provide a normal direction
                directions.add(csDirPrint(path.get(i), path.get(i+1), path.get(i+2)));
                afterFloorChange = false;
            }
        }

        //System.out.println("before simplifying: " + directions);
        // Simplify directions that continue approximately straight from each other
        for (int i = 1; i < directions.size(); i++) {
            String currDir = directions.get(i);
            String currOne = currDir.substring(0,1);
            String prevDir = directions.get(i-1);
            String prevOne = prevDir.substring(0,1);
            String newDir = "";
            boolean changed = false;
            if (currOne.equals("A") && !"IJ".contains(prevOne)) {
                int prevDist = Integer.parseInt(prevDir.substring(1,6));
                int currDist = Integer.parseInt(currDir.substring(1,6));
                double totalDist = prevDist + currDist;    // Combine the distance of this direction with the previous one
                newDir = prevOne + padWithZeros(totalDist) + currDir.substring(6);
                changed = true;
            }
            else if ("NOPQ".contains(currOne) && currOne.equals(prevOne)) {    // If the current direction contains straight, get the distance substring
                newDir = currOne + prevDir.substring(1, 2) + currDir.substring(2, 3);
                changed = true;
            }
            if (changed) {
                directions.remove(i);
                directions.remove(i-1);
                directions.add(i-1, newDir);
                i--;
            }
        }

        // Add the final direction
        directions.add("You have arrived at " + path.get(path.size() - 1).getLongName() + ".");
        System.out.println(directions); // TODO cut
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
     * @param ds the list of directions as strings
     * @return a String that is the sum of all the directions
     */
    public String printDirections(ArrayList<String> ds) {
        HashMap<String, String> backToFloors = new HashMap<>();
        backToFloors.put("A", "L2");
        backToFloors.put("B", "L1");
        backToFloors.put("C", "G");
        backToFloors.put("D", "1");
        backToFloors.put("E", "2");
        backToFloors.put("F", "3");
        ArrayList<String> directions = new ArrayList<>();
        directions.add(ds.get(0));
        ObservableList<Label> dirs = FXCollections.observableArrayList();
        ArrayList<Label> labels = new ArrayList<>();

        Label first = new Label(ds.get(0));
        first.setWrapText(true);
        first.setTextFill(Color.WHITE);
        //first.setPrefWidth(450);
        labels.add(first);

        for (int i = 1; i < ds.size() - 1; i++) {
            String direct = ds.get(i);
            switch(direct.substring(0,1)) {
                case "A":
                    direct = "Walk straight for " + Integer.parseInt(direct.substring(1,6)) + " " + units + direct.substring(6) + ".\n";
                    break;
                case "B":
                    direct = "Turn left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "C":
                    direct = "Turn slightly left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "D":
                    direct = "Turn sharply left and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "E":
                    direct = "Turn right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "F":
                    direct = "Turn slightly right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "G":
                    direct = "Turn sharply right and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "H":
                    direct = "Turn around and walk for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "I":
                    direct = "Walk to the elevator.\n";
                    break;
                case "J":
                    direct = "Walk to the stairs.\n";
                    break;
                case "N":
                    direct = "Take the elevator up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "O":
                    direct = "Take the elevator down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "P":
                    direct = "Take the stairs up from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "Q":
                    direct = "Take the stairs down from floor " + backToFloors.get(direct.substring(1,2)) + " to floor " + backToFloors.get(direct.substring(2,3)) + ".\n";
                    break;
                case "S":
                    direct = "Walk north for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "T":
                    direct = "Walk north west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "U":
                    direct = "Walk west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "V":
                    direct = "Walk south west for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "W":
                    direct = "Walk south for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "X":
                    direct = "Walk south east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "Y":
                    direct = "Walk east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                case "Z":
                    direct = "Walk north east for " + Integer.parseInt(direct.substring(1,6)) + " " + units  + direct.substring(6) + ".\n";
                    break;
                default:
                    direct = "Houston we have a problem";
                    break;
            }

            Label l = new Label(direct);
            l.setWrapText(true);
           // l.setPrefWidth(450); todo: get text to wrap
            l.setTextFill(Color.WHITE);
            labels.add(l);
            directions.add(direct);
        }
        directions.add(ds.get(ds.size() -1));

        Label last = new Label(ds.get(ds.size() - 1));
        last.setWrapText(true);
       // last.setPrefWidth(450);
        last.setTextFill(Color.WHITE);
        labels.add(last);

        dirs.addAll(labels);
        directionsView.setItems(dirs);

        // Return the directions
        directions.add(ds.get(ds.size() -1));
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < directions.size(); ++i) {
            buf.append(directions.get(i));
        }
        String total = buf.toString();
        System.out.println(total);
        return total;
    }

    /**
     * Convert this direction to a cardinal direction
     * ONLY WHEN MADE WITH SOUTH AS THE FIRST VECTOR
     * @param cardinal the direction with the first vector going south
     * @return the direction as a cardinal direction
     */
    public String convertToCardinal(String cardinal) {
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

    public String csDirPrint(int x, int y, Node curr, Node next) {
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
    public String csDirPrint(Node prev, Node curr, Node next) {
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

        // TODO: landmarks
        String landmark = "";
        ArrayList<Node> closeNodes = MapController.getNodesConnectedTo(next);
        //  ArrayList<Node> closeNodes = DatabaseService.getDatabaseService().getNodesConnectedTo(next);
        for (Node n: closeNodes) {
            if (!landmark.contains("towards") && n.getNodeType().equals("HALL")) {
                landmark = " down the hall";
            }
            if (!n.getNodeType().equals("HALL") && !n.getNodeType().equals("ELEV") && !n.getNodeType().equals("STAI")) {
                landmark = " towards " + n.getLongName();
            }
        }
        if (!next.getNodeType().equals("HALL") && !next.getNodeType().equals("ELEV") && !next.getNodeType().equals("STAI")) {
            landmark = " towards " + next.getLongName();
        }

        // Create and return the direction
        String distPadded = padWithZeros(distance);    // Pad direction with zeros so always same lengtb
        String direction = turn + distPadded + landmark;
        return direction;
    }

    /**
     * Pad a distance with zeros so all distances are the same length
     * @param distance the distance to pad with zeros
     * @return the padded distance as a string
     */
    private String padWithZeros(double distance) {
        String orig = String.format("%.0f", distance);
        for (int i = 0; i < 6; i++) {    // Assume max distance is less than 99999 feet
            if (orig.length() < i) {    // Pad with zeroes while string length is less than 6
                orig = "0" + orig;
            }
        }
        return orig;
    }

    /**
     * On click, show the directions. On second click, hide them again.
     */
//    @FXML
//    public void showDirections() {
//        directionsView.setVisible(!directionsView.isVisible());
//        if (showDirectionsBtn.getText().contains("Show")) {
//            showDirectionsBtn.setText("Close Textual Directions");
//            directionsView.toFront();
//        }
//        else {
//            showDirectionsBtn.setText("Show Textual Directions");
//            showDirVbox.toFront();
//        }
//        if (showDirVbox.getAlignment().equals(Pos.BOTTOM_RIGHT)) {
//            showDirVbox.setAlignment(Pos.TOP_RIGHT);
//        }
//        else {
//            showDirVbox.setAlignment(Pos.BOTTOM_RIGHT);
//        }
//    }   todo hmmm still needed or not?

//    private void hideDirections() {
//        showDirectionsBtn.setText("Show Textual Directions");
//        showDirVbox.toFront();
//        showDirVbox.setAlignment(Pos.BOTTOM_RIGHT);
//        directionsView.setVisible(false);
//    }

    /**
     * Get the current units
     * @return current units: feet or meters
     */
    public String getUnits() {
        return units;
    }

    // TODO:  cut print statements.

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
    }

    public void setPath(ArrayList<Node> thePath) {
        this.path = thePath;
    }

    /**
     * Send a text message with the URL of the map
     */
    public void sendMapToPhone(){
        TextingService textSender = new TextingService();
        textSender.textMap("+19787298044",printDirections(makeDirections(path)));
    }

    /**
     * Compress a given set of directions into a series of characters
     * to be used in a QR code
     * Format: <Instruction> <Distance/Floor> <Hint>
     * @return the String to use in the QR code
     */
    private String convertToQRCode(ArrayList<String> directions) {
        // TODO if necc - ex all into one
        return "";
    }

}
