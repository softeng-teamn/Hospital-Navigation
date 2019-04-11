package service_request.controller.sub_controller;

import com.jfoenix.controls.*;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.ExternalTransportRequest;
import database.DatabaseService;

import java.net.URL;
import java.util.*;

public class ExternalTransportationController  extends RequestController {
    @FXML
    private JFXComboBox<ExternalTransportRequest.TransportationType> transport;

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXTimePicker timePicker;

    @FXML
    private JFXButton submit;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transport.setItems(FXCollections.observableArrayList(ExternalTransportRequest.TransportationType.values()));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            GregorianCalendar g = new GregorianCalendar(datePicker.getValue().getYear(),
                    datePicker.getValue().getMonthValue(), datePicker.getValue().getDayOfYear(),
                    timePicker.getValue().getHour(), timePicker.getValue().getMinute());
            Date d = new Date((g.getTimeInMillis()/1000));
            ExternalTransportRequest extTrans = new ExternalTransportRequest(-1, description.getText(), selectedNode, false, d, transport.getSelectionModel().getSelectedItem(), "");
            extTrans.makeRequest();
        }
    }
}
