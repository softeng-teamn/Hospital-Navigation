package elevator_api;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InternalTransportRequest {
    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) throws ServiceException {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/api/internalTransportRequest.fxml"));

        Parent root;
        try {
            root = loader.load();
        } catch (Exception var12) {
            System.out.println("failed to load the file");
            var12.printStackTrace();
            return;
        }

        Scene scene = new Scene(root, (double) windowWidth, (double) windowLength);
        primaryStage.setX((double) xcoord);
        primaryStage.setY((double) ycoord);
        primaryStage.setTitle("Internal Transport Request");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource(cssPath).toExternalForm());
        primaryStage.show();
    }
}
