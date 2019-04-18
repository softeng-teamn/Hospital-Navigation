package employee.controller;

import application_state.ApplicationState;
import application_state.Event;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseService;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import employee.Face;
import employee.MyCallback;
import employee.model.Employee;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
    @FXML
    private MaterialDesignIconView eye_icon;

    public static String usernameToFaceLogin = "nathan";
    private boolean showEye = false;
    private boolean isFaceScanning = false;

    Event event = new Event();
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
                event = ApplicationState.getApplicationState().getObservableBus().getEvent();
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());
                event.setEventName("login");
                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                showHome();
            } else {     // else user is an employee
                event.setLoggedIn(true);
                event.setAdmin(user.isAdmin());
                // set employee logged in with app state
                ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
                System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());

                event.setEventName("employee-login");    // Tell topNav not to show the gear/admin services button
                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                showHome();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        System.out.println("this is being run!!!!");
        initEyeButton();

    }

    void initEyeButton() {
        eye_icon.setCursor(Cursor.HAND);
        eye_icon.setOnMouseClicked((e) -> eyeIconClicked(e));
        usernameText.textProperty().addListener((observable, oldValue, newValue) -> usrnameListener(newValue));
        this.eye_icon.setVisible(false);
    }

    void eyeIconClicked(MouseEvent e) {
        eye_icon.setFill(Color.BLACK);
        if (isFaceScanning) {
            eye_icon.setGlyphName("EYE_OFF");
            isFaceScanning = false;
            turnOffCamera();
        } else {
            eye_icon.setGlyphName("EYE");
            isFaceScanning = true;
            turnOnCamera();
        }
    }

    void turnOnCamera() {
        String usrName = usernameText.getText();
        if (changeKnownImg(usrName)) {
            Face face = new Face(usrName, new MyCallback() {
                @Override
                public void callback(boolean b) {
                    if (b) {
                        // prompt the user of success
                        eye_icon.setFill(Color.rgb(98, 225, 125));
                        // log them in
                        Platform.runLater(() -> {
                            faceLogin(usrName);
                        });
                    } else {
                        eye_icon.setFill(Color.rgb(202, 56, 65));
                    }
                }
            });
            face.isMatch();
        } else {
            // something went wrong
            System.out.println("couldn't change the file");
            eye_icon.setFill(Color.rgb(202, 56, 65));
        }

    }

    boolean changeKnownImg(String username) {
        return username.equals(usernameToFaceLogin);
    }

    void faceLogin(String username) {
        Employee user = myDBS.getEmployeeByUsername(username);
        if (user.isAdmin()){
            event = ApplicationState.getApplicationState().getObservableBus().getEvent();
            event.setLoggedIn(true);
            event.setAdmin(user.isAdmin());
            // set employee logged in with app state
            ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
            event.setEventName("login");
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        } else {     // else user is an employee
            event.setLoggedIn(true);
            event.setAdmin(user.isAdmin());
            // set employee logged in with app state
            ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
            event.setEventName("employee-login");    // Tell topNav not to show the gear/admin services button
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
        }
        try {
            showHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void turnOffCamera() {
        System.out.println("Turn off camera");

    }

    void usrnameListener(String newValue) {
        if (newValue.equals("")) {
            eye_icon.setVisible(false);
            this.showEye = false;
            turnOffCamera();
        }
        if (!newValue.equals("") && !this.showEye) {
            this.showEye = true;
            eye_icon.setVisible(true);
        }
    }


    public void ifEnterLogin(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            try {
                login();
            } catch (Exception e) {
                System.out.println("Error logging in");
                e.printStackTrace();
            }
        }
    }

}
