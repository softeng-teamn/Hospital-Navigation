package edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class HelpController {

    @FXML
    private JFXButton closeButton;

    @FXML
    void helpConfirmAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

}
