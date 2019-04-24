package edu.wpi.cs3733d19.teamN.map.edit_node;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.map.Edge;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;

/**
 * controls the editor confirmation FXML
 */
public class EditConfirmController {

    /** Handles node deletions
     * @param e the event that activated this in the FXML
     * @throws Exception
     */
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

    /** handles cancellations in the node editor
     * @param e the event that activated this in the FXML
     */
    @FXML
    void cancelConfirmAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    /** handles saves in the node editor
     * @param e the event that activated this in the FXML
     */
    @FXML
    void saveConfirmAction(ActionEvent e) {
        JFXButton btn = (JFXButton) e.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

}
