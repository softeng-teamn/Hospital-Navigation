package employee.controller;

import application_state.ApplicationState;
import application_state.Event;
import application_state.EventBusFactory;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.Controller;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

import static application_state.ApplicationState.getApplicationState;

public class EmployeeLoginController extends Controller implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXTextField usernameText;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private MaterialDesignIconView eye_icon;

    private boolean showEye = false;
    private boolean isFaceScanning = false;

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
                getApplicationState().setEmployeeID(user.getID());
                Controller.setCurrentJob(user.getJob());
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
                Controller.setCurrentJob(user.getJob());
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
//        System.out.println("this is being run!!!!");
        initEyeButton();
        eventBus.register(this);

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
    }

    void faceLogin(String username) {
        Employee user = myDBS.getEmployeeByUsername(username);
        event.setAdmin(user.isAdmin());
        getApplicationState().setEmployeeID(user.getID());
        Controller.setCurrentJob(user.getJob());
        // set employee logged in with app state
        ApplicationState.getApplicationState().setEmployeeLoggedIn(user);
        event.setEventName("login");
        eventBus.post(event);
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
        if(newValue.equals("")) {
            eye_icon.setVisible(false);
            this.showEye = false;
            turnOffCamera();
        }
        if (!newValue.equals("") && !this.showEye) {
            this.showEye = true;
            eye_icon.setVisible(true);
        }
    }
}
