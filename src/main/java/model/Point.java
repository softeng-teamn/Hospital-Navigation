package model;

public class Point {

    private double weight;
    private int x, y;
    private String nodeID;
    private Point next;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public Point getNext() {
        return next;
    }

    public void setNext(Point next) {
        this.next = next;
    }

    public Point(double weight, int x, int y, String nodeID, Point next) {
        this.weight = weight;
        this.x = x;
        this.y = y;
        this.nodeID = nodeID;
        this.next = next;
    }
}
