package edu.wpi.cs3733d19.teamN.application_state;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;

import java.util.Timer;
import java.util.TimerTask;

public class InactivityManager implements Observer {

    private Event event;

    private int inactivityLimit;
    private Timer timer = new Timer();

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
