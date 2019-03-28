package controller;

import model.Node;
import model.Point;

public class HomeController extends MapController {


    // switches window to map editor screen
    private void showMapEditor() {

    }

    // switches window to request screen
    private void showRequest() {

    }

    // switches window to schedule screen
    private void showSchedule() {

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
