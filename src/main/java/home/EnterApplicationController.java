package home;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;

import static javafx.fxml.FXMLLoader.load;

public class EnterApplicationController {

    //private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    StackPane sp = new StackPane();

    @FXML
    ImageView imageView = new ImageView();

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
        //Stage stage = (Stage) sp.getScene().getWindow();

    }

    //USELESS IGNORE (but may come in handy idk, probably won't)
    /*public void centerImage() {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            System.out.println("RatioY is: " + ratioY);
            System.out.println("RatioX is: " + ratioX);

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }*/

}
