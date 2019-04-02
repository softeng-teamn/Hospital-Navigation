package controller;

import service.DatabaseService;
import service.MismatchedDatabaseVersionException;

import java.sql.SQLException;

public class Controller {

    static DatabaseService dbs;

    static {
        initializeDatabase();
    }

    public static void initializeDatabase() {
        try {
            dbs = DatabaseService.init();
        } catch (SQLException | MismatchedDatabaseVersionException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAdmin = false;

    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }

    public static void closeDatabase() {
        dbs.close();
    }


    public static void wipeTables() {
        dbs.wipeTables();
    }

}
