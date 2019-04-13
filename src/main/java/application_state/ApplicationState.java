package application_state;

import map.Edge;
import map.Node;
import java.util.ArrayList;

public class ApplicationState {

    //******************************************

    private Node nodeToEdit;
    private ArrayList<Edge> edgesToEdit;
    private FakeEventBus feb = new FakeEventBus();


    //******************************************

    private static class SingletonHelper {
        private static final ApplicationState appState = new ApplicationState();
    }

    public static ApplicationState getApplicationState() {
        return SingletonHelper.appState;
    }

    private ApplicationState() {

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

    public FakeEventBus getFeb() {
        return feb;
    }
}
