package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

public class AdministratorController extends Controller {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton loginBtn;
    @FXML
    private JFXTextField idText;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Text idPrompt;
    @FXML
    private Text passwordPrompt;



    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    @FXML
    public void showDecision() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    public void login() {
        String id = idText.getText();
        String password = passwordField.getText();


        idPrompt.setText(id);
        passwordPrompt.setText(password);

    }

}
