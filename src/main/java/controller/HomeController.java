package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import model.MapNode;
import model.Node;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.ArrayList;

public class HomeController extends MapController {

    @FXML
    private JFXButton editBtn, editBtnLbl, schedulerBtn, schedulerBtnLbl, serviceBtn, serviceBtnLbl;

    @FXML
    // switches window to map editor screen.
    public void showMapEditor() throws Exception {
        Stage stage = (Stage) editBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.mapEdit);
        StageManager.changeExistingWindow(stage, root, "Map Editor");
    }

    @FXML
    // switches window to request screen
    public void showRequest() throws Exception {
        Stage stage = (Stage) serviceBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    @FXML
    // switches window to schedule screen
    public void showSchedule() throws Exception {
        Stage stage = (Stage) schedulerBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
    }
  
    //Pathfind and show path to user
    public void pathfind(Node start, Node dest) {
        MapNode pStart = new MapNode(start.getXcoord(), start.getYcoord(), start);
        MapNode pDest = new MapNode(dest.getXcoord(), dest.getYcoord(), dest);
        PathFindingService pathFindingService = new PathFindingService();
        ArrayList<Node> path;
        path = pathFindingService.genPath(pStart, pDest);
        for (int i=0; i<path.size(); i++){
            Line line = new Line(path.get(i).getXcoord(), path.get(i).getYcoord(),
                                 path.get(i++).getXcoord(), path.get(i++).getYcoord());
            line.getEndX(); //Delete this line, I just put it here to appease spotBugs
            //Nathan here, I don't know the specifics of how our UI system works.
            //Thus, the below lines are commented until I learn how to interface with it.
            //IF you uncomment it, then it will simply draw the path on a white background.
            //NOTE: THIS DOES NOT SUPPORT A DYNAMICALLY MOVING PATH (yet)
            //Group root = new Group();
            //Scene scene = new Scene(root, 1920, 1080, Color.WHITE);
            //root.getChildren.add(line);
            //stage.setScene(scene);
            //stage.show();
        }
    }


}
