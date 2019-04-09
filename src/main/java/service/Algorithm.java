package service;

import controller.MapController;
import model.MapNode;
import model.Node;

import java.util.ArrayList;

public interface Algorithm {

    ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter);
    int getEstimatedTime();

    /**
     * Gets reachable MapNodes from given MapNode
     * @param node
     * @return
     */
    default ArrayList<MapNode> getChildren(MapNode node) {
        ArrayList<Node> neighbors = MapController.getNodesConnectedTo(node.getData());
        ArrayList<MapNode> nodeChildren = new ArrayList<>();
        for (Node n : neighbors) {
            nodeChildren.add(new MapNode(n.getXcoord(), n.getYcoord(), n));
        }
        return nodeChildren;
    }

}
