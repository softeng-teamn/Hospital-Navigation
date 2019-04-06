package model;

public class Event {

    String eventName = "";      // one of the event names below
                                // signifies field change


    String searchBarQuery = ""; // search-query
    Node nodeSelected = null;   // node-select
    boolean isLoggedIn = false; // login
    boolean isAdmin = false;    // admin
    boolean isAccessiblePath = false; // accessible





    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSearchBarQuery() {
        return searchBarQuery;
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

    public boolean isAccessiblePath() { return isAccessiblePath; }

    public void setAccessiblePath(boolean accessiblePath) { isAccessiblePath = accessiblePath; }


}
