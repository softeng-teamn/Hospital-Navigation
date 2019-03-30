package controller;

import service.DatabaseService;

import java.sql.SQLException;

public class Controller {

    static DatabaseService dbs;

    static {
        try {
            dbs = DatabaseService.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
