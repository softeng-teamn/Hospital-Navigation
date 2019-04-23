package employee.controller;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

import static javafx.fxml.FXMLLoader.load;

public class EnterApplicationController{

    //private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    StackPane sp = new StackPane();

    @FXML
    public void initialize() throws Exception{
        sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                try {
                    enter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sp.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

            }
        });
    }

    public void enter() throws Exception {
        Stage stage = (Stage) sp.getScene().getWindow();
        Parent root = load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home");
    }

    public void gameStart() throws Exception{

    }

}
