package model;

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

    public void calculateF() {
        this.f = this.g + this.h;
    }

}
