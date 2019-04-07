package controller.requests;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.PatientInfoRequest;

public class PatientInfo {

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

        PatientInfoRequest newPatientInfoRequest = new PatientInfoRequest(-1, null, null, false, firstName, lastName, birthDay, description);
        newPatientInfoRequest.makeRequest();
    }



}
