import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

public class Main extends Application {

    // Application Start
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeWindow(primaryStage, root, "Home");
    }

}
