package edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import edu.wpi.cs3733d19.teamN.service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.MaintenanceRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class MaintenanceRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<MaintenanceRequest.MaintenanceType> type;

    @FXML
    private Label errorMsg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(MaintenanceRequest.MaintenanceType.values()));
        type.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(errorMsg.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                MaintenanceRequest maintenanceRequest = new MaintenanceRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
                maintenanceRequest.makeRequest();
                description.setText("");
                type.getSelectionModel().select(0);
                errorMsg.setTextFill(Color.BLACK);
                errorMsg.setText("Request Submitted!");
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}