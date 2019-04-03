package controller;

import model.Elevator;
import model.Node;
import service.DatabaseService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Controller {
    static Elevator elev;
    static HashMap<String, ArrayList<Node>> connections;

    static {
        // init node hash map
//        initConnections();
//        initializeElevator();
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
        ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();
        for (Node n : allNodes) {
            connections.put(n.getNodeID(), DatabaseService.getDatabaseService().getNodesConnectedTo(n));
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
}
