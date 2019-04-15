package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapController;
import map.MapNode;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DepthFS extends AlgorithmContext implements Algorithm{
    HashMap<MapNode, String> visited;

    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;
    private MapNode current;


    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {
        visited = new HashMap<MapNode, String>();
        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;
    }

    @Override
    MapNode throughMap() {
        current = start;

        MapNode path = depthUtil();

        return path;
    }

    @Override
    int getET() {
        return 0;
    }

    private MapNode depthUtil() {
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
                current = child;
                MapNode path = depthUtil();
                if (path != null){
                    return path;
                }
            }
        }

        // return null if dest isn't in this node and any of it's child;
        return null;
    }

}
