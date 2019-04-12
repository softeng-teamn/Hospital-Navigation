package map;

import elevator.ElevatorFloor;
import map.pathfinding.AlgorithmContext;
import map.pathfinding.Astar;
import map.pathfinding.BreadthFS;
import map.pathfinding.DepthFS;

import java.util.*;

public class PathFindingService {

    public int estimatedTimeOfArrival;
    public HashMap<String, ElevatorFloor> elevTimes;

    public PathFindingService() { }

    /**
     *  attempts to generate a path from a start node to a dest node
     * @param start
     * @param dest
     * @param accessibility
     * @param filter "breadth" for BFS "depth" for DFS "astar" for astar, Any nodeType for a filtered BFS
     * @return Returns null on fail
     * */
    public ArrayList<Node> genPath(MapNode start, MapNode dest, Boolean accessibility, String filter) {

        ArrayList<Node> target;
        AlgorithmContext current;

        switch (filter) {
            case "astar":
                current = new Astar();
                target = current.findDest(start, dest, accessibility, null);
                estimatedTimeOfArrival = current.getEstimatedTime();
                elevTimes = current.getElevTimes();
                break;
            case "breadth":
                current = new BreadthFS();
                target = current.findDest(start, dest, accessibility, null);
                //estimatedTimeOfArrival = ctx.getEstimatedTime();
                //elevTimes = ctx.getElevTimes();
                break;
            case "depth":
                current = new DepthFS();
                target = current.findDest(start, dest, accessibility, null);
                estimatedTimeOfArrival = current.getEstimatedTime();
                //elevTimes = ctx.getElevTimes();
                break;
            default:
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;

        }

        if (target != null){
            return target;
        } else {
            return null;
        }
    }

    public HashMap<String, ElevatorFloor> getElevTimes() {
        return elevTimes;
    }

    int getEstimatedTimeOfArrival(){
         return estimatedTimeOfArrival;
    }

}