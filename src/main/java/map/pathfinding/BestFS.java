package map.pathfinding;

import map.MapNode;
import map.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BestFS extends AlgorithmContext implements Algorithm {
    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;
    PriorityQueue<MapNode> open;
    Set<MapNode> explored;
    MapNode current;


    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {
        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;
        open = new PriorityQueue<>();
        explored = new HashSet<MapNode>();
        start.calculateHeuristic(dest);
        open.add(start);
    }

    @Override
    MapNode throughMap() {
        while(!open.isEmpty()){
            double min = 99999999;
            MapNode minNode = new MapNode(0, 0, new Node(0,0));

            for(MapNode node : open){
                if (node.getH() < min){
                    min = node.getH();
                    minNode = node;
                }
            }
            current = minNode;
            open.remove(minNode);

//            current = open.poll();
            explored.add(current);
            if (current.equals(dest)){
                return current;
            }




            for (MapNode child : getChildren(current)){
                if (child.equals(dest)){
                    child.setParent(current, 0);
                    return child;
                }

                child.calculateHeuristic(dest);
//                if (child.getH() < min && !explored.contains(child) && !open.contains(child)){
//                    min = child.getH();
//                    minNode = child;
//                }

                if (child.getData().getNodeType() == "STAI" && accessibility){
                    continue;
                }
                if (!explored.contains(child) && !open.contains(child)){
//                    if(child.getH() < current.getH())
                    child.setParent(current, 0);
                    open.add(child);
                }
            }
        }
        return null;
    }

    @Override
    int getET() {
        return 0;
    }
}
