package service;

import controller.MapController;
import model.MapNode;
import model.Node;

import java.util.*;

public class PathFindingService {

    private static final int DEFAULT_HV_COST = 10;
    private static final int DEFAULT_DIAGONAL_COST = 14;

    public PathFindingService() {}

    /**
     *  attempts to generate a path from a start node to a dest node
     * @param start
     * @param dest
     * @param accessibility
     * @param type "breadth" for BFS "depth" for DFS "astar" for astar, Any nodeType for a filtered BFS
     * @return Returns null on fail
     * */
    public ArrayList<Node> genPath(MapNode start, MapNode dest, Boolean accessibility, String type) {
        MapNode target;
        switch (type){
            case "astar":
                target = aStar(start, dest, accessibility);
                break;
            case "breadth":
                target = filteredBreadth(start, dest, null);
                break;
            default:
                target = filteredBreadth(start, null, type);
        }


        if (target != null) {
            ArrayList<Node> path = new ArrayList<Node>();
            while (target != null) { // INFINITE LOOP
                //System.out.println("im still in the loop");
                //System.out.println(target.getData().getNodeID());
                // add every item to beginning of list
                path.add(0, target.getData());
                target = target.getParent();
            }
            return path;
        }
        // PATH NOT FOUND
        return null;
    }

    /**
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param dest
     * @return
     */
    MapNode aStar(MapNode start, MapNode dest, Boolean accessibility) {
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

                if(child.getData().getNodeType().equals("STAI") && accessibility) {
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
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param nodeDest
     * @param dest
     * @return
     */
    MapNode filteredBreadth(MapNode start, MapNode nodeDest, String dest) {
        //1.  Initialize queue and set
        PriorityQueue<MapNode> needVisit = new PriorityQueue<>();
        //System.out.println("Created open PriorityQueue");
        HashMap<MapNode, String> visited = new HashMap<MapNode, String>();
        //2. Set up default values
        needVisit.add(start);
        while(!needVisit.isEmpty()){
            //System.out.println(open.toString());
            MapNode current = needVisit.poll();
            //System.out.println("Current Node: " + current.getData().getNodeID());
            visited.put(current, "true");
            //System.out.println("Added current node to explored Set");
            if (nodeDest == null){
                if(current.getData().getNodeType().equals(dest)){
                    //System.out.println("DESTINATION FOUND!!!!!");
                    System.out.println(current.getData().getLongName());

                    return current;
                }
            } else if (dest == null){
                if(current.equals(nodeDest)){
                    return current;

                }
            }
            ArrayList<MapNode> children = getChildren(current);
            for (MapNode child : children){
                if (!visited.containsKey(child) && !needVisit.contains(child)){
                    child.setParent(current, 0);
                    needVisit.add(child);
                }
            }
        }
        return null;
    }

    /**
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param nodeDest
     * @return
     */
    MapNode depth(MapNode start, MapNode nodeDest) {
        //System.out.println("Created open PriorityQueue");
        HashMap<MapNode, String> visited = new HashMap<MapNode, String>();

        MapNode path = depthUtil(start, visited, nodeDest);

        return path;
    }

    private MapNode depthUtil(MapNode current, HashMap<MapNode, String> visited, MapNode dest) {
        visited.put(current, "true");

        ArrayList<MapNode> children = getChildren(current);

        if(current.equals(dest)){
            //System.out.println("DESTINATION FOUND!!!!!");
            return current;
        }

        for (MapNode child : children){
            if (!visited.containsKey(child)){
                child.setParent(current, 0);
                MapNode path = depthUtil(child, visited, dest);
                if (path != null){
                    return path;
                }
            }
        }

        // return null if dest isn't in this node and any of it's child;
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