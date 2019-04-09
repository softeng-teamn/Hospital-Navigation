import javafx.application.Platform;
import service.CSVService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;
import java.lang.SuppressWarnings;

public class Main extends Application {

    /**
     *  Application Start
     * @param args
     */
    public static void main(String[] args) {
        DatabaseService.getDatabaseService().loadFromCSVsIfNecessary();
        launch();
    }

    /**
     * initializes the UI
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
         Parent root = FXMLLoader.load(ResourceLoader.home);
        // Closes all threads
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMaximized(true);
        StageManager.changeWindow(primaryStage, root, "Home");
    }

}
