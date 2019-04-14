package application_state;

import javafx.scene.image.ImageView;
import map.Edge;
import map.Node;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationState {

    //******************************************

    private Node nodeToEdit;
    private ArrayList<Edge> edgesToEdit;
    private static HashMap<String, ImageView> imageCache;


    //******************************************

    private static class SingletonHelper {
        private static final ApplicationState appState = new ApplicationState();
    }

    public static ApplicationState getApplicationState() {
        return SingletonHelper.appState;
    }

    private ApplicationState() {

    }

    public static HashMap<String, ImageView> getImageCache() {
        return imageCache;
    }

    public static void setImageCache(HashMap<String, ImageView> imageCache) {
        ApplicationState.imageCache = imageCache;
    }

    public Node getNodeToEdit() {
        return nodeToEdit;
    }

    public void setNodeToEdit(Node nodeToEdit) {
        this.nodeToEdit = nodeToEdit;
    }

    public ArrayList<Edge> getEdgesToEdit() {
        return edgesToEdit;
    }

    public void setEdgesToEdit(ArrayList<Edge> edgesToEdit) {
        this.edgesToEdit = edgesToEdit;
    }
}
