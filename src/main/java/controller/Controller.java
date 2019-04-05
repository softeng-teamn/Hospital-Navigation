package controller;


import model.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    static boolean isAdmin = false;
    static HashMap<String, ArrayList<Node>> connections;

    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }
}
