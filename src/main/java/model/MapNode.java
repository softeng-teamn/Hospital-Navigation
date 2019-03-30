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

    public MapNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.g = 99999999;
    }

    public MapNode(int x, int y, Node data) {
        this.x = x;
        this.y = y;
        this.data = data;
        this.g = 99999999;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapNode mapNode = (MapNode) o;
        return (x == mapNode.x &&
                y == mapNode.y);
    }

    public MapNode getParent() {
        return this.parent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y,data);
    }

    public Node getData() {
        return this.data;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getF() {
        return this.f = this.g + this.h;
    }

    public void calculateHeuristic(MapNode destination) {
        double dx = (double) destination.x - this.x;
        double dy = (double) destination.y - this.y;
        this.h = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0));
    }

    public void calculateG(MapNode nextNode) {
        int dx = nextNode.x - this.x;
        int dy = nextNode.y - this.y;
        this.g = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public void setParent(MapNode n, int cost) {
        this.parent = n;
        this.g = n.g + cost;
    }

    public void checkBetter(MapNode n, int cost) {
        if (this.g > n.g + cost) {
            setParent(n, cost);
        }
    }

    public int getG(){
        return this.g;
    }

    public void setG(int g){
        this.g = g;
    }

    public double getH(){
        return this.h;
    }

    @Override
    public String toString() {
        return "MapNode{" +
                "g=" + g +
                ", x=" + x +
                ", y=" + y +
                ", h=" + h +
                ", f=" + f +
                ", parent=" + parent +
                ", data=" + data +
                '}';
    }

    @Override
    public int compareTo(MapNode node) {
        if(this.getF() > node.getF()) {
            return 1;
        }
        if(this.getF() < node.getF()){
            return 1;
        }
        else {
            return 0;
        }
    }
}
