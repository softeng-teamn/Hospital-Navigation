package controller;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Event;
import model.EventBusFactory;
import service.CSVService;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminServiceController extends Controller{

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
    private void showHome() throws Exception {
        Stage stage = (Stage) fulfillRequestBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home");
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

    @FXML
    public void showSearch(ActionEvent actionEvent) {
        event.setEventName("showSearch");
        eventBus.post(event);
    }
}
