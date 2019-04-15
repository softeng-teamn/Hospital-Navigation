package service_request.controller.sub_controller;

import com.jfoenix.controls.*;
import javafx.scene.control.Label;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.ExternalTransportRequest;
import database.DatabaseService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @FXML
    private Label errorMsg;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transport.setItems(FXCollections.observableArrayList(ExternalTransportRequest.TransportationType.values()));
        datePicker.setValue(LocalDate.now());
        timePicker.setValue(LocalTime.now());
        transport.getSelectionModel().select(2);
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if(description.getText().equals("")){
                errorMsg.setText("Please Enter a Description");
            }
            else{
            GregorianCalendar g = new GregorianCalendar(datePicker.getValue().getYear(),
                    datePicker.getValue().getMonthValue(), datePicker.getValue().getDayOfYear(),
                    timePicker.getValue().getHour(), timePicker.getValue().getMinute());
            Date d = new Date((g.getTimeInMillis()/1000));
            ExternalTransportRequest extTrans = new ExternalTransportRequest(-1, description.getText(), selectedNode, false, d, transport.getSelectionModel().getSelectedItem(), "");
            extTrans.makeRequest();
            transport.getSelectionModel().select(2);
            description.setText("");
            datePicker.setValue(LocalDate.now());
            timePicker.setValue(LocalTime.now());
            errorMsg.setText("");
        }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
