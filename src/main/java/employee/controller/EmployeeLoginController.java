package employee.controller;

import application_state.ApplicationState;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import employee.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import application_state.Event;
import application_state.EventBusFactory;
import database.DatabaseService;
import map.Edge;
import service.ResourceLoader;
import service.StageManager;
import application_state.ApplicationState;
import java.net.URL;
import java.util.ResourceBundle;
import static application_state.ApplicationState.getApplicationState;

public class EmployeeLoginController implements Initializable {

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
                // if user has admin credentials
            } else if (user.isAdmin()){
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());
                event.setEventName("login");
                eventBus.post(event);
                showHome();
                // else user is an employee
            } else {
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin() == false);
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());

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


    public void ifEnterLogin(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            try {
                login();
            } catch (Exception e) {
                System.out.println("Error logging in");
                e.printStackTrace();
            }
        }
    }
}
