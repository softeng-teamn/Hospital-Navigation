package application_state;

import map.Node;
import scheduler.model.Reservation;
import service.ResourceLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import static service.ResourceLoader.dfBundle;

/**
 *
 */
public class Event {

    // Used to indicate to all observers the action they should take
    String eventName = "";      // one of the event names below
                                // signifies field change

    String searchBarQuery = ""; // search-query, indicates typing in the node search text fields
    Node nodeSelected = null;   // node-select, indicates user has selected a node in the listview
    Node directionsNode = null;    // scroll-to-direction, indicates user has clicked a textual direction
    boolean isLoggedIn = false; // login, logout; indicates whether someone is logged in
    boolean isAdmin = false;    // admin, indicates whether user is admin
    boolean isAccessiblePath = false; // accessible, indicates whether user has selected the accessibility/no-stairs toggle
    String filterSearch = ""; // filtered search, indicates user has entered text in search field and to filter the listview
    String searchMethod = "astar"; // depth for DFS, breadth for BFS, astar for astar, dijsktra, best for bestFS; indicates which method is selected
    boolean isEditing = false;      // editing
    String floor = "1";      // floor, indicates current floor map displayed
    boolean callElev = false;
   // floor
    ArrayList<Node> path = null;    // The currently drawn path
    ResourceBundle currentBundle = dfBundle;



    URL theme = ResourceLoader.default_style;
    // for scheduling an event.
    ArrayList<GregorianCalendar> startAndEndTimes = null ;    // times.
    ArrayList<Reservation> repeatReservations = new ArrayList<>();
    String roomId = "" ;    // room
    private boolean actuallyRecurring = false;
    private String frequency = "Daily";


    public ResourceBundle getCurrentBundle() {
        return currentBundle;
    }

    public void setCurrentBundle(ResourceBundle currentBundle) {
        this.currentBundle = currentBundle;
    }

    public boolean isCallElev() {
        return callElev;
    }

    public void setCallElev(boolean callElev) {this.callElev = callElev;}

    public ArrayList<Node> getPath() { return path; }

    public void setPath(ArrayList<Node> path) { this.path = path; }

    public String getFloor() { return floor; }

    public void setFloor(String floor) { this.floor = floor; }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSearchBarQuery() {
        return searchBarQuery;
    }

    public boolean isAccessiblePath() {
        return isAccessiblePath;
    }

    public void setAccessiblePath(boolean accessiblePath) {
        isAccessiblePath = accessiblePath;
    }

    public void setSearchBarQuery(String searchBarQuery) {
        this.searchBarQuery = searchBarQuery;
    }

    public Node getNodeSelected() {
        return nodeSelected;
    }

    public void setNodeSelected(Node nodeSelected) { this.nodeSelected = nodeSelected; }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isAdmin() { return isAdmin; }

    public void setAdmin(boolean admin) { isAdmin = admin; }

    public String getFilterSearch() { return filterSearch; }

    public void setFilterSearch(String filterSearch) { this.filterSearch = filterSearch; }

    public String getSearchMethod() { return searchMethod; }

    public void setSearchMethod(String searchMethod) { this.searchMethod = searchMethod; }

    public ArrayList<GregorianCalendar> getStartAndEndTimes() { return startAndEndTimes; }

    public void setStartAndEndTimes(ArrayList<GregorianCalendar> startAndEndTimes) { this.startAndEndTimes = startAndEndTimes; }

    public String getRoomId() {return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Node getDirectionsNode() {
        return directionsNode;
    }

    public void setDirectionsNode(Node directionsNode) {
        this.directionsNode = directionsNode;
    }

    public URL getTheme() {
        return theme;
    }

    public void setTheme(URL theme) {
        this.theme = theme;
    }

    public boolean isActuallyRecurring() {
        return actuallyRecurring;
    }

    public void setActuallyRecurring(boolean actuallyRecurring) {
        this.actuallyRecurring = actuallyRecurring;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public ArrayList<Reservation> getRepeatReservations() {
        return repeatReservations;
    }

    public void setRepeatReservations(ArrayList<Reservation> repeatReservations) {
        this.repeatReservations = repeatReservations;
    }

    public void resetReservation() {
        startAndEndTimes = new ArrayList<>() ;    // times.
        repeatReservations = new ArrayList<>();
        roomId = "" ;    // room
        actuallyRecurring = false;
        frequency = "Daily";
    }
}
