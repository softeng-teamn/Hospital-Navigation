package edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller;

import com.jfoenix.controls.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import edu.wpi.cs3733d19.teamN.service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.GiftStoreRequest;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import java.net.URL;
import java.util.ResourceBundle;



public class GiftStoreRequestController extends RequestController {
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXComboBox <GiftStoreRequest.GiftType> type;

    @FXML
    private JFXButton submit;

    @FXML
    private Label errorMsg;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    private JFXTextField patientName;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<GiftStoreRequest.GiftType> options = FXCollections.observableArrayList(GiftStoreRequest.GiftType.BALLOONS, GiftStoreRequest.GiftType.TEDDY_BEAR, GiftStoreRequest.GiftType.GIFT_BASKET) ;
        type.getItems().addAll(options);
        type.getSelectionModel().select(0);
    }


    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(description.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else if(patientName.getText().equals("")){
                errorMsg.setText("Please Enter a Patient Name");
            }
            else {
                GiftStoreRequest giftStoreRequest = new GiftStoreRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem(), patientName.getText());
                giftStoreRequest.makeRequest();
                description.setText("");
                type.getSelectionModel().select(0);
                patientName.setText("");
                errorMsg.setTextFill(Color.BLACK);
                errorMsg.setText("Request Submitted!");
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
