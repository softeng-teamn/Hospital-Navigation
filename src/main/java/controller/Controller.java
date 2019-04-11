package controller;


import employee.model.JobType;
import map.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    static boolean isAdmin = false;

    static JobType currentJob = JobType.GUEST;

    public static void setCurrentJob(JobType currentJob) { Controller.currentJob = currentJob; }

    static HashMap<String, ArrayList<Node>> connections;
    @SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")

    //BHALL01402,3296,880,2,45 Francis,HALL,Hallway Intersection 14 Level 2,Hallway B1402

    static String floorIsAt = "0";


    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }
}
