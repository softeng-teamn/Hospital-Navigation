package employee.controller;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import database.DatabaseService;
import employee.model.Employee;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class EmployeeConfirmController {


    @FXML
    public void cancelDelete(javafx.event.ActionEvent actionEvent) {
        JFXButton btn = (JFXButton) actionEvent.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void deleteEmployee(javafx.event.ActionEvent actionEvent) {
        DatabaseService.getDatabaseService().deleteEmployee(ApplicationState.getApplicationState().getEmployeeToDelete());
        ApplicationState.getApplicationState().setEmployeeToDelete(null);
        JFXButton btn = (JFXButton) actionEvent.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }
}
