package model;

import service.DatabaseService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class MapNode implements Comparable<MapNode>{

    int g, x, y;
    double h, f;
    MapNode parent;
    Node data;

    int FLOOR_MULTIPLIER = 43;

    public MapNode(int x, int y, Node data) {
        this.x = x;
        this.y = y;
        this.data = data;
        this.g = 99999999;
    }

    /**
     * checks if two MapNodes are equal to each other
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapNode mapNode = (MapNode) o;
        return (x == mapNode.x &&
                y == mapNode.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y,data);
    }

    public Node getData() {
        return this.data;
    }

    public double getF() {
        return this.f = this.g + this.h;
    }

    public MapNode getParent() {
        return this.parent;
    }

    public int getG(){
        return this.g;
    }

    public double getH(){
        return this.h;
    }

    /**
     * calculates the heuristic
     * @param destination
     */
    public void calculateHeuristic(MapNode destination) {
        double dx = (double) destination.x - this.x;
        double dy = (double) destination.y - this.y;
        double dz = destination.getData().getIntFloor() - this.getData().getIntFloor();
        this.h = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0) + Math.pow(FLOOR_MULTIPLIER*dz, 2.0));
    }

    /**
     * calculates G
     * @param nextNode
     */
    public void calculateG(MapNode nextNode) {
        int dx = nextNode.x - this.x;
        int dy = nextNode.y - this.y;
        double dz = nextNode.getData().getIntFloor() - this.getData().getIntFloor();
        this.g = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(FLOOR_MULTIPLIER*dz, 2));
    }

    public void setParent(MapNode n, int cost) {
        this.parent = n;
        this.g = n.g + cost;
    }

    public void setG(int g){
        this.g = g;
    }

    @Override
    public String toString() {
        return "MapNode{" +
                "g=" + g +
                ", x=" + x +
                ", y=" + y +
                ", h=" + h +
                ", f=" + f +
//                ", parent=" + parent +
                ", data=" + data.getNodeID() +
                '}';
    }

    /**
     * compares two nodes
     * @param node
     * @return
     */
    @Override
    public int compareTo(MapNode node) {
        int comparison = Double.compare(this.getF(), node.getF());
//        // this F is greater than node F
//        if(comparison > 0) {
//            return 1;
//        }
//        // this F is less than node F
//        if(comparison < 0){
//            return 1;
//        }
//        // Both are the same!
//        return 0;
        return comparison; // experimental to find
    }
}
