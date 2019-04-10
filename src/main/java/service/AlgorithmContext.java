package service;

import model.ElevatorFloor;
import model.MapNode;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class AlgorithmContext {

    private Algorithm strategy;

    public AlgorithmContext(Algorithm strategy) {
        this.strategy = strategy;
    }

    public Algorithm getStrategy() {
        return strategy;
    }

    public void setStrategy(Algorithm strategy) {
        this.strategy = strategy;
    }

    public ArrayList<Node> findPathCTX(MapNode start, MapNode dest, boolean accessibility, String filter){
        return strategy.findDest(start, dest, accessibility, filter);
    }

    public int getEstimatedTime() {return strategy.getEstimatedTime();}
    public HashMap<String, ElevatorFloor> getElevTimes(){return strategy.getElevTimes();}

}
