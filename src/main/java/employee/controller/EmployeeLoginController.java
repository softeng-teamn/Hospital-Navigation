package employee.controller;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import employee.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import application_state.Event;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * controller for the login FXML
 */
public class EmployeeLoginController implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXTextField usernameText;
    @FXML
    private JFXPasswordField passwordField;

    Event event = new Event();
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home,event.getCurrentBundle());
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
                event = ApplicationState.getApplicationState().getObservableBus().getEvent();
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());
                event.setEventName("login");
                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                showHome();
                // else user is an employee
            } else {
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());

                event.setEventName("employee-login");
                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                showHome();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // todo?
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
