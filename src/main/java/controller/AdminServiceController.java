package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

public class AdminServiceController extends Controller {
    @FXML
    private JFXButton editBtn;
    @FXML
    private JFXButton fulfillBtn;
    @FXML
    private JFXButton homeBtn;

    @FXML
    public void showadminHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    @FXML
    // switches window to map editor screen.
    public void showMapEditor() throws Exception {
        Stage stage = (Stage) editBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeExistingWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to map editor screen.
    public void showFulfillRequest() throws Exception {
        Stage stage = (Stage) fulfillBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Fulfill Service Request");
    }
}
