package service;

import model.Edge;
import model.Node;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseService {

    private static Connection connection;

    // initialize db
    public boolean init() {
        return true;
    }

    // add node and edges objects to tables
    public boolean addNode(Node n, Collection<Edge> e) {
        return true;
    }

    // edit existing node in database
    public boolean editNode(Node n) {
        return true;
    }

    // delete existing node in database
    public boolean deleteNode(Node n) {
        return true;
    }

    // get all nodes from the specified floor
    public Collection<Node> getNodes(String floor) {
        ArrayList<Node> n = new ArrayList<>();
        return n;
    }

    // get edges from a specific floor
    public static Collection<Edge> getEdges(int floor) {

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

        return edges;

    }

    public ArrayList<Node> getAllNodes() {
        return null;
    }
}
