package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapController;
import map.MapNode;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * breadth first search implementation
 */
public class BreadthFS extends AlgorithmContext implements Algorithm{
    PriorityQueue<MapNode> needVisit;
    HashMap<MapNode, String> visited;


    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;

    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {
        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;
        needVisit = new PriorityQueue<>();
        visited = new HashMap<MapNode, String>();
        needVisit.add(start);

    }

    @Override
    MapNode throughMap() {
        while(!needVisit.isEmpty()){
            //System.out.println(open.toString());
            MapNode current = needVisit.poll();
            //System.out.println("Current Node: " + current.getData().getNodeID());
            visited.put(current, "true");
            //System.out.println("Added current node to explored Set");
            if (dest == null){
                if(current.getData().getNodeType().equals(filter)){
                    //System.out.println("DESTINATION FOUND!!!!!");
                    System.out.println(current.getData().getLongName());

                    return current;
                }
            } else if (filter == null){
                if(current.equals(dest)){
                    return current;

                }
            }
            ArrayList<MapNode> children = getChildren(current);
            for (MapNode child : children){
                if(child.getData().getNodeType().equals("STAI") && accessibility) {
                    //System.out.println("skipping this node because the cost is to big");
                    continue;
                }

                if(child.getData().isClosed()) {
                    //System.out.println("skipping this node because the cost is to big");
                    continue;
                }

                else if (!visited.containsKey(child) && !needVisit.contains(child)){
                    child.setParent(current, 0);
                    needVisit.add(child);
                }
            }
        }
        return null;    }

    @Override
    int getET() {
        return 0;
    }



}
