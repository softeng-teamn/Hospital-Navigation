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
    // To know when to genPath           navigation

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


}
