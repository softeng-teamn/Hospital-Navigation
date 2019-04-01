package controller;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import model.MapNode;
import model.Node;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.ArrayList;

import java.util.ArrayList;

public class HomeController extends MapController {

    @FXML
    private JFXButton editBtn, editBtnLbl, schedulerBtn, schedulerBtnLbl, serviceBtn, serviceBtnLbl;
    @FXML
    private JFXSlider zoom_slider;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private JFXListView list_view;

    public Group zoomGroup;

    @FXML
    void initialize() {

        ArrayList<Node> nodes = dbs.getAllNodes();
        for (Node n : nodes) {
            System.out.println(n.getNodeID());
            JFXListCell<String> cell = new JFXListCell<>();
            cell.setText(n.getShortName());
            list_view.getItems().add(cell);
        }

        System.out.println("We are running our init");
        zoom_slider.setMin(0.3);
        zoom_slider.setMax(0.9);
        zoom_slider.setValue(0.3);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
    }



    //

    @FXML
    // searches for Room
    public void searchBarEnter(ActionEvent e) {
        String text = search_bar.getText();
        System.out.println(text);
    }

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

    @FXML
    void zoomIn(ActionEvent event) {
    System.out.println("airportapp.Controller.zoomIn");
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.05);
    }

    @FXML
    void zoomOut(ActionEvent event) {
    System.out.println("airportapp.Controller.zoomOut");
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.05);
    }

    private void zoom(double scaleValue) {
//    System.out.println("airportapp.Controller.zoom, scaleValue: " + scaleValue);
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

}
