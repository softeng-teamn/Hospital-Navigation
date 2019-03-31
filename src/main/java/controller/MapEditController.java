package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Edge;
import model.Node;
import service.ResourceLoader;
import service.StageManager;

import java.util.Collection;

public class MapEditController extends MapController {

    @FXML
    private JFXButton homeBtn;

    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    // add a new node into map and DB
     boolean insertNode(Node n, Collection<Edge> e) {
        if(e.isEmpty()){
            return false;
        }
        boolean success = dbs.insertNode(n);
        /*
        for (Edge edge: e){
            if(dbs.getEdge(edge.getEdgeID()) != null){
                success &&= success && dbs.insertEdge(edge);
        }
        */
        return success;
    }

    // edit node form map and DB
     boolean updateNode(Node n) {
        return dbs.updateNode(n);
    }

    // remove node from Map and DB
     boolean deleteNode(Node n) {
        return dbs.deleteNode(n);
    }


}
