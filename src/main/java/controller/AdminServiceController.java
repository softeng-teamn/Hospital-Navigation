package controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.CSVService;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminServiceController extends Controller{

    @FXML
    private JFXButton fulfillRequestBtn, editEmployeeBtn, mapEditorController, exportCSVBtn, showHomeBtn;

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
    private void showMapEditor() throws Exception {
        Stage stage = (Stage) editEmployeeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeExistingWindow(stage, root, "Edit Map");
        // TODO: verify/change that this is how you edit the map; vs going to home controller?
    }

    @FXML
    private void exportCSV() throws IOException {
        CSVService.exportEdges();
        CSVService.exportNodes();
        CSVService.exportEmployees();
        CSVService.importEmployees();
    }
}
