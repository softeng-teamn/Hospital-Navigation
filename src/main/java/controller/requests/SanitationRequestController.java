package controller.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.MaintenanceRequest;
import model.request.SanitationRequest;
import service.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class SanitationRequestController extends RequestController {
    @FXML
    private JFXTextArea notes;

    @FXML
    private JFXComboBox<String> urgencyBox, materialBox;

    @FXML
    private JFXButton submitBtn;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set urgency options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Low",
                        "Medium",
                        "High"
                );
        urgencyBox.setItems(options);

        // Set state options
        options =
                FXCollections.observableArrayList(
                        "Liquid",
                        "Solid",
                        "Mixture",
                        "Other"
                );
        materialBox.setItems(options);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            SanitationRequest sanitationRequest = new SanitationRequest(-1, notes.getText(), selectedNode, false, urgencyBox.getValue(), materialBox.getValue());
            sanitationRequest.makeRequest();
        }
    }

}
