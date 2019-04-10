package service;

import model.ElevatorFloor;
import model.MapNode;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class BreadthFS implements Algorithm {


    public int estimatedTime;
    public HashMap<String, ElevatorFloor> elevTimes;

    @Override
    public ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter) {
        MapNode target = breadth(start, dest, accessibility, filter);
        if (target != null) {
            ArrayList<Node> path = new ArrayList<Node>();
            while (target != null) { // INFINITE LOOP
                //System.out.println("im still in the loop");
                //System.out.println(target.getData().getNodeID());
                // add every item to beginning of list
                if(target.getData().getNodeType().equals("ELEV")) {
                    String floor = target.getData().getNodeID().substring(target.getData().getNodeID().length() - 2);//get floor
                    String key = target.getData().getNodeID().substring(0, target.getData().getNodeID().length() - 2);//get all info except floor
                    ElevatorFloor ef = new ElevatorFloor(floor, target.getG() / 734);
                    if(!elevTimes.containsKey(key)) {
                        elevTimes.put(key, ef);
                    }
                    else{
                        elevTimes.replace(key, ef);
                    }
                }
                path.add(0, target.getData());
                target = target.getParent();
            }
            return path;
        }
        return null;
    }

    @Override
    public int getEstimatedTime() {
        return estimatedTime;
    }

    @Override
    public HashMap<String, ElevatorFloor> getElevTimes() {return elevTimes; }



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
