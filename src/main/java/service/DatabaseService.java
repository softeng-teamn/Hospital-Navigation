package service;

import model.Edge;
import model.Node;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseService {

    private Connection connection;

    private DatabaseService(Connection connection) {
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
        return init("hospital-db");
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
                statement.execute("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(255), node2 varchar(255))");
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
            statement.execute("ALTER TABLE EDGE ADD FOREIGN KEY (node1) REFERENCES NODE(nodeID)");
            statement.execute("ALTER TABLE EDGE ADD FOREIGN KEY (node2) REFERENCES NODE(nodeID)");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }



    // NODE FUNCTIONS

    /**
     * Attempt to insert a node into the database. Will not succeed if n.nodeID is not unique
     * @param n A {@link Node} to insert into the database
     * @return true if the node is successfully inserted, false otherwise.
     */
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
        } finally {
            closeStatement(insertNode);
        }
        return insertStatus;
    }

    /**
     * Update the database entry for a given node
     * @param n A {@link Node} to update. The node must have a valid ID
     * @return true if the update is successful, false otherwise
     */
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
            prepareStatement(stmt, xcoord, ycoord, floor, building, nodeType, longName, shortName, nodeID);
            try {
                stmt.executeUpdate();
                updateResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(stmt);
        }
        return updateResult;
    }

    /**
     * Delete a node if it exists
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    public boolean deleteNode(Node n) {
        PreparedStatement stmt = null;
        String nodeID = n.getNodeID();
        String query = "DELETE FROM NODE WHERE (nodeID = ?)";
        boolean deleteStatus = false;
        try {
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, nodeID);

            stmt.executeUpdate();
            deleteStatus = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(stmt);

        }
        return deleteStatus;
    }

    // retrieves the given node from the database
    public Node getNode(String nodeID){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String input = "SELECT * FROM NODE WHERE (NODEID = ?)";
        Node newNode;

        try {
            stmt = connection.prepareStatement(input);

            prepareStatement(stmt, nodeID);

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

    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<Node>();
        String query = "Select * FROM NODE";
        Statement stmt = null;
        ResultSet nodes = null;
        try{
            stmt = connection.createStatement();

            // execute the query
            nodes = stmt.executeQuery(query);
            while(nodes.next()){
                // extract results from each row of the database.
                String newNodeID = nodes.getString("nodeID");
                int newxcoord = nodes.getInt("xcoord");
                int newycoord = nodes.getInt("ycoord");
                String newFloor = nodes.getString("floor");
                String newBuilding = nodes.getString("building");
                String newNodeType = nodes.getString("nodeType");
                String newLongName = nodes.getString("longName");
                String newShortName = nodes.getString("shortName");
                // construct the new node and return it
                Node newNode = new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
                allNodes.add(newNode);
            }
            stmt.close();
            nodes.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeAll(stmt, nodes);
        }

        return allNodes;
    }



    // get all nodes from the specified floor
    public Collection<Node> getNodes(String floor) {
        ArrayList<Node> n = new ArrayList<>();
        return n;
    }

    // EDGE FUNCTIONS
    // returns a list of nodes that are connected to the given node
    public ArrayList<Node> getNodesConnectedTo(Node n) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String nodeID = n.getNodeID();
        String input = "SELECT NODE.NodeID, NODE.xcoord, NODE.ycoord, NODE.floor, NODE.building, NODE.nodeType, NODE.longName, NODE.shortName FROM NODE INNER JOIN EDGE ON (NODE.NodeID = EDGE.node1 AND EDGE.node2 = ?) OR (NODE.NodeID = EDGE.node2 AND EDGE.Node1 = ?)";
        ArrayList<Node> connectedNodes = new ArrayList<Node>();
        try {
            stmt = connection.prepareStatement(input);
            prepareStatement(stmt,nodeID,nodeID);
            rs = stmt.executeQuery();
            while(rs.next()){
                // extract results from each row of the database.
                String newNodeID = rs.getString("nodeID");
                int newxcoord = rs.getInt("xcoord");
                int newycoord = rs.getInt("ycoord");
                String newFloor = rs.getString("floor");
                String newBuilding = rs.getString("building");
                String newNodeType = rs.getString("nodeType");
                String newLongName = rs.getString("longName");
                String newShortName = rs.getString("shortName");
                // construct the new node and return it
                Node newNode = new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
                connectedNodes.add(newNode);;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(stmt, rs);
        }
        return connectedNodes;
    }

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

    public String getRoomSched(String day, String roomID){
        return "";
    }

    public boolean bookRoom(String day, String roomID, String time){
        return false;
    }

    public void addRequest(String Request){}

    public boolean login(String id, String password){
        return false;
    }

    // insert an edge. The method will fail and return false if the two nodes it points to
    // do not already exist in the database.
    public boolean insertEdge(Edge e){
        String insertStatement = ("INSERT INTO EDGE VALUES(?,?,?)");
        PreparedStatement statement = null;
        String node1ID = e.getNode1().getNodeID();
        String node2ID = e.getNode2().getNodeID();
        boolean returnValue = false;
        try {
            statement = connection.prepareStatement(insertStatement);

            prepareStatement(statement, e.getEdgeID(), node1ID, node2ID);

            statement.execute();
            returnValue = true;
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            closeStatement(statement);
        }

        return returnValue;
    }

    // get an edge. This also pulls out the nodes that edge connects.
    public Edge getEdge(String edgeID){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String input = "SELECT * FROM EDGE WHERE (EDGEID = ?)";
        Edge newEdge;
        try {
            stmt = connection.prepareStatement(input);

            prepareStatement(stmt, edgeID);

            rs = stmt.executeQuery();

            // extract results, only one record should be found.
            boolean hasNext = rs.next();

            // If there is no next node, return null
            if (!hasNext) {
                return null;
            }
            String newEdgeID = rs.getString("edgeID");
            String node1Name = rs.getString("NODE1");
            String node2Name = rs.getString("NODE2");
            Node node1 = getNode(node1Name);
            Node node2 = getNode(node2Name);
            newEdge = new Edge (newEdgeID, node1, node2);
            return newEdge;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(stmt, rs);
        }
        return null;

    }

    public boolean updateEdge(Edge e){
        boolean updateResult = false;
        String edgeID = e.getEdgeID();
        String node1 = e.getNode1().getNodeID();
        String node2 = e.getNode2().getNodeID();
        String updateStatement = "UPDATE EDGE SET edgeID=?, NODE1=?, NODE2=? WHERE(EDGEID = ?)";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(updateStatement);

            prepareStatement(stmt, edgeID, node1, node2, edgeID);

            try {
                stmt.executeUpdate();
                updateResult = true;
            } catch (SQLException e1) {
                System.out.println(e1.getMessage());
                e1.printStackTrace();
            }
            stmt.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            closeStatement(stmt);
        }
        return updateResult;
    }

    public boolean deleteEdge(Edge e){
        PreparedStatement stmt = null;
        String edgeID = e.getEdgeID();
        String query = "DELETE FROM EDGE WHERE (edgeID = ?)";
        boolean deleteStatus = false;
        try {
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, edgeID);

            stmt.executeUpdate();
            deleteStatus = true;
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            closeStatement(stmt);
        }
        return deleteStatus;
    }

    public ArrayList<Edge> getAllEdges(){
        return new ArrayList<Edge>();
    }



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
        closeStatement(stmt);
    }

    // delete all from each table. Almost exclusively used for testing.
    public void wipeTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DELETE FROM EDGE");
            statement.execute("DELETE FROM NODE");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }

    private void prepareNodeStatement(Node n, PreparedStatement insertNode) throws SQLException {
        prepareStatement(insertNode, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName());
    }

    /**
     * Set the values of a prepared statement. The number of variables in the prepared statement and the number of
     * values must match.
     * @param preparedStatement the prepared statement to prepare
     * @param values the values to insert
     * @throws SQLException there is a mismatch in number of variables or there is a database access error
     */
    private void prepareStatement(PreparedStatement preparedStatement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setObject(i + 1, values[i]);
        }
    }

    /**
     * Attempt to close a statement
     * @param statement the statement to close. Null is handled
     */
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
