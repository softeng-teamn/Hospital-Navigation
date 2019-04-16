package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import service_request.model.sub_model.SanitationRequest;
import database.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class SanitationRequestController extends RequestController {
    @FXML
    private JFXTextArea notes;

    @FXML
    private JFXComboBox<String> urgencyBox, materialBox;

    @FXML
    private JFXButton submitBtn;

    @FXML
    private Label errorMsg;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set urgency options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Low",
                        "Medium",
                        "High"
                );
        urgencyBox.setItems(options);

        // Set state options
        options =
                FXCollections.observableArrayList(
                        "Liquid",
                        "Solid",
                        "Mixture",
                        "Other"
                );
        materialBox.setItems(options);
        urgencyBox.getSelectionModel().select(0);
        materialBox.getSelectionModel().select(0);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        System.out.println("clicked");
        if(selectedNode != null) {
            if(notes.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                System.out.println("making service_request");
                SanitationRequest sanitationRequest = new SanitationRequest(-1, notes.getText(), selectedNode, false, urgencyBox.getValue(), materialBox.getValue());
                sanitationRequest.makeRequest();
                notes.setText("");
                urgencyBox.getSelectionModel().select(0);
                materialBox.getSelectionModel().select(0);
                errorMsg.setText("");
            }
        }
        else {
            errorMsg.setText("Please Select a Location");
        }
    }

}
