package controller;

import service.DatabaseService;

public class Controller {

    static DatabaseService dbs;

    static {
        dbs = new DatabaseService();
    }

}
