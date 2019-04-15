package application_state;

import javafx.scene.image.ImageView;
import employee.model.Employee;
import javafx.stage.Stage;
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
    private ArrayList<Edge> edgesToEdit;
    private static HashMap<String, ImageView> imageCache;
    private int employeeID;
    private Employee employeeLoggedIn;
    private boolean isInactive;
    private InactivityManager IM;
    private Stage primaryStage;

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

    public Employee getEmployeeLoggedIn() {
        return employeeLoggedIn;
    }

    public void setEmployeeLoggedIn(Employee employeeLoggedIn) {
        this.employeeLoggedIn = employeeLoggedIn;
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
