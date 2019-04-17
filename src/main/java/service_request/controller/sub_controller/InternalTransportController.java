package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleNode;
import elevator.ElevatorConnection;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import service_request.controller.RequestController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import service_request.model.sub_model.InternalTransportRequest;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InternalTransportController extends RequestController{

    @FXML
    private JFXComboBox<InternalTransportRequest.TransportType> dropdown;
    @FXML
    private JFXTextArea text_area;
    @FXML
    private ToggleGroup urgency;
    @FXML
    private Label errorMsg;
    @FXML
    private JFXToggleNode low;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
         urgency.selectToggle(low);
        dropdown.setItems(FXCollections.observableArrayList(InternalTransportRequest.TransportType.values()));
        dropdown.getSelectionModel().select(0);
    }

    @FXML
    public void submitAction(javafx.event.ActionEvent actionEvent) {
        InternalTransportRequest.Urgency urgencyLevel = InternalTransportRequest.Urgency.NOT;

        JFXToggleNode selected = (JFXToggleNode) urgency.getSelectedToggle();

        if (selected != null) {
            switch (selected.getText()) {
                case "Low":
                    urgencyLevel = InternalTransportRequest.Urgency.NOT;
                    break;
                case "Medium":
                    urgencyLevel = InternalTransportRequest.Urgency.SOMEWHAT;
                    break;
                case "High":
                    urgencyLevel = InternalTransportRequest.Urgency.VERY;
                    break;
                default:
                    urgencyLevel = InternalTransportRequest.Urgency.NOT;
            }
        }


        if (selectedNode != null) {
            if(text_area.getText().equals("")){
                errorMsg.setText("Please Enter Details");
            }
            else {
                ElevatorConnection e = new ElevatorConnection("N");
                InternalTransportRequest request = new InternalTransportRequest(-1, text_area.getText(), RequestController.selectedNode, false, dropdown.getSelectionModel().getSelectedItem(), urgencyLevel);
                request.makeRequest();
                dropdown.getSelectionModel().select(0);
                text_area.setText("");
                errorMsg.setText("");
                if(request.getUrgency() == InternalTransportRequest.Urgency.VERY) {
                    try {
                        e.postFloor(request.getLocation().getFloor());
                    } catch (IOException e1) {
                        System.out.println("Error posting elev in int. trans. req");
                        e1.printStackTrace();
                    }
                }
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}
