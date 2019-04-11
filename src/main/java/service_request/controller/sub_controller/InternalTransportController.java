package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import service_request.model.sub_model.InternalTransportRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class InternalTransportController extends RequestController{

    @FXML
    private JFXComboBox<InternalTransportRequest.TransportType> dropdown;
    @FXML
    private JFXTextArea text_area;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdown.setItems(FXCollections.observableArrayList(InternalTransportRequest.TransportType.values()));
    }

    @FXML
    public void submitAction(javafx.event.ActionEvent actionEvent) {
        if (selectedNode != null) {
            InternalTransportRequest request = new InternalTransportRequest(-1, text_area.getText(), RequestController.selectedNode, false, dropdown.getSelectionModel().getSelectedItem());
            request.makeRequest();
        }
    }
}
