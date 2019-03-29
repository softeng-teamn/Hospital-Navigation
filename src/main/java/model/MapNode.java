package model;

import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

public class MapNode {

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

    @Override
    public int hashCode() {
        return Objects.hash(g, x, y, h, f, parent, data);
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

    private void setParent(MapNode n, int cost) {
        this.parent = n;
        this.g = n.g + cost;
    }

    public void checkBetter(MapNode n, int cost) {
        if (this.g > n.g + cost) {
            setParent(n, cost);
        }
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

}
