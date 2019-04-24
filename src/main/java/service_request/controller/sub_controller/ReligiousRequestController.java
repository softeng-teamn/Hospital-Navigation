package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.ReligiousRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class ReligiousRequestController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<ReligiousRequest.Religion> type;

    @FXML
    private Label errorMsg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(ReligiousRequest.Religion.values()));
        type.getSelectionModel().select(4);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(description.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                ReligiousRequest religiousRequest = new ReligiousRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
                religiousRequest.makeRequest();
                description.setText("");
                type.getSelectionModel().select(4);
                errorMsg.setTextFill(Color.BLACK);
                errorMsg.setText("Request Submitted!");
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
