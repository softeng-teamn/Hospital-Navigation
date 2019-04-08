package controller.requests;

import com.jfoenix.controls.*;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.ExternalTransportRequest;
import service.DatabaseService;

import java.net.URL;
import java.time.Year;
import java.util.*;

public class ExternalTransportationController  extends RequestController {
    @FXML
    private JFXComboBox<ExternalTransportRequest.TransportationType> transport;

    @FXML
    private JFXTextArea description;

    @FXML
    JFXComboBox<Integer> hour;

    @FXML
    JFXComboBox<Integer> minute;

    @FXML
    JFXComboBox<Integer> month;

    @FXML
    JFXComboBox<Integer> day;

    @FXML
    JFXComboBox<Integer> year;

    @FXML
    private JFXButton submit;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transport.setItems(FXCollections.observableArrayList(ExternalTransportRequest.TransportationType.values()));
        ArrayList<Integer> iList = new ArrayList<>();
        for(int i = 0; i < 60; i++){
            iList.add(i);
        }
        minute.setItems(FXCollections.observableArrayList(iList));

        iList.clear();
        for(int i = 1; i <= 24; i++){
            iList.add(i);
        }
        hour.setItems(FXCollections.observableArrayList(iList));

        iList.clear();
        for(int i = 1; i <= 31; i++){
            iList.add(i);
        }
        day.setItems(FXCollections.observableArrayList(iList));

        iList.clear();
        for(int i = 1; i<=12; i++){
            iList.add(i);
        }
        month.setItems(FXCollections.observableArrayList(iList));

        iList.clear();
        for(int i = 2019; i <= 2020; i++){
            iList.add(i);
        }

        year.setItems(FXCollections.observableArrayList(iList));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            GregorianCalendar g = new GregorianCalendar(year.getSelectionModel().getSelectedItem(),
                    month.getSelectionModel().getSelectedItem(), day.getSelectionModel().getSelectedItem(),
                    hour.getSelectionModel().getSelectedItem(), minute.getSelectionModel().getSelectedItem());
            Date d = new Date((g.getTimeInMillis()/1000));
            ExternalTransportRequest extTrans = new ExternalTransportRequest(-1, description.getText(), selectedNode, false, d, transport.getSelectionModel().getSelectedItem(), "");
            extTrans.makeRequest();
        }
    }
}
