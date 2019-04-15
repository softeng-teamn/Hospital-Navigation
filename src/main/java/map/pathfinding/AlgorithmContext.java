package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapController;
import map.MapNode;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AlgorithmContext{
    abstract void initial(MapNode start, MapNode dest, boolean accessibility, String filter);
    abstract MapNode throughMap();
    abstract int getET();

    private HashMap<String, ElevatorFloor> elevTimes;

    public ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter) {
        MapNode target = returnMapNode(start, dest, accessibility, filter);

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

    public MapNode returnMapNode(MapNode start, MapNode dest, boolean accessibility, String filter){
        initial(start, dest, accessibility, filter);

        MapNode target = throughMap();
        return target;
    }

    /**
     * Gets reachable MapNodes from given MapNode
     * @param node
     * @return
     */
    public ArrayList<MapNode> getChildren(MapNode node) {
        ArrayList<Node> neighbors = MapController.getNodesConnectedTo(node.getData());
        ArrayList<MapNode> nodeChildren = new ArrayList<>();
        for (Node n : neighbors) {
            nodeChildren.add(new MapNode(n.getXcoord(), n.getYcoord(), n));
        }
        return nodeChildren;
    }

    public int getEstimatedTime() {
        return getET();
    }

    public HashMap<String, ElevatorFloor> getElevTimes(){ return elevTimes; }



}
