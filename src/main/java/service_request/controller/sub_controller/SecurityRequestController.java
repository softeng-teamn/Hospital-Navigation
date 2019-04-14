package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleNode;
import service_request.controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import service_request.model.sub_model.SecurityRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class SecurityRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private ToggleGroup urgency;

    @FXML
    private JFXToggleNode urgency_low;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urgency.selectToggle(urgency_low);
    }

    @FXML
    void submitRequest(ActionEvent event) {

        SecurityRequest.Urgency urgencyLevel = SecurityRequest.Urgency.NOT;

        JFXToggleNode selected = (JFXToggleNode) urgency.getSelectedToggle();

        if (selected != null) {
            switch (selected.getText()) {
                case "Low":
                    urgencyLevel = SecurityRequest.Urgency.NOT;
                    break;
                case "Medium":
                    urgencyLevel = SecurityRequest.Urgency.SOMEWHAT;
                    break;
                case "High":
                    urgencyLevel = SecurityRequest.Urgency.VERY;
                    break;
                default:
                    urgencyLevel = SecurityRequest.Urgency.NOT;
            }
        }

        if(selectedNode != null) {
            SecurityRequest securityRequest = new SecurityRequest(-1, description.getText(), selectedNode, false, urgencyLevel);
            securityRequest.makeRequest();
            description.setText("");
            urgency.selectToggle(urgency_low);
        }
    }
}