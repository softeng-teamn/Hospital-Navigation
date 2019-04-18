package service;

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
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
        //set the style here
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
        primaryStage.show();
        return primaryStage;
    }


}
