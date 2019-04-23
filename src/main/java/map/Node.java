package map;

import java.util.Objects;

/**
 * Node that represents a location on the map
 */
public class Node {

    private int xcoord, ycoord;
    private String nodeID, floor, building, nodeType, longName, shortName;

    private boolean isClosed;

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

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

    /** gets the floor the node is on as an int
     * @return an int based on what floor the node is on
     */
    public int getIntFloor() {

        if (floor.equals("L2")){
            return -2;
        }
        else if (floor.equals("L1")){
            return -1;
        }
        else if (floor.equals("G")){
            return 0;
        }
        else{
            return Integer.parseInt(floor);
        }
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
                "xcoord=" + xcoord +
                ", ycoord=" + ycoord +
                ", nodeID='" + nodeID + '\'' +
                ", floor='" + floor + '\'' +
                ", building='" + building + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", longName='" + longName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isClosed=" + isClosed +
                '}';
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @param x the x-coordinate of the node
     * @param y the y-coordinate of the node
     */
    public Node(int x, int y) {
        xcoord = x;
        ycoord = y;
        nodeID = "" + x + y;
        isClosed = false;
    }

    public Node(String id, int x, int y) {
        xcoord = x;
        ycoord = y;
        nodeID = id;
        isClosed = false;
    }

    public Node(String nodeID, int xcord, int ycord, String nodeType){
        this.nodeID = nodeID;
        this.xcoord = xcord;
        this.ycoord = ycord;
        this.nodeType = nodeType;
        this.isClosed = false;
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
        this.isClosed = false;
    }

    /**
     * @param xcoord the x-coordinate of the node
     * @param ycoord the y-coordinate of the node
     * @param nodeID the desired ID of the node
     * @param floor the floor the node is on
     * @param building the building the node is in
     * @param nodeType the getType of node this node is
     * @param longName the long name of the node, used on the display
     * @param shortName the abbreviated name of the node, used internally
     */
    public Node(int xcoord, int ycoord, String nodeID, String floor, String building, String nodeType, String longName, String shortName) {
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.nodeID = nodeID;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
        this.isClosed = false;
    }

    /**
     * ensures nodeType is a string
     * @param nodeType the getType of node to check
     * @return true
     */
    public boolean validateType(String nodeType) {
        return true;
    }

    /**
     * checks if two nodes are equal to each other.
     * @param o the node to compare to another
     * @return true if they are equal and false if otherwise
     */
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
                Objects.equals(shortName, node.shortName) &&
                isClosed == node.isClosed;
    }

    /**
     * node hash code
     * @return a hashcode generated from the given parameters
     */
    @Override
    public int hashCode() {
        return Objects.hash(xcoord, ycoord, nodeID, floor, building, nodeType, longName, shortName);
    }
}
