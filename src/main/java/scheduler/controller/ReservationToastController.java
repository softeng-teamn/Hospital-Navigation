package scheduler.controller;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import javax.swing.*;

public class ReservationToastController {

    @FXML
    public void cancelDelete(javafx.event.ActionEvent actionEvent) {
        JFXButton btn = (JFXButton) actionEvent.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void confirmReservation(ActionEvent e){
        ApplicationState.getApplicationState().setConfirmReservation(true);
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }
}
