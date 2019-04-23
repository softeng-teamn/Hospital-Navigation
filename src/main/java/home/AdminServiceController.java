package home;

import application_state.ApplicationState;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import application_state.Event;
import database.CSVService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

/**
 * Controls the admin options screen
 */
public class AdminServiceController {

    @FXML
    public JFXToggleNode bestFirstToggle;
    @FXML
    public JFXToggleNode dijsktraToggle;
    private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    private JFXButton fulfillRequestBtn, editEmployeeBtn, mapEditorController, exportCSVBtn, showHomeBtn, newNode_btn;

    @FXML
    private JFXToggleNode aStarToggle;

    @FXML
    private JFXTextField autoLogout;

    @FXML
    private JFXToggleNode depthFirstToggle;

    @FXML
    private JFXToggleNode breadthFirstToggle;

    @FXML
    private ToggleGroup algorithm;

    @FXML
    private void showFulfillRequest() throws Exception {
        Stage stage = (Stage) fulfillRequestBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Fulfill Request");
    }

    @FXML
    void showSearchResults(ActionEvent e) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("closeDrawer");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    @FXML
    private void showEditEmployees() throws Exception {
        Stage stage = (Stage) editEmployeeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.employeeEdit);
        StageManager.changeExistingWindow(stage, root, "Edit Employees");
    }

    @FXML
    private void exportCSV() throws IOException {
        CSVService.exportEdges();
        CSVService.exportNodes();
        CSVService.exportEmployees();
        CSVService.exportReservableSpaces();
    }

    /** change the pathfinding algorithm to A*
     * @param actionEvent FXML event that calls this method
     */
    public void astarSwitch(ActionEvent actionEvent) {
        algorithm.selectToggle(aStarToggle);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("methodSwitch");
        event.setSearchMethod("astar");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** change the pathfinding algorithm to depth-first
     * @param actionEvent FXML event that calls this method
     */
    public void depthSwitch(ActionEvent actionEvent) {
        algorithm.selectToggle(depthFirstToggle);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("methodSwitch");
        event.setSearchMethod("depth");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);    }

    /** switch the pathfinding algorithm to breadth-first
     * @param actionEvent FXML event that calls this method
     */
    public void breadthSwitch(ActionEvent actionEvent) {
        algorithm.selectToggle(breadthFirstToggle);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("methodSwitch");
        event.setSearchMethod("breadth");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** Switch the screen to add node
     * @param e FXML event that calls this method
     * @throws Exception if the FXML fails to load
     */
    @FXML
    void showNewNode(ActionEvent e) throws  Exception{
        Parent root = FXMLLoader.load(ResourceLoader.createNode);
        Stage stage = (Stage) newNode_btn.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Add Node");
    }

    @FXML
    void initialize() {    // Select the current method
        if (event.getSearchMethod().equals("astar")) {
            algorithm.selectToggle(aStarToggle);
        }
        else if (event.getSearchMethod().equals("breadth")) {
            algorithm.selectToggle(breadthFirstToggle);
        }
        else if (event.getSearchMethod().equals("depth")){
            algorithm.selectToggle(depthFirstToggle);
        }
        else if (event.getSearchMethod().equals("best")){
            algorithm.selectToggle(bestFirstToggle);
        }
        else if (event.getSearchMethod().equals("dijsktra")){
            algorithm.selectToggle(dijsktraToggle);
        }

    }

    public void bestSwitch(ActionEvent actionEvent) {
        algorithm.selectToggle(bestFirstToggle);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("methodSwitch");
        event.setSearchMethod("best");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void dijsktraSwitch(ActionEvent actionEvent) {
        algorithm.selectToggle(dijsktraToggle);
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("methodSwitch");
        event.setSearchMethod("dijsktra");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void updateLogout(ActionEvent actionEvent){
        if(autoLogout.getText().matches("^[0-9]*$")) {
            ApplicationState.getApplicationState().setIMTimeOut(autoLogout.getText() + "000" );
        }
    }
}
