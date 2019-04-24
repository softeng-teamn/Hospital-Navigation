package edu.wpi.cs3733d19.teamN.map.pathfinding;

import edu.wpi.cs3733d19.teamN.map.MapNode;
import edu.wpi.cs3733d19.teamN.map.Node;

import java.util.ArrayList;

/**
 * Algorithm interface for strategy design pattern
 */
public interface Algorithm {

    ArrayList<Node> findDest(MapNode start, MapNode dest, boolean accessibility, String filter);
//    int getEstimatedTime();
//    HashMap<String, ElevatorFloor> getElevTimes();



}
