package service;

import application_state.InactivityManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static application_state.ApplicationState.getApplicationState;
import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;

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
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
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
        getApplicationState().getIM().startTheTimer();
        primaryStage.setTitle(title);
        addMouseMotionListener(getApplicationState().getIM());
        primaryStage.getScene().setRoot(root);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        return primaryStage;
    }


}
