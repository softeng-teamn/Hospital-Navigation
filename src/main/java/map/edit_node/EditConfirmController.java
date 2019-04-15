package map.edit_node;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import map.Edge;
import map.Node;
import database.DatabaseService;

import java.util.ArrayList;

public class EditConfirmController {

    @FXML
    void deleteConfirmAction(ActionEvent e) throws Exception {
        // Delete the edges
        for (Edge edge : ApplicationState.getApplicationState().getEdgesToEdit()) {
            DatabaseService.getDatabaseService().deleteEdge(edge);
        }
        // DELETE THE NODE
        DatabaseService.getDatabaseService().deleteNode(ApplicationState.getApplicationState().getNodeToEdit());
        // Set to null
        ApplicationState.getApplicationState().setNodeToEdit(null);
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
