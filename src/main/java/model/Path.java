package model;

import java.util.ArrayList;

public class Path {

    public ArrayList<Node> nodes;
    public int time;

    public Path(ArrayList<Node> nodes, int time) {
        this.nodes = nodes;
        this.time = time;
    }
}
