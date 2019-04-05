package controller;

import model.MapNode;
import model.Node;
import service.MapService;

import java.util.ArrayList;

public class MapController extends Controller {

    static MapService map;

    // Creates static variables when compiled
    static {
        map = new MapService();
    }

    // Initializes the Map ???? if static method works, then this is not needed.
    // Question for implementor: How will we know when the map needs to be built?
    public void init() {

    }


    /**
     * a getter to return the notes connected to a given node
     * @param n
     * @return
     */
    public static ArrayList<Node> getNodesConnectedTo(Node n) {

        return Controller.connections.get(n.getNodeID());

    }


}
