package application_state;

import javafx.scene.image.ImageView;
import employee.model.Employee;
import map.Edge;
import map.Node;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton that holds information related to the state of the application
 */
public class ApplicationState {

    //******************************************

    private Node nodeToEdit;
    private Node DEFAULT_NODE = new Node("ARETL00101",1619,2522,"1","BTM","RETL","Cafe","Cafe");
    private Node startNode = DEFAULT_NODE;
    private Node endNode = null;
    private ArrayList<Edge> edgesToEdit;
    private FakeEventBus feb = new FakeEventBus();
    private static HashMap<String, ImageView> imageCache;
    private int employeeID;
    private Employee employeeLoggedIn;
    private String startEnd = "end";


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

    public FakeEventBus getFeb() {
        return feb;
    }

    public Employee getCurrentEmployee() { return employeeLoggedIn ; }

    public Employee getEmployeeLoggedIn() {
        return employeeLoggedIn;
    }

    public void setEmployeeLoggedIn(Employee employeeLoggedIn) {
        this.employeeLoggedIn = employeeLoggedIn;
    }

    public Node getStartNode() {
        return startNode;
    }

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

}
