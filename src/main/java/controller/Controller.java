package controller;


import model.JobType;
import model.Node;
import service.DatabaseService;
import model.Edge;
import model.ElevatorCon;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    static boolean isAdmin = false;
    static boolean isEmployee =false;
    static JobType currentJob = JobType.GUEST;

    public static JobType getCurrentJob() { return currentJob; }

    public static void setCurrentJob(JobType currentJob) { Controller.currentJob = currentJob; }

    public static boolean getIsEmployee() { return isEmployee; }

    public static void setIsEmployee(boolean isEmployee) { Controller.isEmployee = isEmployee; }

    static HashMap<String, ArrayList<Node>> connections;
    @SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    static Node nodeToEdit = new Node("BHALL01402",3296,880,"2","45 Francis","HALL","Hallway Intersection 14 Level 2","Hallway B1402");
    static ArrayList<Edge> edgesToEdit;
    //BHALL01402,3296,880,2,45 Francis,HALL,Hallway Intersection 14 Level 2,Hallway B1402
    static ElevatorCon elevatorCon = new ElevatorCon();
    static String floorIsAt = "0";


    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }

    public static void initConnections() {
        System.out.println("creating hashmap ...");
        connections = new HashMap<>();
        ArrayList<Edge> allEdges = DatabaseService.getDatabaseService().getAllEdges();

        for (Edge e : allEdges) {
            if (connections.containsKey(e.getNode1().getNodeID())) {
                connections.get(e.getNode1().getNodeID()).add(e.getNode2());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode2());
                connections.put(e.getNode1().getNodeID(), newList);
            }


            if (connections.containsKey(e.getNode2().getNodeID())) {
                connections.get(e.getNode2().getNodeID()).add(e.getNode1());
            } else {
                ArrayList<Node> newList = new ArrayList<>();
                newList.add(e.getNode1());
                connections.put(e.getNode2().getNodeID(), newList);
            }
        }

        System.out.println("the hashmap is MADE!");
    }

}
