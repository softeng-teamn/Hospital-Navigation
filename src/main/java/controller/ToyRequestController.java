package controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.ToyRequest;
import service.DatabaseService;
import java.net.URL;
import java.util.ResourceBundle;

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
            ToyRequest toyRequest = new ToyRequest(-1, description.getText(), selectedNode, false, toy);
            ToyRequest.makeRequest();
        }
    }
}