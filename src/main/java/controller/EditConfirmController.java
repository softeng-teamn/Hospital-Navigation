package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Edge;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

public class EditConfirmController extends Controller {

    @FXML
    void deleteConfirmAction(ActionEvent e) throws Exception {
        // Delete the edges
        for (Edge edge : edgesToEdit) {
            DatabaseService.getDatabaseService().deleteEdge(edge);
        }
        // DELETE THE NODE
        DatabaseService.getDatabaseService().deleteNode(nodeToEdit);
        // Close the window
        nodeToEdit = null;
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
