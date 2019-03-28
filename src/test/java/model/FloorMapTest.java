package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FloorMapTest {

    Collection<Edge> edges;

    @Before
    public void genEdges() {
        ArrayList<Edge> edges = new ArrayList<>();
        Node a = new Node(0,0);
        Node b = new Node(0,1);
        Node c = new Node(1,1);
        Node d = new Node(2,0);
        Node e = new Node(2,2);
        Node f = new Node(3,2);
        Node g = new Node(3,3);
        edges.add(new Edge(a, b));
        edges.add(new Edge(b, c));
        edges.add(new Edge(c, d));
        edges.add(new Edge(c, e));
        edges.add(new Edge(e, f));
        edges.add(new Edge(d, f));
        edges.add(new Edge(f, g));
        this.edges = edges;
    }

    @Test
    public void buildFloorMap() {
        FloorMap fm = new FloorMap(1);

        HashMap<String, Point> hm = fm.getfMap();
        Collection<String> keys = hm.keySet();
        for (String s : keys) {
            System.out.print("[" + s + "]");
            Point p = hm.get(s);
            while (p.getNext() != null) {
                System.out.print(" -> " + p.getNodeID());
                p = p.getNext();
            }
            System.out.print(" -> " + p.getNodeID());
            System.out.println();
        }

    }

}
