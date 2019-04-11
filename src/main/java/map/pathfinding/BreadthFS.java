package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapNode;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class BreadthFS implements Algorithm {

    @Override
    public ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter) {
        MapNode target = breadth(start, dest, accessibility, filter);
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

    @Override
    public HashMap<String, ElevatorFloor> getElevTimes() {return new HashMap<>(); }



    /**
     *  Will either return the last MapNode with a parent chain back to the start
     *  or returns null if we CANT get to the dest node
     * @param start
     * @param dest
     * @param filter
     * @return
     */
    public MapNode breadth(MapNode start, MapNode dest, boolean accessibility, String filter) {
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
                } else if (!visited.containsKey(child) && !needVisit.contains(child)){
                    child.setParent(current, 0);
                    needVisit.add(child);
                }
            }
        }
        return null;
    }



}
