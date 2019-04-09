package controller.requests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.ReligiousRequest;

import java.net.URL;
import java.util.ResourceBundle;

import static controller.RequestController.selectedNode;

public class ReligiousRequestController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<ReligiousRequest.Religion> type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(ReligiousRequest.Religion.values()));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            ReligiousRequest religiousRequest = new ReligiousRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            religiousRequest.makeRequest();
        }
    }
}
