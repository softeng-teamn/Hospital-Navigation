package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.InterpreterRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class InterpreterController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<InterpreterRequest.Language> type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(InterpreterRequest.Language.values()));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            InterpreterRequest interpRequest = new InterpreterRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            interpRequest.makeRequest();
            description.setText("");
            type.getSelectionModel().clearSelection();
        }
    }

}
