package model;

public class Event {

    Node DEFAULT_NODE = new Node("ARETL00101",1619,2522,"1","BTM","RETL","Cafe","Cafe");

    String eventName = "";      // one of the event names below
                                // signifies field change


    String searchBarQuery = ""; // search-query
    Node nodeStart = DEFAULT_NODE; // node-start
    Node nodeSelected = null;   // node-select
    boolean isLoggedIn = false; // login
    boolean isAdmin = false;    // admin
    boolean isAccessiblePath = false; // accessible
    boolean isChangingStart = false;  // start-change
    String filterSearch = ""; // filtered search
    String searchMethod = "astar"; // depth for DFS, breadth for BFS, astar for astar
    boolean isEditing = false;      // editing
    String floor = "01";      // floor
    boolean callElev = false;

    public boolean isCallElev() {
        return callElev;
    }

    public void setCallElev(boolean callElev) {this.callElev = callElev;}

    boolean endNode = true; //true for end node, false for start node


    public boolean isEndNode() { return endNode; }

    public void setEndNode(boolean startEnd) { this.endNode = startEnd; }

    public void setDefaultStartNode (){this.nodeStart = DEFAULT_NODE;}

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

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

    public Node getDefaultNode() {
        return DEFAULT_NODE;
    }

    public Node getNodeStart() {
        return nodeStart;
    }

    public void setNodeStart(Node nodeStart) {
        this.nodeStart = nodeStart;
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

    public void setNodeSelected(Node nodeSelected) {
        this.nodeSelected = nodeSelected;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFilterSearch() { return filterSearch; }

    public void setFilterSearch(String filterSearch) { this.filterSearch = filterSearch; }

    public String getSearchMethod() { return searchMethod; }

    public void setSearchMethod(String searchMethod) { this.searchMethod = searchMethod; }
}
