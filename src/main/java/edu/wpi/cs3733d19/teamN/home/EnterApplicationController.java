package edu.wpi.cs3733d19.teamN.home;

import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.fxml.FXMLLoader.load;

public class EnterApplicationController{

    //private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    StackPane sp = new StackPane();

    @FXML
    private MaterialIconView tapToStart;

    @FXML
    public void initialize() throws Exception{
        addAnimation(tapToStart);
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

    private void addAnimation(javafx.scene.Node object) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(object.scaleXProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(object.scaleYProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(object.opacityProperty(), .6, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(object.scaleXProperty(), 1.1, Interpolator.EASE_BOTH),
                        new KeyValue(object.scaleYProperty(), 1.1, Interpolator.EASE_BOTH),
                        new KeyValue(object.opacityProperty(), .8, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(object.scaleXProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(object.scaleYProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(object.opacityProperty(), .6, Interpolator.EASE_BOTH)
                )
        );
        timeline.setRate(1);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}