package application_state;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import static application_state.ApplicationState.getApplicationState;

public class InactivityManager extends AnimationTimer implements MouseMotionListener {

    int inactivityLimit;
    int countDown;

    public InactivityManager(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
        countDown = inactivityLimit;
    }

    public int getInactivityLimit() {
        return inactivityLimit;
    }

    public void setInactivityLimit(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
    }

    public void timeOut(){
        getApplicationState().setInactive(true);
        Stage stage = getApplicationState().getPrimaryStage();
        try {
            Parent root = FXMLLoader.load(ResourceLoader.home);
            StageManager.changeExistingWindow(stage, root, "Home");
        }
        catch(Exception exception){
            System.out.println("big sad");
        }
    }

    public void startTheTimer() {
        start();
    }

    public void stopTheTimer(){
        stop();
    }

    @Override
    public void handle(long now){
        countDown--;
        if(countDown < 1){
            stopTheTimer();
            timeOut();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        countDown = inactivityLimit;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        countDown = inactivityLimit;
    }
}
