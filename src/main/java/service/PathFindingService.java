package service;

import controller.MapController;
import model.MapNode;
import model.Node;

import java.util.*;

public class PathFindingService {

    private static final int DEFAULT_HV_COST = 10;
    private static final int DEFAULT_DIAGONAL_COST = 14;

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

        AlgorithmContext ctx = new AlgorithmContext(new Astar());

        ArrayList<Node> target;


        switch (filter) {
            case "astar":
                ctx.setStrategy(new Astar());
                target = ctx.findPathCTX(start, dest, accessibility, null);
                break;
            case "breadth":
                ctx.setStrategy(new BreadthFS());
                target = ctx.findPathCTX(start, dest, accessibility, null);
                break;
            case "depth":
                ctx.setStrategy(new DepthFS());
                target = ctx.findPathCTX(start, dest, accessibility, null);
                break;
            default:
                ctx.setStrategy(new BreadthFS());
                target = ctx.findPathCTX(start, null, accessibility, filter);
                break;
        }

        if (target != null){
            return target;
        } else {
            return null;
        }


    }













}