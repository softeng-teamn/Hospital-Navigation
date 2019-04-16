package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Label;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.ITRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class ITRequestController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<ITRequest.ITRequestType> type;

    @FXML
    private Label errorMsg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(ITRequest.ITRequestType.values()));
        type.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(description.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                ITRequest itRequest = new ITRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
                itRequest.makeRequest();

                description.setText("");
                type.getSelectionModel().select(0);
                errorMsg.setText("");
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
