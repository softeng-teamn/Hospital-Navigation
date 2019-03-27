package controller;

public class CSVController extends Controller {

    // export tables from db to .csv file
    public boolean exportDB() {
        return true;
    }

    // loads .csv files into database
    public boolean loadNodes(String nodePath, String edgePath) {
        return true;
    }

}
