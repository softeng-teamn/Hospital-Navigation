package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapController;
import map.MapNode;
import map.Node;
import map.NodeFacade;

import java.util.*;

/**
 * A* implementation
 */
public class Astar extends AlgorithmContext implements Algorithm{
    public int estimatedTime;
    PriorityQueue<MapNode> open;
    Set<MapNode> explored;

    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;


    @Override
    public int getET() {
        return estimatedTime;
    }

    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {
        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;
        open = new PriorityQueue<>();
        explored = new HashSet<MapNode>();
        this.start.setG(0);
        open.add(start);
    }

    @Override
    MapNode throughMap() {
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
                    open.add(child);
                }
            }
        }
        return null;
    }

}
