package application_state;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import static application_state.ApplicationState.getApplicationState;

public class InactivityManager implements MouseMotionListener {

    private Event event;

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
        public void run() {
            System.out.println("Timer Task Running");
            event = ApplicationState.getApplicationState().getObservableBus().getEvent();
            event.setAdmin(false);
            event.setLoggedIn(false);
            event.setEventName("logout");
            ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
            ApplicationState.getApplicationState().setEmployeeLoggedIn(null);
        }
    };

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("mouse dragged");
        timer.cancel();
        timer.schedule(task, inactivityLimit);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("mouse moved");
        timer.cancel();
        timer.schedule(task, inactivityLimit);
    }
}
