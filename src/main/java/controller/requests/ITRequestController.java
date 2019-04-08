package controller.requests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.ITRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class ITRequestController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<ITRequest.ITRequestType> type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(ITRequest.ITRequestType.values()));
        type.getSelectionModel().select(1);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            ITRequest itRequest = new ITRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            itRequest.makeRequest();

            description.setText("");
            type.getSelectionModel().select(1);
        }
    }
}
