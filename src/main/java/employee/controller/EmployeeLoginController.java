package employee.controller;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.Controller;
import employee.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import application_state.Event;
import application_state.EventBusFactory;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeLoginController extends Controller implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXTextField usernameText;
    @FXML
    private JFXPasswordField passwordField;

    Event event = new Event();
    private EventBus eventBus = EventBusFactory.getEventBus();
    static DatabaseService myDBS = DatabaseService.getDatabaseService();


    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }



    @FXML
    public void login() throws Exception{
        String username = usernameText.getText();
        String password = passwordField.getText();
        Employee user = myDBS.getEmployeeByUsername(username);

        if (user == null) {
            // Invalid username
            passwordField.getStyleClass().add("wrong-credentials");
        } else {
            if (!password.equals(user.getPassword())) {
                // Invalid password
                passwordField.getStyleClass().add("wrong-credentials");
            } else if (user.isAdmin()){
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                Controller.setCurrentJob(user.getJob());
                event.setEventName("login");
                eventBus.post(event);
                showHome();
            } else {
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin() == false);
                Controller.setCurrentJob(user.getJob());
                event.setEventName("empLogin");
                eventBus.post(event);
                showHome();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventBus.register(this);
    }
}
