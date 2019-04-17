import database.DatabaseService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.opencv.core.Core;
import service.ResourceLoader;
import service.StageManager;
import web_server.WebServer;

public class Main extends Application {

    static WebServer webServer;

    /**
     *  Application Start
     * @param args
     */
    public static void main(String[] args) {

        WebServer.run();
//        new Thread(() -> {
//            webServer = new WebServer();
//        }).start();


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
//        FXMLLoader loader = new FXMLLoader(ResourceLoader.faceDetect);
//
//        BorderPane rootElement = (BorderPane) loader.load();

        Parent root = FXMLLoader.load(ResourceLoader.home);
        //
        // Closes all threads
        primaryStage.setOnCloseRequest((ae) -> {
            System.out.println("closing...");
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMaximized(true);
        StageManager.changeWindow(primaryStage, root, "Home");
        // set the proper behavior on closing the application


//        Scene scene = new Scene(rootElement, 800, 600);
//        primaryStage.setTitle("Smile");
//        primaryStage.setScene(scene);
//        primaryStage.show();

//        FaceDetectController controller = loader.getController();
//        primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
//            public void handle(WindowEvent we)
//            {
//                controller.setClosed();
//            }
//        }));
    }

}
