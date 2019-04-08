package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.request.InterpreterRequest;
import service.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class InterpreterController extends RequestController {
    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<InterpreterRequest.Language> type;

    @FXML
    private JFXButton submit;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        type.setItems(FXCollections.observableArrayList(InterpreterRequest.Language.values()));
    }

    @FXML
    void submitRequest(ActionEvent event) {
        if(selectedNode != null) {
            InterpreterRequest interpRequest = new InterpreterRequest(-1, description.getText(), selectedNode, false, type.getSelectionModel().getSelectedItem());
            interpRequest.makeRequest();
        }
    }

}
