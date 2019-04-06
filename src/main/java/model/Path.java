package model;

import java.util.ArrayList;

public class Path {

    public ArrayList<Node> nodes;
    public double time;

    public Path(ArrayList<Node> nodes, double time) {
        this.nodes = nodes;
        this.time = time;
    }
}
