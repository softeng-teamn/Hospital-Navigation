package controller;

import model.Node;
import model.Point;

public class HomeController extends MapController {

    @FXML
    private JFXButton editBtn, schedulerBtn, serviceBtn;

    @FXML
    // switches window to map editor screen
    private void showMapEditor() {
        Stage stage = (Stage) editBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to request screen
    private void showRequest() {
        Stage stage = (Stage) serviceBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeWindow(stage, root, "Service Request");
    }

    @FXML
    // switches window to schedule screen
    private void showSchedule() {
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
