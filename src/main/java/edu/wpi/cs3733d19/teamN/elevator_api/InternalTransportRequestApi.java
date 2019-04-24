package edu.wpi.cs3733d19.teamN.elevator_api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.wpi.cs3733d19.teamN.elevator.ElevatorConnection;
import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Team N Internal Transport Request API
 * This API can, but does not have to, integrate with Team N's physical elevator.
 * When a high urgency transport request is assigned to an employee, an elevator is
 * called to the floor of the request.
 *
 * You can check the remote status of the elevator using the provided functions.
 *
 * To schedule a time to practice with the elevator or for any other questions, reach out to Team N.
 *
 */
public class InternalTransportRequestApi {
    ApiDatabaseService myDBS = ApiDatabaseService.getDatabaseService();

    static String originNodeID;
    private String team;
    private boolean useElev;

    /**
     * Use me if you don't want to use the elevator
     * @param xcoord where you want the top left corner x coordinate to be
     * @param ycoord where you want the top left corner y coordinate to be
     * @param windowWidth the desired window width
     * @param windowLength the desired window length
     * @param cssPath path to custom css to use
     * @param destination unused
     * @param origin the node where you want the service request to be fulfilled
     * @throws ServiceException never
     */
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
     * Use me if you want to use the elevator. The elevator will be called when a request is assigned to an employee
     * @param xcoord where you want the top left corner x coordinate to be
     * @param ycoord where you want the top left corner y coordinate to be
     * @param windowWidth the desired window width
     * @param windowLength the desired window length
     * @param cssPath path to custom css to use
     * @param destination unused
     * @param origin the node where you want the service request to be fulfilled. The last two characters must bo "01" - "04"
     *               corresponding to the floor the elevator will be called to.
     * @param useElev true if you want the elevator to be active
     * @param team "L", "M", "N", "O", "P" corresponding to your team name
     * @throws ServiceException never
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
     * @param t "L", "M", "N", "O", "P" corresponding to your team name
     * @return a string containing elevator L's current floor for the team specified
     */
    public String getCurrentElevFloor(String t) {
        ElevatorConnection e = new ElevatorConnection();
        try {
           return e.getFloor(t + "L");
        } catch (IOException e1) {
            System.out.println("Error Connecting to Elevator getCurrentElev in internal Transport Req API");
            e1.printStackTrace();
        }
        return "";
    }

    /**
     * Insert an employee into the API database - you need at least one. Only username and ID are used, ID must be &gt; 0 (-1 and 0 are forbidden)
     * @param e The employee to insert
     * @return true if the employee was successfully inserted
     */
    public boolean insertEmployee(Employee e) {
        return myDBS.insertEmployee(e);
    }

    // Get an employee by id
    public Employee getEmployee(int id) {
        return myDBS.getEmployee(id);
    }

    // Update an employee
    public boolean updateEmployee(Employee e) {
        return myDBS.updateEmployee(e);
    }

    // Delete an employee
    public boolean deleteEmployee(Employee e) {
        return myDBS.deleteEmployee(e);
    }

    // Get an employee by username
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
