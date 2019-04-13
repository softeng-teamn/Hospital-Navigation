package map.pathfinding;

import map.MapNode;

import java.util.HashMap;

public class Dijsktra extends AlgorithmContext implements Algorithm{
    HashMap<MapNode, MapNode> visited;


    private MapNode start;
    private MapNode dest;
    private boolean accessibility;
    private String filter;

    @Override
    void initial(MapNode start, MapNode dest, boolean accessibility, String filter) {
        this.start = start;
        this.dest = dest;
        this.accessibility = accessibility;
        this.filter = filter;

    }

    @Override
    MapNode throughMap() {
        return null;
    }

    @Override
    int getET() {
        return 0;
    }
}
