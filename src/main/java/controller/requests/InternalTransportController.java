package controller.requests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import model.request.InternalTransportRequest;
import service.DatabaseService;
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
            text_area.setText("");
            dropdown.getSelectionModel().clearSelection();
        }
    }
}
