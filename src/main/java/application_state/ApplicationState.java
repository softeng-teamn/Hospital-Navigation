package application_state;

import javafx.scene.image.ImageView;
import employee.model.Employee;
import javafx.stage.Stage;
import map.Edge;
import map.Node;
import service.ResourceLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton that holds information related to the state of the application
 */
public class ApplicationState {

    //******************************************

    private Node nodeToEdit;
    private Node DEFAULT_NODE = new Node("ARETL00101",1619,2522,"1","BTM","RETL","Cafe","Cafe");    // The default location of this kiosk
    private Node startNode = DEFAULT_NODE;    // The path start node
    private Node endNode = null;    // The path end node
    private ArrayList<Edge> edgesToEdit;
    private ObservableBus observableBus = new ObservableBus();    // The observable object
    private static HashMap<String, ImageView> imageCache;
    private int employeeID;
    private Employee employeeLoggedIn;
    private String startEnd = "end";    // Whether the currently selected node in the listView is the start or end node
    private boolean isInactive;
    private InactivityManager IM = new InactivityManager(90000);
    private Stage primaryStage;
    // sets the current theme
    private URL currentTheme = ResourceLoader.default_style;

    // Scheduler settings
    private int openTime = 9;   // hour to start schedule display
    private String openTimeStr = "09:00";    // open time as string
    private int openTimeMinutes = 0;
    private int closeTime = 22;    // 24-hours hour to end schedule display
    private String closeTimeString = "22:00";    // close time as string
    private int closeTimeMinutes = 0;
    private int timeStep = 2;    // Fractions of an hour
    private boolean boundOpenTime = true;    // Whether open time is defined
    private boolean boundCloseTime = true;    // Whether close time is defined
    private boolean boundMinRes = true;    // Whether minimum reservation is defined
    private int minRes = 15;
    private boolean snapToMinutes = true;
    private boolean allowMultidayRes = false;
    private boolean allowRecurringRes = false;
    private static boolean showContactInfo = true;

    //******************************************

    private static class SingletonHelper {
        private static final ApplicationState appState = new ApplicationState();
    }

    /** Allow other classes to access the application state.
     * @return the ApplicationState object
     */
    public static ApplicationState getApplicationState() {
        return SingletonHelper.appState;
    }

    private ApplicationState() {

    }

    public static HashMap<String, ImageView> getImageCache() {
        return imageCache;
    }

    public static void setImageCache(HashMap<String, ImageView> imageCache) {
        ApplicationState.imageCache = imageCache;
    }

    public Node getNodeToEdit() {
        return nodeToEdit;
    }

    public void setNodeToEdit(Node nodeToEdit) {
        this.nodeToEdit = nodeToEdit;
    }

    public ArrayList<Edge> getEdgesToEdit() {
        return edgesToEdit;
    }

    public void setEdgesToEdit(ArrayList<Edge> edgesToEdit) {
        this.edgesToEdit = edgesToEdit;
    }

    public ObservableBus getObservableBus() {
        return observableBus;
    }

    public Employee getEmployeeLoggedIn() {
        return employeeLoggedIn;
    }

    public void setEmployeeLoggedIn(Employee employeeLoggedIn) {
        this.employeeLoggedIn = employeeLoggedIn;
    }

    // methods for controlling the current theme
    public URL getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(URL currentTheme) {
        this.currentTheme = currentTheme;
    }


    /**
     * Return the current path's start node.
     * @return the current path's start node
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * Set the current path's start node.
     * @param startNode current path's start node
     */
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public void setDefaultStartNode (){this.startNode = DEFAULT_NODE;}

    public String getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(String startEnd) {
        this.startEnd = startEnd;
    }

    public Node getDEFAULT_NODE() {
        return DEFAULT_NODE;
    }


    public boolean isInactive() {
        return isInactive;
    }

    public void setInactive(boolean inactive) {
        isInactive = inactive;
    }

    public InactivityManager getIM() {
        return IM;
    }

    public void setIMTimeOut(String num){
        IM.setInactivityLimit(Integer.parseInt(num));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public int getOpenTime() {
        return openTime;
    }

    public void setOpenTime(int openTime) {
        this.openTime = openTime;
    }

    public String getOpenTimeStr() {
        return openTimeStr;
    }

    public void setOpenTimeStr(String openTimeStr) {
        this.openTimeStr = openTimeStr;
    }

    public int getOpenTimeMinutes() {
        return openTimeMinutes;
    }

    public void setOpenTimeMinutes(int openTimeMinutes) {
        this.openTimeMinutes = openTimeMinutes;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
        this.closeTime = closeTime;
    }

    public String getCloseTimeString() {
        return closeTimeString;
    }

    public void setCloseTimeString(String closeTimeString) {
        this.closeTimeString = closeTimeString;
    }

    public int getCloseTimeMinutes() {
        return closeTimeMinutes;
    }

    public void setCloseTimeMinutes(int closeTimeMinutes) {
        this.closeTimeMinutes = closeTimeMinutes;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

    public boolean isBoundOpenTime() {
        return boundOpenTime;
    }

    public void setBoundOpenTime(boolean boundOpenTime) {
        this.boundOpenTime = boundOpenTime;
    }

    public boolean isBoundCloseTime() {
        return boundCloseTime;
    }

    public void setBoundCloseTime(boolean boundCloseTime) {
        this.boundCloseTime = boundCloseTime;
    }

    public boolean isBoundMinRes() {
        return boundMinRes;
    }

    public void setBoundMinRes(boolean boundMinRes) {
        this.boundMinRes = boundMinRes;
    }

    public boolean isSnapToMinutes() {
        return snapToMinutes;
    }

    public void setSnapToMinutes(boolean snapToMinutes) {
        this.snapToMinutes = snapToMinutes;
    }

    public boolean isAllowMultidayRes() {
        return allowMultidayRes;
    }

    public void setAllowMultidayRes(boolean allowMultidayRes) {
        this.allowMultidayRes = allowMultidayRes;
    }

    public boolean isAllowRecurringRes() {
        return allowRecurringRes;
    }

    public void setAllowRecurringRes(boolean allowRecurringRes) {
        this.allowRecurringRes = allowRecurringRes;
    }

    public static boolean isShowContactInfo() {
        return showContactInfo;
    }

    public static void setShowContactInfo(boolean showContactInfo) {
        ApplicationState.showContactInfo = showContactInfo;
    }

    public int getMinRes() {
        return minRes;
    }

    public void setMinRes(int minRes) {
        this.minRes = minRes;
    }
}
