package elevator_api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import elevator.ElevatorConnection;
import employee.model.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * API main class
 */
public class InternalTransportRequestApi {
    ApiDatabaseService myDBS = ApiDatabaseService.getDatabaseService();

    static String originNodeID;
    private String team;
    private boolean useElev;

    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Can't figure out a better way (yet)")
    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destination, String origin) throws ServiceException {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/api/api.fxml"), ResourceBundle.getBundle("strings", Locale.getDefault()));

        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println("failed to load the file");
            e.printStackTrace();
            return;
        }

        originNodeID = (origin == null ? "" : origin);

        Scene scene = new Scene(root, (double) windowWidth, (double) windowLength);
        primaryStage.setX((double) xcoord);
        primaryStage.setY((double) ycoord);
        primaryStage.setTitle("Internal Transport Request");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource(cssPath).toExternalForm());
        primaryStage.show();

        useElev = false;
        myDBS.setCallElev(useElev);
    }


    /**
     *
     * @param xcoord of node
     * @param ycoord of node
     * @param windowWidth of window
     * @param windowLength of window
     * @param cssPath make it pretty
     * @param destination where do i go
     * @param origin where did i start
     * @param useElev true if you want to use the elevator
     * @param team the team letter in upper case
     * @throws ServiceException
     */
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Can't figure out a better way (yet)")
    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destination, String origin, boolean useElev, String team) throws ServiceException {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/api/api.fxml"), ResourceBundle.getBundle("strings", Locale.getDefault()));

        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println("failed to load the file");
            e.printStackTrace();
            return;
        }

        originNodeID = (origin == null ? "" : origin);

        Scene scene = new Scene(root, (double) windowWidth, (double) windowLength);
        primaryStage.setX((double) xcoord);
        primaryStage.setY((double) ycoord);
        primaryStage.setTitle("Internal Transport Request");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource(cssPath).toExternalForm());
        primaryStage.show();

        this.useElev = useElev;
        this.team = team;
        myDBS.setTeam(team);
        myDBS.setCallElev(useElev);
    }

    /**
     *
     * @return a string containing elevator L's current floor. empty string if error
     */
    public String getCurrentElevFloor(){
        ElevatorConnection e = new ElevatorConnection();
        try {
           return e.getFloor(team + "L");
        } catch (IOException e1) {
            System.out.println("Error Connecting to Elevator getCurrentElev in internal Transport Req API");
            e1.printStackTrace();
        }
        return "";
    }

    public boolean insertEmployee(Employee e) {
        return myDBS.insertEmployee(e);
    }

    public Employee getEmployee(int id) {
        return myDBS.getEmployee(id);
    }

    public boolean updateEmployee(Employee e) {
        return myDBS.updateEmployee(e);
    }

    public boolean deleteEmployee(Employee e) {
        return myDBS.deleteEmployee(e);
    }

    Employee getEmployeeByUsername(String username) {
        return myDBS.getEmployeeByUsername(username);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertInternalTransportRequest(ApiInternalTransportRequest req) {
        return myDBS.insertInternalTransportRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the InternalTransportRequest service_request object with the given ID
     */
    ApiInternalTransportRequest getInternalTransportRequest(int id) {
        return myDBS.getInternalTransportRequest(id);
    }

    /**
     * @return all internal service requests stored in the database in a List.
     */
    ArrayList<ApiInternalTransportRequest> getAllInternalTransportRequests() {
        return myDBS.getAllInternalTransportRequests();
    }

    /**
     * updates a given service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateInternalTransportRequest(ApiInternalTransportRequest req) {
        return myDBS.updateInternalTransportRequest(req);
    }

    /**
     * deletes a given service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteInternalTransportRequest(ApiInternalTransportRequest req) {
        return myDBS.deleteInternalTransportRequest(req);
    }
}
