package controller;

import service.DatabaseService;
import service.MismatchedDatabaseVersionException;

import java.sql.SQLException;

public class Controller {

    static DatabaseService dbs;

    static {
        try {
            dbs = DatabaseService.init();
        } catch (SQLException | MismatchedDatabaseVersionException e) {
            e.printStackTrace();
        }
    }

}
