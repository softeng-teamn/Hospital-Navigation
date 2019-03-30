package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Node;
import model.Point;
import service.ResourceLoader;
import service.StageManager;

public class HomeController extends controller.MapController {

    @FXML
    private JFXButton editBtn, schedulerBtn, serviceBtn;

    @FXML
    // switches window to map editor screen.
    public void showMapEditor() throws Exception {
        Stage stage = (Stage) editBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to request screen
    public void showRequest() throws Exception {
        Stage stage = (Stage) serviceBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeWindow(stage, root, "Service Request");
    }

    @FXML
    // switches window to schedule screen
    public void showSchedule() throws Exception {
        Stage stage = (Stage) schedulerBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeWindow(stage, root, "Scheduler");
    }

    // Get path from start node to destination node
    private Point requestPath(Node start, Node dest) {
        Point myPath = new Point(1,1,1,"someID", null);
        return myPath;
    }

    // Show path to user
    private void displayPath(Point path) {

    }


}
