package service;

import model.Edge;
import model.Node;

import java.sql.Connection;
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

}
