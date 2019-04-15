package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import service_request.controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.MedicineRequest;

public class MedicineRequestController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXTextField medicineType, quantity;

    @FXML
    private Label errorMsg;

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            try {

                MedicineRequest medicineRequest = new MedicineRequest(-1, description.getText(), selectedNode, false, medicineType.getText(), Double.parseDouble(quantity.getText()));
                medicineRequest.makeRequest();

                description.setText("");
                medicineType.setText("");
                quantity.setText("");
                quantity.getStyleClass().remove("wrong-credentials");
            } catch (NumberFormatException e) {
                quantity.getStyleClass().add("wrong-credentials");
            }
            errorMsg.setText("");
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
