package service;

import model.MapNode;
import model.Node;

import java.util.ArrayList;

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

}
