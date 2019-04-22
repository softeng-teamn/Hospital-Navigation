package application_state;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import static application_state.ApplicationState.getApplicationState;

public class InactivityManager implements MouseMotionListener {

    int inactivityLimit;
    Timer timer = new Timer();

    public InactivityManager(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
        timer.schedule(task, inactivityLimit);
    }

    public int getInactivityLimit() {
        return inactivityLimit;
    }

    public void setInactivityLimit(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
    }

    TimerTask task = new TimerTask() {
        public void run(){
            getApplicationState().setInactive(true);
            Stage stage = getApplicationState().getPrimaryStage();
            try {
                Parent root = FXMLLoader.load(ResourceLoader.idle);
                StageManager.changeExistingWindow(stage, root, "idle");
            }
            catch(Exception exception){
                System.out.println("big sad");
            }
        }
    };

    @Override
    public void mouseDragged(MouseEvent e) {
        timer.cancel();
        timer.schedule(task, 5000l);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        timer.cancel();
        timer.schedule(task, 5000l);
    }
}
