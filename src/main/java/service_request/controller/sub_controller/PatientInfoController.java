package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import service_request.controller.RequestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service_request.model.sub_model.PatientInfoRequest;

public class PatientInfoController extends RequestController {

    @FXML
    private JFXTextField firstNameField, lastNameField, birthYField, birthMField, birthDField;

    @FXML
    private JFXTextArea descriptionArea;

    @FXML
    private Label errorMsg;

    @FXML
    void makeRequestHandler(ActionEvent event) {
        String firstName, lastName, birthDay, description;

        firstName = firstNameField.getText();
        lastName = lastNameField.getText();
        birthDay = birthYField.getText() + birthMField.getText() + birthDField.getText();
        description = descriptionArea.getText();

        if (selectedNode != null) {
            if(descriptionArea.getText().equals("")){
                errorMsg.setText("Please enter a Description");
            }
            else if(firstNameField.getText().equals("")){
                errorMsg.setText("Please enter a First Name");
            }
            else if(lastNameField.getText().equals("")){
                errorMsg.setText("Please enter a Last Name");
            }
            else if(birthDField.getText().equals("") || birthMField.getText().equals("") || birthYField.getText().equals("")){
                errorMsg.setText("Please enter a Birthday");
            }
            else {
                PatientInfoRequest newPatientInfoRequest = new PatientInfoRequest(-1, description, selectedNode, false, firstName, lastName, birthDay, description);
                newPatientInfoRequest.makeRequest();
                descriptionArea.setText("");
                firstNameField.setText("");
                lastNameField.setText("");
                birthDField.setText("");
                birthMField.setText("");
                birthYField.setText("");
                errorMsg.setText("");
            }
        }
        else{
            errorMsg.setText("Please Select a Location");
        }
    }
}