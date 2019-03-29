package service;

import model.Edge;
import model.Node;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseService {

    private Connection connection;

    public DatabaseService(Connection connection) {
        this.connection = connection;
    }

    public static DatabaseService init(String DBName) throws SQLException {
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        Connection connection = DriverManager.getConnection("jdbc:derby:"+DBName+";create=true");
        DatabaseService myDB = new DatabaseService(connection);
        myDB.createTables();
        return myDB;
    }

    public static DatabaseService init() throws SQLException{
        DatabaseService myDB = init("hospital-db");
        return myDB;
    }

    // create tables in the database if they do not already exist.
    private void createTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // Check to see if the tables have already been created, if they have, do not create them a second time.
            if(!tableExists("NODE")){
                statement.execute("CREATE TABLE NODE (nodeID varchar(255) PRIMARY KEY, xcoord int, ycoord int, floor varchar(255), building varchar(255), nodeType varchar(255), longName varchar(255), shortName varchar(255))");
            }
            if(!tableExists("EDGE")){
                statement.execute("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(10), node2 varchar(10))");
            }
            if(!tableExists("EMPLOYEE")){
                statement.execute("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, job varchar(25), isAdmin boolean)");
            }
            if(!tableExists("SERVICEREQUEST")){
                statement.execute("CREATE TABLE SERVICEREQUEST(serviceID int PRIMARY KEY, serviceType varchar(4), locationNode varchar(10), description varchar(300), requestorID int, fulfillerID int)");
            }
            if(!tableExists("RESERVEDEVENT")){
                statement.execute("CREATE TABLE RESERVEDEVENT(eventID int PRIMARY KEY, eventName varchar(50), locationID varchar(30), startTime int, endTime int, privacyLevel int, employeeID int)");
            }
            if(!tableExists("RESERVABLESPACE")){
                statement.execute("CREATE TABLE RESERVABLESPACE(spaceID varchar(30) PRIMARY KEY , spaceName varchar(50), spaceType varchar(4), locationNode varchar(10), timeOpen timestamp, timeClosed timestamp)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }





    }

    // add node and edges objects to tables
    public boolean addNode(Node n, Collection<Edge> e) {
        return true;
    }

    // edit existing node in database
    public boolean editNode(Node n) {

        return true;
    }

    // delete existing node in database
    public boolean deleteNode(Node n) {
        return true;
    }

    // get all nodes from the specified floor
    public Collection<Node> getNodes(String floor) {
        ArrayList<Node> n = new ArrayList<>();
        return n;
    }

    // get edges from a specific floor
    public static Collection<Edge> getEdges(String floor) {

        ArrayList<Edge> edges = new ArrayList<>();
        Node a = new Node(0,0);
        Node b = new Node(0,1);
        Node c = new Node(1,1);
        Node d = new Node(2,0);
        Node e = new Node(2,2);
        Node f = new Node(3,2);
        Node g = new Node(3,3);
        edges.add(new Edge(a, b));
        edges.add(new Edge(b, c));
        edges.add(new Edge(c, d));
        edges.add(new Edge(c, e));
        edges.add(new Edge(e, f));
        edges.add(new Edge(d, f));
        edges.add(new Edge(f, g));

        return edges;

    }

    //oi sam how the f does this work
    boolean tableExists(String table){
        DatabaseMetaData dbm;
        try {
            dbm = connection.getMetaData();
            ResultSet rs = dbm.getTables(null, null, table, null);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
