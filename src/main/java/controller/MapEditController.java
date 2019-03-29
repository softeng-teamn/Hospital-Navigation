package controller;

import model.Edge;
import model.Node;

import java.util.Collection;

public class MapEditController extends MapController {

    @FXML
    private JFXButton homeBtn;

    // switches window to home screen
    private void showHome() {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeWindow(stage, root, "Home (Path Finder)");
    }

    // add a new node into map and DB
    private void insertNode(Node n, Collection<Edge> e) {

    }

    // edit node form map and DB
    private void editNode(Node n) {

    }

    // remove node from Map and DB
    private void deleteNode(Node n) {

    }

}
