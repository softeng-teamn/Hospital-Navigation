package service;

import application_state.ApplicationState;
import application_state.Observer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        primaryStage.setFullScreen(true);
        //set the style sheet here
        root.getStylesheets().removeAll();
        root.getStylesheets().add(ApplicationState.getApplicationState().getCurrentTheme().toString());
        primaryStage.show();
        return primaryStage;
    }


}
