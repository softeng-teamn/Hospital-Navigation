package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.MaintenanceRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class MaintenanceRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<MaintenanceRequest.MaintenanceType> type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(MaintenanceRequest.MaintenanceType.values()));
        type.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            MaintenanceRequest maintenanceRequest = new MaintenanceRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            maintenanceRequest.makeRequest();
            description.setText("");
            type.getSelectionModel().select(0);
        }
    }
}