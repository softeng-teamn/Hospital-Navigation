package service;

import application_state.ApplicationState;
import application_state.Observer;
import application_state.Event;
import application_state.InactivityManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static application_state.ApplicationState.getApplicationState;

/**
 * controls scene switching
 */
public class StageManager {

    /**
     * change stage window
     * @param primaryStage Stage to change to
     * @param root given root
     * @param title window title to set
     * @return Stage switched to
     * @throws Exception if the FXML fails to load
     */
    public static Stage changeWindow(Stage primaryStage, Parent root, String title) throws Exception {
        primaryStage.setTitle(title);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getScene().setOnMouseMoved(e -> {
            Event ev = ApplicationState.getApplicationState().getObservableBus().getEvent();
            ev.setEventName("reset-timer");
            ApplicationState.getApplicationState().getObservableBus().updateEvent(ev);
        });
        primaryStage.getScene().setOnKeyPressed(e-> {
            Event ev = ApplicationState.getApplicationState().getObservableBus().getEvent();
            ev.setEventName("reset-timer");
            ApplicationState.getApplicationState().getObservableBus().updateEvent(ev);
        });
        System.out.println("1st time Scene: " + primaryStage.getScene());
        primaryStage.setFullScreen(true);
        //set the style here
        scene.getStylesheets().clear();
        //setUserAgentStylesheets(null);
        scene.getStylesheets().add(ApplicationState.getApplicationState().getCurrentTheme().toString());
        primaryStage.setScene(scene);
        primaryStage.show();
        return primaryStage;
    }

    /**
     * change stage window
     * testing out faster load
     * @param primaryStage Stage to change to
     * @param root given root
     * @param title window title to set
     * @return Stage switched to
     * @throws Exception if the FXML fails to load
     */
    public static Stage changeExistingWindow(Stage primaryStage, Parent root, String title) throws Exception {
        primaryStage.setTitle(title);
        primaryStage.getScene().setRoot(root);
        //set the style sheet here
        root.getStylesheets().removeAll();
        root.getStylesheets().add(ApplicationState.getApplicationState().getCurrentTheme().toString());
        primaryStage.setFullScreen(true);
        primaryStage.show();
        return primaryStage;
    }

}
