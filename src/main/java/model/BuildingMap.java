package model;

import java.util.HashMap;

public class BuildingMap {

    private HashMap<Integer, FloorMap> bMap;

    public BuildingMap(HashMap<Integer, FloorMap> bMap) {
        this.bMap = bMap;
    }

    public FloorMap getMap(int floor) {
        return bMap.get(floor);
    }

}
