package service;

import controller.MapController;
import model.MapNode;
import model.Node;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

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

    private static final int DEFAULT_HV_COST = 10;
    private static final int DEFAULT_DIAGONAL_COST = 14;

    public PathFindingService() {

    }

    public ArrayList<MapNode> aStar(MapNode start, MapNode dest) {
        //1.  Initialize the open list
        PriorityQueue<MapNode> open = new PriorityQueue<>();
        start.setF(0.0);
        open.add(start);
        //2.  Initialize the path list
        ArrayList<MapNode> path = new ArrayList<>();
        start.checkBetter(null, 0);
        path.add(start);
        //3. Initialize the closed list
        ArrayList<MapNode> closed = new ArrayList<>();
        closed.add(start);

        while(!open.isEmpty()) {
            MapNode parent = open.poll();
            if (parent.equals(dest)){
                break;
            }
            ArrayList<MapNode> children = getChildren(parent);
            for (MapNode child : children) {
                int cost = parent.getG() + DEFAULT_HV_COST;
                if (!closed.contains(child) || cost < child.getG()){
                    child.checkBetter(parent, cost);
                    child.calculateHeuristic(dest);
                    child.getF();
                    open.add(child);
                    path.add(parent);
                }
            }
        }
        return path;
    }

    public PriorityQueue<MapNode> aStar2(MapNode start, MapNode dest) {
        //1.  Initialize queue and set
        PriorityQueue<MapNode> open = new PriorityQueue<>();
        Set<MapNode> explored = new HashSet<MapNode>();
        //2. Set up default values
        start.setG(0);
        open.add(start);
        while(!open.isEmpty()){
            MapNode current = open.poll();
            explored.add(current);

            if(current.equals(dest)){
                break;
            }

            for (MapNode child : getChildren(current)){
                child.checkBetter(current, DEFAULT_HV_COST);
                child.calculateHeuristic(dest);
                double cost = current.getG() + DEFAULT_HV_COST + child.getH();

                if(explored.contains(child) && cost>=child.getF()) {
                    continue;
                }
                else if(!open.contains(child) || cost < child.getF()){
                    child.setParent(current, current.getG() + DEFAULT_HV_COST);
                    if(open.contains(child)){
                        open.remove(child);
                    }
                    open.add(child);
                }
            }
        }
        return open;
    }


    private ArrayList<MapNode> getChildren(MapNode node) {
        ArrayList<Node> neighbors = MapController.getNodesConnectedTo(node.getData());
        ArrayList<MapNode> nodeChildren = new ArrayList<>();
        for (Node n : neighbors) {
            nodeChildren.add(new MapNode(n.getXcoord(), n.getYcoord(), n));
        }
        return nodeChildren;
    }
}
