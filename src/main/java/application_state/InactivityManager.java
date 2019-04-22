package application_state;

import com.google.zxing.WriterException;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import static application_state.ApplicationState.getApplicationState;

public class InactivityManager implements Observer {

    private Event event;

    int inactivityLimit;
    Timer timer = new Timer();

    public InactivityManager(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
        task = new TimerTask() {
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
        timer.schedule(task, inactivityLimit);
    }

    public int getInactivityLimit() {
        return inactivityLimit;
    }

    public void setInactivityLimit(int inactivityLimit) {
        this.inactivityLimit = inactivityLimit;
    }

    TimerTask task;

    /** recieve a notification from the observed object
     * @param o event recieved
     */
    @Override
    public void notify(Object o) {
        event = (Event) o;
        switch (event.getEventName()) {
            case "reset-timer":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        task.cancel();
                        timer.cancel();
                        timer = new Timer();
                        task = new TimerTask() {
                            public void run() {
                                System.out.println("Timer Task Running");
                                event = ApplicationState.getApplicationState().getObservableBus().getEvent();
                                event.setAdmin(false);
                                event.setLoggedIn(false);
                                event.setEventName("logout");
                                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                                ApplicationState.getApplicationState().setEmployeeLoggedIn(null);
                                event = ApplicationState.getApplicationState().getObservableBus().getEvent();
                                event.setEventName("idle");
                                ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
                            }
                        };
                        timer.schedule(task, inactivityLimit);
                    }
                });
                break;
            case "idle":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        goToIdleScreen();
                    }
                });
                break;
            default:
                break;
        }

    }

    private void goToIdleScreen() {
        Stage stage = ApplicationState.getApplicationState().getPrimaryStage();
        try {
            Parent root = FXMLLoader.load(ResourceLoader.idle);
            StageManager.changeExistingWindow(stage, root, "Kiosk Application");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
