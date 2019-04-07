package controller;

import model.Node;

import java.util.ArrayList;

public class MapController extends Controller {

    // Creates static variables when compiled
    static { }

    /**
     * a getter to return the notes connected to a given node
     * @param n
     * @return
     */
    public static ArrayList<Node> getNodesConnectedTo(Node n) {
        return Controller.connections.get(n.getNodeID());
    }
}