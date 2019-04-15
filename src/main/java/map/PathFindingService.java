package map;

import elevator.ElevatorFloor;
import map.pathfinding.*;

import java.util.*;

public class PathFindingService {

    public int estimatedTimeOfArrival;
    public HashMap<String, ElevatorFloor> elevTimes;

    public PathFindingService() {
        this.elevTimes = new HashMap<>();
    }

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
            case "dijsktra":
                current = new Dijsktra();
                target = current.findDest(start, dest, accessibility, null);
//                estimatedTimeOfArrival = current.getEstimatedTime();
                break;
            case "best":
                current = new BestFS();
                target = current.findDest(start, dest, accessibility, null);
                break;
            case "REST":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            case "ELEV":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            case "INFO":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            case "CONF":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            case "EXIT":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            case "STAI":
                current = new BreadthFS();
                target = current.findDest(start, null, accessibility, filter);
                break;
            default:
                current = new Astar();
                target = current.findDest(start, dest, accessibility, null);
                estimatedTimeOfArrival = current.getEstimatedTime();
                elevTimes = current.getElevTimes();
                break;
        }

        if (target != null){
            System.out.println(target);
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