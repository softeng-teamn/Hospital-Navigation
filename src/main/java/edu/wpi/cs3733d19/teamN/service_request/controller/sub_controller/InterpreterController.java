package edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import edu.wpi.cs3733d19.teamN.service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.InterpreterRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class InterpreterController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<InterpreterRequest.Language> type;

    @FXML
    private Label errorMsg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(InterpreterRequest.Language.values()));
        type.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(description.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                InterpreterRequest interpRequest = new InterpreterRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
                interpRequest.makeRequest();
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
