package controller.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleNode;
import controller.RequestController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import model.request.AVServiceRequest;
import service.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class AVServiceRequestController extends RequestController {

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXComboBox<AVServiceRequest.AVServiceType> type;

    @FXML
    private ToggleGroup AVType;

    @FXML
    void submitRequest(ActionEvent event) {

        AVServiceRequest.AVServiceType AVTypeSelected =  AVServiceRequest.AVServiceType.Audio;

        JFXToggleNode selected = (JFXToggleNode) AVType.getSelectedToggle();

        if (selected != null) {
            switch (selected.getText()) {
                case "Audio":
                    AVTypeSelected = AVServiceRequest.AVServiceType.Audio;
                    break;
                case "Visual":
                    AVTypeSelected = AVServiceRequest.AVServiceType.Visual;
                    break;
                case "Other":
                    AVTypeSelected = AVServiceRequest.AVServiceType.Other;
                    break;
                default:
                    AVTypeSelected = AVServiceRequest.AVServiceType.Audio;
            }
        }

        if(selectedNode != null) {
            AVServiceRequest avServiceRequest = new AVServiceRequest(-1, description.getText(), selectedNode, false, AVTypeSelected);
            avServiceRequest.makeRequest();
            description.setText("");
            AVType.getSelectedToggle().setSelected(false);
        }
    }

}

