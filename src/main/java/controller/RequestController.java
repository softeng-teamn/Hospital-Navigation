package controller;

import model.request.Request;

import java.util.Collection;

public class RequestController extends Controller {

    @FXML
    private JFXButton homeBtn;

    private Collection<Request> requests;

    @FXML
    // switches window to home screen
    private void showHome() {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeWindow(stage, root, "Home (Path Finder)");
    }

    // adds a request object to local collection
    private void makeRequest() {

    }

    // removes object from local collection
    private void fufillRequest() {

    }

}
