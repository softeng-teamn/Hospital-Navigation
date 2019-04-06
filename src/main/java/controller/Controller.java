package controller;


import model.JobType;
import model.Node;

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

    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }
}
