import database.DatabaseService;
import face_detect.FaceDetectController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;
import service.ResourceLoader;

public class Main extends Application {

    /**
     *  Application Start
     * @param args
     */
    public static void main(String[] args) {

        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
        FXMLLoader loader = new FXMLLoader(ResourceLoader.faceDetect);

        BorderPane rootElement = (BorderPane) loader.load();
        //
        // Closes all threads
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
//        primaryStage.setMaximized(true);
//        StageManager.changeWindow(primaryStage, root, "Home");
        // set the proper behavior on closing the application


        Scene scene = new Scene(rootElement, 800, 600);
        primaryStage.setTitle("Smile");
        primaryStage.setScene(scene);
        primaryStage.show();

        FaceDetectController controller = loader.getController();
        primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we)
            {
                controller.setClosed();
            }
        }));
    }

}
