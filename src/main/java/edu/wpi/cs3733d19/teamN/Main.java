package edu.wpi.cs3733d19.teamN;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;

import java.io.IOException;
import java.util.HashMap;

import static edu.wpi.cs3733d19.teamN.application_state.ApplicationState.getApplicationState;

public class Main extends Application {

    /**
     *  Application Start
     * @param args arguments for main
     */
    public static void main(String[] args) {
        DatabaseService.getDatabaseService().loadFromCSVsIfNecessary();
        HashMap<String, ImageView> imageCache = new HashMap<>();
        try {
            imageCache.put("4", new ImageView(new Image(ResourceLoader.fourthFloor.openStream())));
            imageCache.put("3", new ImageView(new Image(ResourceLoader.thirdFloor.openStream())));
            imageCache.put("2", new ImageView(new Image(ResourceLoader.secondFloor.openStream())));
            imageCache.put("1", new ImageView(new Image(ResourceLoader.firstFloor.openStream())));
            imageCache.put("G", new ImageView(new Image(ResourceLoader.groundFloor.openStream())));
            imageCache.put("L1", new ImageView(new Image(ResourceLoader.firstLowerFloor.openStream())));
            imageCache.put("L2", new ImageView(new Image(ResourceLoader.secondLowerFloor.openStream())));
        } catch(IOException e) {
            e.printStackTrace();
        }
        getApplicationState().setImageCache(imageCache);
        launch();
    }

    /**
     * initializes the UI
     * @param primaryStage parameter to start the UI
     * @throws Exception if the FXML fails to load
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationState.getApplicationState().setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(ResourceLoader.idle);
        getApplicationState().getObservableBus().register("InactivityManager", ApplicationState.getApplicationState().getIM());
        //
        // Closes all threads
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMaximized(true);
        StageManager.changeWindow(primaryStage, root, "Idle");
    }

}
