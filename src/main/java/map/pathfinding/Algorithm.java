package map.pathfinding;

import elevator.ElevatorFloor;
import map.MapController;
import map.MapNode;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;

public interface Algorithm {

    ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter);
//    int getEstimatedTime();
//    HashMap<String, ElevatorFloor> getElevTimes();



}
