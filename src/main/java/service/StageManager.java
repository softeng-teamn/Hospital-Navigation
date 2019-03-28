package service;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageManager {

    // change stage window
    public static Stage changeWindow(Stage primaryStage, Parent root, String title) throws Exception {
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        return primaryStage;
    }

}
