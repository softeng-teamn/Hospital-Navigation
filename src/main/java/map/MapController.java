package map;

import database.DatabaseService;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controls the node hashmap for pathfinding
 */
public class MapController {

    // Creates static variables when compiled
    static {
        //initConnections();
    }

    static HashMap<String, ArrayList<Node>> connections;

    /**
     * a getter to return the notes connected to a given node
     * @param n the node to get nodes connected to
     * @return all nodes connected to the given node
     */
    public static ArrayList<Node> getNodesConnectedTo(Node n) {
        return connections.get(n.getNodeID());
    }

    /**
     * initializes the node hashmap by extracting all edges and nodes from the database
     */
    public static void initConnections() {
        System.out.println("creating hashmap ...");
        connections = new HashMap<>();
        ArrayList<Edge> allEdges = DatabaseService.getDatabaseService().getAllEdges();

        for (Edge e : allEdges) {
            if (connections.containsKey(e.getNode1().getNodeID())) {
                connections.get(e.getNode1().getNodeID()).add(e.getNode2());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode2());
                connections.put(e.getNode1().getNodeID(), newList);
            }


            if (connections.containsKey(e.getNode2().getNodeID())) {
                connections.get(e.getNode2().getNodeID()).add(e.getNode1());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode1());
                connections.put(e.getNode2().getNodeID(), newList);
            }
        }

        System.out.println("the hashmap is MADE!");
    }
}