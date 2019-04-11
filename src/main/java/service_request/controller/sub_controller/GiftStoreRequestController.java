package service_request.controller.sub_controller;

import com.jfoenix.controls.*;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.GiftStoreRequest;
import database.DatabaseService;
import java.net.URL;
import java.util.ResourceBundle;



public class GiftStoreRequestController extends RequestController {
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXComboBox <GiftStoreRequest.GiftType> type;

    @FXML
    private JFXButton submit;
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    private JFXTextField patientName;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<GiftStoreRequest.GiftType> options = FXCollections.observableArrayList(GiftStoreRequest.GiftType.BALLOONS, GiftStoreRequest.GiftType.TEDDY_BEAR, GiftStoreRequest.GiftType.GIFT_BASKET) ;
        type.getItems().addAll(options);
    }


    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            GiftStoreRequest giftStoreRequest = new GiftStoreRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem(), patientName.getText());
            giftStoreRequest.makeRequest();
        }
    }
}
