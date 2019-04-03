package controller;

import model.Elevator;
import model.Node;
import service.DatabaseService;
import service.MismatchedDatabaseVersionException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Controller {

    static DatabaseService dbs;

    static Elevator elev;
    static HashMap<String, ArrayList<Node>> connections;

    static {
        initializeDatabase();
        initializeElevator();
    }

    /**
     * initializes the Database
     */
    public static void initializeDatabase() {
        try {
            dbs = DatabaseService.init();
        } catch (SQLException | MismatchedDatabaseVersionException e) {
            e.printStackTrace();
        }
    }

    public static void initializeElevator() {
        try {
            elev = Elevator.get("MyRobotName");
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
  
    public static void initConnections() {
        System.out.println("creating hashmap ...");
        connections = new HashMap<String, ArrayList<Node>>();
        ArrayList<Node> allNodes = dbs.getAllNodes();
        for (Node n : allNodes) {
            connections.put(n.getNodeID(), dbs.getNodesConnectedTo(n));
        }
        System.out.println("the hashmap is MADE!");
    }

    static boolean isAdmin = false;

    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }

    /**
     * closes the database
     */
    public static void closeDatabase() {
        dbs.close();
    }

    /**
     * empties all entries from tables in the database, used for testing.
     */
    public static void wipeTables() {
        dbs.wipeTables();
    }

}
