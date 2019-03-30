package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.request.Request;
import service.ResourceLoader;
import service.StageManager;

import java.util.Collection;

public class RequestController extends Controller {

    @FXML
    private JFXButton homeBtn;

    private Collection<Request> requests;

    @FXML
    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    // adds a request object to local collection
    private void makeRequest() {

    }

    // removes object from local collection
    private void fufillRequest() {

    }

}
