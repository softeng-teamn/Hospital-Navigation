package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapNode;
import map.Node;
import map.NodeFacade;

import java.util.*;

public class Astar implements Algorithm {

    public int estimatedTime;
    public HashMap<String, ElevatorFloor> elevTimes;

    @Override
    public ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter) {
        System.out.println("    in Astar class " + start + dest);
        MapNode target = aStar(start, dest, accessibility, null);
        elevTimes = new HashMap<>();
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
     * @return
     */
    public MapNode aStar(MapNode start, MapNode dest, boolean accessibility, String filter) {
        System.out.println("in other astar function: " + start + dest);
        //1.  Initialize queue and set
        PriorityQueue<MapNode> open = new PriorityQueue<>();
        //System.out.println("Created open PriorityQueue");
        Set<MapNode> explored = new HashSet<MapNode>();
        //2. Set up default values
        start.setG(0);
        open.add(start);
        System.out.println(open);
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
                NodeFacade nf = new NodeFacade(child);
                nf.mapNodeCalculateG(current);
                nf.mapNodeCalculateHeuristic(dest);
                double cost = current.getG() + child.getG() + child.getH();

                if (child.equals(dest)) {
                    //System.out.println("This child is our destination node!");
                    child.setParent(current, child.getG());
                    estimatedTime = child.getG()/734;
                    System.out.println("    returning child: " + start + dest + child);
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

                if(child.getData().isClosed()) {
                    //System.out.println("skipping this node because the cost is to big");
                    continue;
                }

                else if(!open.contains(child) || cost < child.getF()){
                    //System.out.println("setting child's parent to be current");
                    child.setParent(current, child.getG());
                    if(open.contains(child)){
                        open.remove(child);
                    }
                    System.out.println("adding child to open list");
                    open.add(child);
                }
            }
        }
        System.out.println("    retrning null: " + start + dest);
        return null;
    }


}
