package service;

import controller.MapController;
import model.MapNode;
import model.Node;
import model.Path;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

public class PathFindingService {

    private static final int DEFAULT_HV_COST = 10;
    private static final int DEFAULT_DIAGONAL_COST = 14;

    public PathFindingService() {}

    /**
     *  attempts to generate a path from a start node to a dest node
     * @param start
     * @param dest
     * @return Returns null on fail
     * */
    public Path genPath(MapNode start, MapNode dest) {
        int numMinutes = 0;
        MapNode target = aStar(start, dest);
        if (target != null) {
            ArrayList<Node> path = new ArrayList<Node>();
            while (target != null) { // INFINITE LOOP
                //System.out.println("im still in the loop");
                //System.out.println(target.getData().getNodeID());
                // add every item to beginning of list
                numMinutes += gToMinutes(target.getG());
                path.add(0, target.getData());
                target = target.getParent();
            }
            return new Path(path, numMinutes);
        }
        // PATH NOT FOUND
        return null;
    }

    public int gToMinutes(int g) {
        return g * 4;
    }

    /**
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param dest
     * @return
     */
    MapNode aStar(MapNode start, MapNode dest) {
        //1.  Initialize queue and set
        PriorityQueue<MapNode> open = new PriorityQueue<>();
        //System.out.println("Created open PriorityQueue");
        Set<MapNode> explored = new HashSet<MapNode>();
        //2. Set up default values
        start.setG(0);
        open.add(start);
        while(!open.isEmpty()){
            //System.out.println(open.toString());
            MapNode current = open.poll();
            //System.out.println("Current Node: " + current.getData().getNodeID());
            explored.add(current);
            //System.out.println("Added current node to explored Set");
            if(current.equals(dest)){
                //System.out.println("DESTINATION FOUND!!!!!");
                return current;
            }
            //System.out.println("Iterating through children of current...");
            for (MapNode child : getChildren(current)){
                //System.out.println("child: " + child.getData().getNodeID());
                child.calculateG(current);
                child.calculateHeuristic(dest);
                double cost = current.getG() + child.getG() + child.getH();

                if (child.equals(dest)) {
                    //System.out.println("This child is our destination node!");
                    child.setParent(current, current.getG() + child.getG());
                    return child;
                }

                if(open.contains(child) && cost>=child.getF()) {
                    //System.out.println("skipping this node because it was already seen");
                    continue;
                }

                if(explored.contains(child) && cost>=child.getF()) {
                    //System.out.println("skipping this node because the cost is to big");
                    continue;
                }

                else if(!open.contains(child) || cost < child.getF()){
                    //System.out.println("setting child's parent to be current");
                    child.setParent(current, current.getG() + child.getG());
                    if(open.contains(child)){
                        open.remove(child);
                    }
                    //System.out.println("adding child to open list");
                    open.add(child);
                }
            }
        }
        return null;
    }

    /**
     * Gets reachable MapNodes from given MapNode
     * @param node
     * @return
     */
     ArrayList<MapNode> getChildren(MapNode node) {
        ArrayList<Node> neighbors = MapController.getNodesConnectedTo(node.getData());
        ArrayList<MapNode> nodeChildren = new ArrayList<>();
        for (Node n : neighbors) {
            nodeChildren.add(new MapNode(n.getXcoord(), n.getYcoord(), n));
        }
        return nodeChildren;
    }
}