package service;

import model.MapNode;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DepthFS implements Algorithm {

    @Override
    public ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter) {
        MapNode target = depth(start, dest, accessibility, null);
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
        return null;
    }

    @Override
    public int getEstimatedTime() {
        return 0;
    }


    /**
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param dest
     * @return
     */
    public MapNode depth(MapNode start, MapNode dest, boolean accessibility, String filter) {
        //System.out.println("Created open PriorityQueue");
        HashMap<MapNode, String> visited = new HashMap<MapNode, String>();

        MapNode path = depthUtil(start, visited, dest, accessibility);

        return path;
    }

    private MapNode depthUtil(MapNode current, HashMap<MapNode, String> visited, MapNode dest, boolean accessibility) {
        visited.put(current, "true");

        ArrayList<MapNode> children = getChildren(current);

        if(current.equals(dest)){
            //System.out.println("DESTINATION FOUND!!!!!");
            return current;
        }

        for (MapNode child : children){
            if(child.getData().getNodeType().equals("STAI") && accessibility) {
                //System.out.println("skipping this node because the cost is to big");
                continue;
            } else if (!visited.containsKey(child)){
                child.setParent(current, 0);
                MapNode path = depthUtil(child, visited, dest, accessibility);
                if (path != null){
                    return path;
                }
            }
        }

        // return null if dest isn't in this node and any of it's child;
        return null;
    }

}
