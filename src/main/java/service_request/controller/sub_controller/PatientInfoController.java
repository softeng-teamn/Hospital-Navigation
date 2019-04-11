package service_request.controller.sub_controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
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
    void makeRequestHandler(ActionEvent event) {
        String firstName, lastName, birthDay, description;

        firstName = firstNameField.getText();
        lastName = lastNameField.getText();
        birthDay = birthYField.getText() + birthMField.getText() + birthDField.getText();
        description = descriptionArea.getText();

        if (selectedNode != null) {
            PatientInfoRequest newPatientInfoRequest = new PatientInfoRequest(-1, description, selectedNode, false, firstName, lastName, birthDay, description);
            newPatientInfoRequest.makeRequest();
            descriptionArea.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            birthDField.setText("");
            birthMField.setText("");
            birthYField.setText("");
        }
    }
}