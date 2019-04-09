package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import model.Edge;
import service.DatabaseService;

public class EditConfirmController extends Controller {

    @FXML
    void deleteConfirmAction(ActionEvent e) {
        // DELETE THE NODE
        DatabaseService.getDatabaseService().deleteNode(nodeToEdit);
        // Close the window
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelConfirmAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    @FXML
    void saveConfirmAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

}
