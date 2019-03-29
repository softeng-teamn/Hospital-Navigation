package service;

import controller.MapController;
import model.MapNode;
import model.Node;

import java.util.ArrayList;

public class PathFindingService {

//    // A* Search Algorithm
//1.  Initialize the open list
//2.  Initialize the closed list
//    put the starting node on the open
//    list (you can leave its f at zero)
//
//3.  while the open list is not empty
//    a) find the node with the least f on
//    the open list, call it "q"
//
//    b) pop q off the open list
//
//    c) generate q's 8 successors and set their
//    parents to q
//
//    d) for each successor
//    i) if successor is the goal, stop search
//    successor.g = q.g + distance between
//    successor and q
//    successor.h = distance from goal to
//    successor (This can be done using many
//            ways, we will discuss three heuristics-
//                       Manhattan, Diagonal and Euclidean
//                       Heuristics)
//
//    successor.f = successor.g + successor.h
//
//    ii) if a node with the same position as
//    successor is in the OPEN list which has a
//    lower f than successor, skip this successor
//
//    iii) if a node with the same position as
//    successor  is in the CLOSED list which has
//    a lower f than successor, skip this successor
//    otherwise, add  the node to the open list
//    end (for loop)
//
//    e) push q on the closed list
//    end (while loop)

    public static final int DEFAULT_HV_COST = 10;
    public static final int DEFAULT_DIAGONAL_COST = 14;
    public ArrayList<MapNode> open;
    public ArrayList<MapNode> closed;

    public PathFindingService() {

    }

    public ArrayList<MapNode> aStar(MapNode start, MapNode dest) {
        //1.  Initialize the open list
        open = new ArrayList<MapNode>();
        //2.  Initialize the closed list
        //    put the starting node on the open
        //    list (you can leave its f at zero)
        start.setF(0.0);
        open.add(start);
        //    3.  while the open list is not empty
        while (!open.isEmpty()) {
            //    a) find the node with the least f on
            //    the open list, call it "q"
            MapNode q = open.get(1);
            for (int i = 1; i < open.size(); i++) {
                if (open.get(i).getF() < q.getF()) {
                    q = open.get(i);
                }
            }
            //
            //    b) pop q off the open list
            open.remove(q);
            //    c) generate q's 8 successors and set their
            //    parents to q
            ArrayList<MapNode> successors = getChildren(q);
            //    d) for each successor
            for (MapNode successor : successors) {
                //    i) if successor is the goal, stop search
                if (successor.equals(dest)) {
                    System.out.println("WE JUST FOUND THE PATH!!!!");
                    break;
                }
                //    successor.g = q.g + distance between successor and q
                successor.checkBetter(q, DEFAULT_HV_COST);
                //    successor.h = distance from goal to
                //    successor (This can be done using many
                //            ways, we will discuss three heuristics-
                //                       Manhattan, Diagonal and Euclidean
                //                       Heuristics)
                successor.calculateHeuristic(dest);
                //    successor.f = successor.g + successor.h
                successor.getF();

                //    ii) if a node with the same position as
                //    successor is in the OPEN list which has a
                //    lower f than successor, skip this successor
                if (open.contains(successor) && successor.getF() > open.get(open.indexOf(successor)).getF() ) {
                    continue;
                }
                //
                //    iii) if a node with the same position as
                //    successor  is in the CLOSED list which has
                //    a lower f than successor, skip this successor
                //    otherwise, add  the node to the open list
                if (closed.contains(successor) && successor.getF() > closed.get(closed.indexOf(successor)).getF()) {
                    continue;
                }
                //    end (for loop)
            }
            //    e) push q on the closed list
            closed.add(q);

        }
        //    end (while loop)
        return new ArrayList<>();
    }

    public ArrayList<MapNode> getChildren(MapNode node) {
        ArrayList<Node> neighbors = MapController.getNodesConnectedTo(node.getData());
        ArrayList<MapNode> nodeChildren = new ArrayList<MapNode>();
        for (Node n : neighbors) {
            nodeChildren.add(new MapNode(n.getXcoord(), n.getYcoord(), n));
        }
        return nodeChildren;
    }



}
