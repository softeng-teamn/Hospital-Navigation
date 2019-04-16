package application_state;

import map.Node;

import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import static service.ResourceLoader.dfBundle;

import java.util.ArrayList;

/**
 *
 */
public class Event {

    String eventName = "";      // one of the event names below
                                // signifies field change

    String searchBarQuery = ""; // search-query
    Node nodeSelected = null;   // node-select
    Node directionsNode = null;    // scroll-to-direction
    boolean isLoggedIn = false; // login, logout
    boolean isAdmin = false;    // admin
    boolean isAccessiblePath = false; // accessible
    boolean isChangingStart = false;  // start-change
    String filterSearch = ""; // filtered search
    String searchMethod = "astar"; // depth for DFS, breadth for BFS, astar for astar, dijsktra, best for bestFS
    boolean isEditing = false;      // editing
    String floor = "1";      // floor
    boolean callElev = false;
   // floor
    ArrayList<Node> path = null;
    ResourceBundle currentBundle = dfBundle;


    // for scheduling an event
    ArrayList<GregorianCalendar> startAndEndTimes = null ; // times
    String roomId = "" ;    // room



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

    public boolean isChangingStart() {
        return isChangingStart;
    }

    public void setChangingStart(boolean changingStart) {
        isChangingStart = changingStart;
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
}
