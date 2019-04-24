package edu.wpi.cs3733d19.teamN.map.pathfinding;

import edu.wpi.cs3733d19.teamN.map.MapNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * depth first search implementation
 */
public class DepthFS extends AlgorithmContext implements Algorithm{


    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;
    private MapNode current;


    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {

        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;
    }

    @Override
    MapNode throughMap() {
        HashMap<MapNode, String> visited = new HashMap<MapNode, String>();


        MapNode path = depthUtil(start, visited, dest, accessibility);

        return path;
    }

    @Override
    int getET() {
        return 0;
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
            }

            if(child.getData().isClosed()) {
                //System.out.println("skipping this node because the cost is to big");
                continue;
            }
            
            else if (!visited.containsKey(child)){
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
