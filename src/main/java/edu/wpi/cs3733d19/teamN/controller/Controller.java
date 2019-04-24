package edu.wpi.cs3733d19.teamN.controller;


import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import edu.wpi.cs3733d19.teamN.employee.model.JobType;
import edu.wpi.cs3733d19.teamN.map.Node;

import java.util.ArrayList;
import java.util.HashMap;

/** old class used to control applicationstate, replaced by {@link ApplicationState}
 * @deprecated
 */
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
