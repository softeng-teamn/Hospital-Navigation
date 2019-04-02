package controller;

import service.DatabaseService;
import service.MismatchedDatabaseVersionException;

import java.sql.SQLException;

public class Controller {

    static DatabaseService dbs;

    public static boolean isAdmin = false;

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

    static boolean isAdmin = false;

    public static void closeDatabase() {
        dbs.close();
    }

}
