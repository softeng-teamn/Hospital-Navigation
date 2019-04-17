package home;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import application_state.Event;
import application_state.EventBusFactory;
import database.CSVService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

public class AdminServiceController {

    private EventBus eventBus = EventBusFactory.getEventBus();
    private Event event = EventBusFactory.getEvent();


    @FXML
    private JFXButton fulfillRequestBtn, editEmployeeBtn, mapEditorController, exportCSVBtn, showHomeBtn, newNode_btn;

    @FXML
    private JFXToggleNode aStarToggle;

    @FXML
    private JFXToggleNode depthFirstToggle;

    @FXML
    private JFXToggleNode breadthFirstToggle;

    @FXML
    private void showFulfillRequest() throws Exception {
        Stage stage = (Stage) fulfillRequestBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Fulfill Request");
    }

    @FXML
    void showSearchResults(ActionEvent e) {
        event.setEventName("closeDrawer");
        eventBus.post(event);
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

    public void astarSwitch(ActionEvent actionEvent) {
        event.setEventName("methodSwitch");
        event.setSearchMethod("astar");
        eventBus.post(event);
    }

    public void depthSwitch(ActionEvent actionEvent) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!! function called !!!!!!!!!!!!!!!!!!");
        event.setEventName("methodSwitch");
        event.setSearchMethod("depth");
        eventBus.post(event);
    }

    public void breadthSwitch(ActionEvent actionEvent) {
        event.setEventName("methodSwitch");
        event.setSearchMethod("breadth");
        eventBus.post(event);
    }

    @FXML
    void showNewNode(ActionEvent e) throws  Exception{
        Parent root = FXMLLoader.load(ResourceLoader.createNode);
        Stage stage = (Stage) newNode_btn.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Add Node");
    }
}
