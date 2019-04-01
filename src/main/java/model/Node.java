package model;

import java.util.Objects;

public class Node {

    private int xcoord, ycoord;
    private String nodeID, floor, building, nodeType, longName, shortName;

    public boolean validateID(String nodeID) {
        return true;
    }

    public boolean validateFloor(String floor) {
        return true;
    }

    public int getXcoord() {
        return xcoord;
    }

    public void setXcoord(int xcoord) {
        this.xcoord = xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public void setYcoord(int ycoord) {
        this.ycoord = ycoord;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeID='" + nodeID + '\'' +
                '}';
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Node(int x, int y) {
        xcoord = x;
        ycoord = y;
        nodeID = "" + x + y;
    }

    public Node(String id, int x, int y) {
        xcoord = x;
        ycoord = y;
        nodeID = id;
    }

    public Node( String nodeID, int xcoord, int ycoord, String floor, String building, String nodeType, String longName, String shortName) {
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.nodeID = nodeID;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
    }

    public Node(int xcoord, int ycoord, String nodeID, String floor, String building, String nodeType, String longName, String shortName) {
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.nodeID = nodeID;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
    }

    public boolean validateType(String nodeType) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return xcoord == node.xcoord &&
                ycoord == node.ycoord &&
                Objects.equals(nodeID, node.nodeID) &&
                Objects.equals(floor, node.floor) &&
                Objects.equals(building, node.building) &&
                Objects.equals(nodeType, node.nodeType) &&
                Objects.equals(longName, node.longName) &&
                Objects.equals(shortName, node.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xcoord, ycoord, nodeID, floor, building, nodeType, longName, shortName);
    }
}
