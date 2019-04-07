package controller.requests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import model.request.InternalTransportRequest;
import model.request.InternalTransportRequest.TransportType;
import model.request.Request;
import service.DatabaseService;

import java.awt.event.ActionEvent;

public class InternalTransportController {


    private TransportType transportType = TransportType.Wheelchair;

    @FXML
    private JFXComboBox<String> dropdown;
    @FXML
    private JFXTextArea text_area;

    @FXML
    void initialize() {
        System.out.println("is this running??");
        ObservableList<String> transportTypes = FXCollections.observableArrayList();
        transportTypes.add(InternalTransportRequest.TransportType.Wheelchair.toString());
        transportTypes.add(InternalTransportRequest.TransportType.MotorScooter.toString());
        transportTypes.add(InternalTransportRequest.TransportType.Stretcher.toString());
        dropdown.setItems(transportTypes);
        dropdown.setOnAction(e -> {
            String selection = dropdown.getValue().toString();
            System.out.println("You selected: " + selection);
            switch (selection) {
                case "Wheelchair":
                    transportType = TransportType.Wheelchair;
                    break;
                case "MotorScooter":
                    transportType = TransportType.MotorScooter;
                    break;
                case "Stretcher":
                    transportType = TransportType.Stretcher;
                    break;
                default:
                    break;
            }
        });
    }

    @FXML
    public void submitAction(javafx.event.ActionEvent actionEvent) {
        if (RequestController.selectedNode != null) {
            InternalTransportRequest request = new InternalTransportRequest(-1, text_area.getText(), RequestController.selectedNode, false, transportType);
            request.makeRequest();
            System.out.println("The request was sent");
            System.out.println("Lets see if I can get it back");
            System.out.println(DatabaseService.getDatabaseService().getAllInternalTransportRequest());
        }
    }
}
