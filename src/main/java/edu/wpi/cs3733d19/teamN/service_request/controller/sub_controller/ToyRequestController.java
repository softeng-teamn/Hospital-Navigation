package edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import edu.wpi.cs3733d19.teamN.service_request.controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.ToyRequest;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;

public class ToyRequestController extends RequestController {
    @FXML
    private JFXTextField toy;
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXButton submit;

    @FXML
    private Label errorMsg;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            if (description.getText().equals("")) {
                errorMsg.setText("Please Enter Details");
            } else {
                if (toy.getText().equals("")) {
                    errorMsg.setText("Please Describe the Toy");
                } else {
                    ToyRequest toyRequest = new ToyRequest(-1, description.getText(), selectedNode, false, toy.getText());
                    toyRequest.makeRequest();
                    description.setText("");
                    toy.setText("");
                    errorMsg.setTextFill(Color.BLACK);
                    errorMsg.setText("Request Submitted!");
                }
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}