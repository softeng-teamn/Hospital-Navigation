import application_state.ApplicationState;
import database.DatabaseService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.Core;
import service.ResourceLoader;
import service.StageManager;
import web_server.WebServer;

import java.io.IOException;
import java.util.HashMap;

public class Main extends Application {

    static WebServer webServer;

    /**
     *  Application Start
     * @param args arguments for main
     */
    public static void main(String[] args) {

        WebServer.run();
//        new Thread(() -> {
//            webServer = new WebServer();
//        }).start();


        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
        ApplicationState.getApplicationState().setImageCache(imageCache);
        launch();
    }

    /**
     * initializes the UI
     * @param primaryStage parameter to start the UI
     * @throws Exception if the FXML fails to load
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(ResourceLoader.faceDetect);
//
//        BorderPane rootElement = (BorderPane) loader.load();

        Parent root = FXMLLoader.load(ResourceLoader.employeeEdit);
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
