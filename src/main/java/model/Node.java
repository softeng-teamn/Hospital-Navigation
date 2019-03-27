package model;

public class Node {

    private int xcoord, ycoord;
    private String nodeID, floor, building, nodeType, longName, shortName;

    public boolean validateID(String nodeID) {
        return true;
    }

    public boolean validateFloor(String floor) {
        return true;
    }

    public boolean validateType(String nodeType) {
        return true;
    }

}
