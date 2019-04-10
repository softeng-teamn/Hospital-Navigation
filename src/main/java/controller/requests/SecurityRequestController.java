package controller.requests;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleNode;
import controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import model.request.SecurityRequest;

public class SecurityRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private ToggleGroup urgency;

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
            urgency.selectToggle(null);
        }
    }
}