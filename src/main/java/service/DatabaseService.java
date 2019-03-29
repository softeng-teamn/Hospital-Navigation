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

    public static DatabaseService init(String DBName) throws SQLException{
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        Connection connection;
        boolean createFlag = false;
        try {
            connection = DriverManager.getConnection("jdbc:derby:"+DBName+";");
        } catch (SQLException e) {
            e.printStackTrace();
            connection = DriverManager.getConnection("jdbc:derby:"+DBName+";create=true");
            createFlag = true;
        }

        DatabaseService myDB = new DatabaseService(connection);
        if(createFlag){
            myDB.createTables();
        }
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



    // NODE FUNCTIONS

    // insert a new node into the database without any edges
    public boolean insertNode(Node n){
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement insertNode = null;
        boolean insertStatus = false;
        try {
            insertNode = connection.prepareStatement(nodeStatement);
            // set the attributes of the statement for the node
            prepareNodeStatement(n, insertNode);
            insertNode.execute();
            insertStatus = true;
        } catch (SQLException e) {
            e.printStackTrace();
            insertStatus = false;
        } finally {
            if(insertNode != null){
                try {
                    insertNode.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return insertStatus;
    }

    // edit existing node in database
    public boolean updateNode(Node n) {
        boolean updateResult = false;
        String nodeID = n.getNodeID();
        String floor = n.getFloor();
        String building = n.getBuilding();
        String nodeType = n.getNodeType();
        String longName = n.getLongName();
        String shortName = n.getShortName();
        int xcoord = n.getXcoord();
        int ycoord = n.getYcoord();
        String insertStatement = "UPDATE NODE SET xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=? WHERE (nodeID = ?)";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(insertStatement);
            stmt.setInt(1, xcoord);
            stmt.setInt(2, ycoord);
            stmt.setString(3, floor);
            stmt.setString(4, building);
            stmt.setString(5, nodeType);
            stmt.setString(6, longName);
            stmt.setString(7, shortName);
            stmt.setString(8, nodeID);
            try {
                stmt.executeUpdate();
                updateResult = true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return updateResult;
    }

        // delete existing node in database
    public boolean deleteNode(Node n) {
        return true;
    }

    // retrieves the given node from the database
    public Node getNode(String nodeID){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String input = "SELECT * FROM NODE WHERE (NODEID = ?)";
        Node newNode;

        try {
            stmt = connection.prepareStatement(input);
            stmt.setString(1,nodeID);
            // execute the query
            rs = stmt.executeQuery();

            // extract results, only one record should be found.
            boolean hasNext = rs.next();

            // If there is no next node, return null
            if (!hasNext) {
                return null;
            }

            String newNodeID = rs.getString("nodeID");
            int newxcoord = rs.getInt("xcoord");
            int newycoord = rs.getInt("ycoord");
            String newFloor = rs.getString("floor");
            String newBuilding = rs.getString("building");
            String newNodeType = rs.getString("nodeType");
            String newLongName = rs.getString("longName");
            String newShortName = rs.getString("shortName");
            // construct the new node and return it
            newNode = new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
            stmt.close();
            rs.close();
            return newNode;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(stmt, rs);
        }
        return null;
    }



    // get all nodes from the specified floor
    public Collection<Node> getNodes(String floor) {
        ArrayList<Node> n = new ArrayList<>();
        return n;
    }

    // EDGE FUNCTIONS

    // get edges from a specific floor
    public static Collection<Edge> getEdges(int floor) {

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

    public boolean insertEdge(Edge e){
        return true;
    }

    //public Edge retrieveEdge(String EdgeID){
    //    return true;
    //}

    public boolean updateEdge(Edge e){
        return true;
    }

    public boolean deleteEdge(Edge e){
        return true;
    }

    // EMPLOYEE FUNCTIONS
    // oh what the heck
    // gigantic ugh we're missing stuff



    // CONTROLS
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

    private void closeAll(Statement stmt, ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // delete all from each table. Almost exclusively used for testing.
    public void wipeTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DELETE FROM NODE");
            statement.execute("DELETE FROM EDGE");
            statement.close();
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

    private void prepareNodeStatement(Node n, PreparedStatement insertNode) throws SQLException {
        insertNode.setString(1,n.getNodeID());
        insertNode.setInt(2,n.getXcoord());
        insertNode.setInt(3,n.getYcoord());
        insertNode.setString(4,n.getFloor());
        insertNode.setString(5,n.getBuilding());
        insertNode.setString(6,n.getNodeType());
        insertNode.setString(7,n.getLongName());
        insertNode.setString(8,n.getShortName());
    }




}
