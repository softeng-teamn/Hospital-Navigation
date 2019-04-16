package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.FloristRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class FloristController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXTextField bouquetType;

    @FXML
    private JFXComboBox<Integer> quantity;

    @FXML
    private Label errorMsg;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quantity.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        quantity.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if (selectedNode != null) {
            if (description.getText().equals("")) {
                errorMsg.setText("Please Enter a Description");
            } else if (bouquetType.getText().equals("")) {
                errorMsg.setText("Please Enter a Bouquet Type");
            } else {
                FloristRequest floristRequest = new FloristRequest(-1, description.getText(), selectedNode, false, bouquetType.getText(), quantity.getSelectionModel().getSelectedItem());
                floristRequest.makeRequest();
                description.setText("");
                bouquetType.setText("");
                quantity.getSelectionModel().select(0);
                errorMsg.setText("");
            }
        } else {
            errorMsg.setText("Please Select a Location");
        }
    }

}
