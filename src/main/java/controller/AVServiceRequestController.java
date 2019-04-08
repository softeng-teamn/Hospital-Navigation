package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.AVServiceRequest;
import service.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class AVServiceRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<AVServiceRequest.AVServiceType> type;

    @FXML
    private JFXButton submit;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(AVServiceRequest.AVServiceType.values()));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            AVServiceRequest avServiceRequest = new AVServiceRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            avServiceRequest.makeRequest();
        }
    }

}

