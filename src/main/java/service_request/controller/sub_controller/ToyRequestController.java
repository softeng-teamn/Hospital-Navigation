package service_request.controller.sub_controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import service_request.controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.ToyRequest;
import database.DatabaseService;

public class ToyRequestController extends RequestController {
    @FXML
    private JFXTextField toy;
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXButton submit;
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            ToyRequest toyRequest = new ToyRequest(-1, description.getText(), selectedNode, false, toy.getText());
            toyRequest.makeRequest();
        }
    }
}