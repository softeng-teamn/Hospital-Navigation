package controller;

import service.DatabaseService;
import service.MismatchedDatabaseVersionException;

import java.sql.SQLException;



public class Controller {

    static DatabaseService dbs;

    static {
        initializeDatabase();
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

    static boolean isAdmin = false;

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
