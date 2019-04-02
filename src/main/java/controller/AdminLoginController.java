package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Employee;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.EmptyStackException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class AdminLoginController extends Controller implements Initializable {

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



//    @FXML
//    public void showDecision() throws Exception {
//        Stage stage = (Stage) loginBtn.getScene().getWindow();
//        Parent root = FXMLLoader.load(ResourceLoader.adminPage);
//        StageManager.changeExistingWindow(stage, root, "Admin Page");
//    }



    @FXML
    public void login() throws Exception{
        String id = idText.getText();
        String password = passwordField.getText();
        int intID = Integer.parseInt(id);
//        Employee user = dbs.getEmployee(intID);

//        if(user.isAdmin() && user != null){
        if (intID == 1234){
            if(password.equals("kimi")){
                isAdmin = true;
                showHome();
            } else {
                passwordPrompt.setText("Invalid Password");
            }
        } else {
            idPrompt.setText("Invalid ID");
        }



    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        idText.setTextFormatter(textFormatter);
    }
}
