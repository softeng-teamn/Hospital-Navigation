package service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.xdrop.fuzzywuzzy.Extractor;
import model.*;

import model.request.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Function;

public class DatabaseService {

    public static final String DATABASE_NAME = "hospital-db";
    public static final Integer DATABASE_VERSION = 7;
    private static DatabaseService _dbs;

    private Connection connection;
    private ArrayList<Function<Void, Void>> nodeCallbacks;
    private ArrayList<Function<Void, Void>> edgeCallbacks;
    private boolean createFlag;

    private static class SingletonHelper {
        private static final DatabaseService dbs = new DatabaseService();
    }

    public static DatabaseService getDatabaseService() {
        return SingletonHelper.dbs;
    }

    /**
     * Construct a DatabaseService
     *
     * @throws SQLException on DB connection creation error
     */
    private DatabaseService() {
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            createFlag = false;

            // Start by trying to open connection with existing database
            Connection conn;
            conn = openConnection(false);

            if (conn == null) { // No database exists on disk, so create a new one
                try {
                    conn = openConnection(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                createFlag = true;
            }

            if (!createFlag) {
                this.connection = conn;
                boolean valid = validateVersion();
                if (!valid) { // Not valid. Nuke it and try again
                    conn.close();
                    this.connection = null;

                    // Open a connection to issue shutdown
                    Connection closeConnection = DriverManager.getConnection(
                            "jdbc:derby:" + DATABASE_NAME + ";shutdown=true");
                    closeConnection.close();

                    // Nuke files
                    wipeOutFiles();

                    // Open a new connection allowing creation of database
                    conn = openConnection(true);

                    createFlag = true;
                }
            }

            this.connection = conn;

            if (createFlag) {
                this.createTables();
            }

            nodeCallbacks = new ArrayList<>();
            edgeCallbacks = new ArrayList<>();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection openConnection(boolean allowCreate) throws SQLException {
        try {
            if (!allowCreate) {
                return DriverManager.getConnection("jdbc:derby:" + DATABASE_NAME + ";");
            } else {
                return DriverManager.getConnection("jdbc:derby:" + DATABASE_NAME + ";create=true");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Database '" + DATABASE_NAME + "' not found")) { // Expected issue: no existing DB
                return null;
            } else { // Unexpected issue, throw it
                throw e;
            }
        }
    }

    public void loadFromCSVsIfNecessary() {
        if (createFlag) {
            CSVService.importNodes();
            CSVService.importEdges();
            CSVService.importEmployees();
            CSVService.importReservableSpaces();
        }
    }

    /**
     * Delete DB Files
     */
    private static void wipeOutFiles(File f) {
        if (f == null) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null)
                for (File c : children)
                    wipeOutFiles(c);
        }
        boolean deleted = f.delete();
        if (!deleted)
            System.err.println("File not deleted: " + f.getPath());
    }

    public static void wipeOutFiles() {
        if (_dbs != null) {
            _dbs.close();
        }
        wipeOutFiles(new File(DATABASE_NAME));
    }

    /**
     *
     */
    private boolean validateVersion() {
        String query = "SELECT * FROM META_DB_VER";

        ResultSet rs = null;
        Statement versionStatement = null;
        try {
            versionStatement = connection.createStatement();

            // Loaded db has no version table, invalid
            try {
                rs = versionStatement.executeQuery(query);
            } catch (SQLSyntaxErrorException e) {
                closeAll(versionStatement, rs);
                return false;
            }

            boolean hasNext = rs.next();

            // If no version table entry exists, invalid
            if (!hasNext) {
                closeAll(versionStatement, rs);
                return false;
            }

            int existingVersion = rs.getInt("version");

            // Version entry doesn't match out version, invalid
            if (existingVersion != getDatabaseVersion()) {
                closeAll(versionStatement, rs);
                return false;
            }

            rs.close();
            versionStatement.close();
        } catch (SQLException e) {
            closeAll(versionStatement, rs);
        }

        return true;
    }

    /**
     * creates tables in the database if they do not already exist.
     */
    private void createTables() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // Check to see if the tables have already been created, if they have, do not create them a second time.
            statement.addBatch("CREATE TABLE NODE (nodeID varchar(255) PRIMARY KEY, xcoord int, ycoord int, floor varchar(255), building varchar(255), nodeType varchar(255), longName varchar(255), shortName varchar(255))");

            statement.addBatch("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(255), node2 varchar(255))");

            statement.addBatch("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, username varchar(255) UNIQUE, job varchar(25), isAdmin boolean, password varchar(50), CONSTRAINT chk_job CHECK (job IN ('ADMINISTRATOR', 'DOCTOR', 'NURSE', 'JANITOR', 'SECURITY_PERSONNEL', 'MAINTENANCE_WORKER')))");

            statement.addBatch("CREATE TABLE RESERVATION(eventID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), eventName varchar(50), spaceID varchar(30), startTime timestamp, endTime timestamp, privacyLevel int, employeeID int)");
            statement.addBatch("CREATE TABLE RESERVABLESPACE(spaceID varchar(30) PRIMARY KEY, spaceName varchar(50), spaceType varchar(4), locationNode varchar(10), timeOpen timestamp, timeClosed timestamp)");

            statement.addBatch("CREATE TABLE ITREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, type varchar(30))");
            statement.addBatch("CREATE TABLE MEDICINEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, medicineType varchar(50), quantity double)");
            statement.addBatch("CREATE TABLE FLORISTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, bouquetType varchar(255), quantity int)");
            statement.addBatch("CREATE TABLE SECURITYREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, urgency varchar(30))");
            statement.addBatch("CREATE TABLE SANITATIONREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, urgency varchar(255), materialState varchar(255))");
            statement.addBatch("CREATE TABLE RELIGIOUSREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),notes varchar(225), locationNodeID varchar (255), completed boolean, religion varchar (30))");
            statement.addBatch("CREATE TABLE GIFTSTOREREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean,  gType varchar(30), patientName varchar(255))");
            statement.addBatch("CREATE TABLE INTERNALTRANSPORTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar (255), completed boolean, transportType varchar(40))");
            statement.addBatch("CREATE TABLE PATIENTINFOREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, firstName varchar(255), lastName varchar(255), birthDay varchar(255), description varchar(255))");
            statement.addBatch("CREATE TABLE AVSERVICEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, avServiceType varchar(30))");
            statement.addBatch("CREATE TABLE EXTERNALTRANSPORTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, time TIMESTAMP, transportType varchar(30), description varchar(255))");
            statement.addBatch("CREATE TABLE MAINTENANCEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, maintenanceType varchar(30))");
            statement.addBatch("CREATE TABLE TOYREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255),completed boolean, toyName varchar(255))");
            statement.addBatch("CREATE TABLE INTERPRETERREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, language varchar(30))");

            // DATABASE CONSTRAINTS
            statement.addBatch("CREATE TABLE META_DB_VER(id int PRIMARY KEY , version int)");
            statement.addBatch("INSERT INTO META_DB_VER values(0, " + getDatabaseVersion() + ")");
            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node1) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node2) REFERENCES NODE(nodeID)");

            statement.addBatch("ALTER TABLE RESERVATION ADD FOREIGN KEY (employeeID) REFERENCES EMPLOYEE(employeeID)");

            statement.addBatch("ALTER TABLE ITREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID)");
            statement.addBatch("ALTER TABLE MEDICINEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID)");
            statement.addBatch("ALTER TABLE SECURITYREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE FLORISTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE GIFTSTOREREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE SANITATIONREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE RELIGIOUSREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE INTERNALTRANSPORTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE PATIENTINFOREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE INTERPRETERREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE EXTERNALTRANSPORTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE AVSERVICEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID)");
            statement.addBatch("ALTER TABLE MAINTENANCEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID)");
            statement.addBatch("ALTER TABLE TOYREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID)");

            statement.addBatch("CREATE INDEX LocationIndex ON RESERVATION (spaceID)");


            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }

    // NODE FUNCTIONS

    /**
     * Attempt to insert a node into the database. Will not succeed if n.nodeID is not unique
     *
     * @param n A {@link Node} to insert into the database
     * @return true if the node is successfully inserted, false otherwise.
     */
    public boolean insertNode(Node n) {
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        boolean successful = executeInsert(nodeStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName());
        if (successful) executeNodeCallbacks();
        return successful;
    }

    /**
     * Update the database entry for a given node
     *
     * @param n A {@link Node} to update. The node must have a valid ID
     * @return true if the update is successful, false otherwise
     */
    public boolean updateNode(Node n) {
        String query = "UPDATE NODE SET xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=? WHERE (nodeID = ?)";
        boolean successful = executeUpdate(query, n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(),
                n.getLongName(), n.getShortName(), n.getNodeID());
        if (successful) executeNodeCallbacks();
        return successful;
    }

    /**
     * Delete a node if it exists
     *
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    public boolean deleteNode(Node n) {
        String query = "DELETE FROM NODE WHERE (nodeID = ?)";
        boolean successful = executeUpdate(query, n.getNodeID());
        if (successful) executeNodeCallbacks();
        return successful;
    }

    /**
     * retrieves the given node from the database
     *
     * @param nodeID the ID of the node to be retrieved
     * @return a node with the given ID
     */
    public Node getNode(String nodeID) {
        String query = "SELECT * FROM NODE WHERE (NODEID = ?)";
        return (Node) executeGetById(query, Node.class, nodeID);
    }

    public boolean insertAllNodes(List<Node> nodes) {
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement insertStatement = null;

        // Track the status of the insert
        boolean insertStatus = false;

        try {
            // Prep the statement
            insertStatement = connection.prepareStatement(nodeStatement);

            for (int i = 0; i <= nodes.size() / 1000; i++) {
                for (int j = (i * 1000); j < i * 1000 + 1000 && j < nodes.size(); j++) {
                    Node n = nodes.get(j);
                    prepareStatement(insertStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName());
                    insertStatement.addBatch();
                }
                // Execute
                insertStatement.executeBatch();

                executeNodeCallbacks();

                // If we made it this far, we're successful!
                insertStatus = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(insertStatement);
        }
        return insertStatus;
    }

    /**
     * Returns all nodes in the database.
     *
     * @return list of all nodes in the database
     */
    public ArrayList<Node> getAllNodes() {
        String query = "Select * FROM NODE";
        return (ArrayList<Node>) (List<?>) executeGetMultiple(query, Node.class, new Object[]{});
    }

    /**
     * get nodes filtered by specific type
     *
     * @param filterOut the parameter to exclude specific nodes by
     * @return an arraylist of nodes that do not include the specified parameter
     */
    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", justification = "Not a security issue - just add question marks based on number of types to filter out.")
    public ArrayList<Node> getNodesFilteredByType(String... filterOut) {
        String query = "Select * from NODE where NODETYPE not in (";
        StringBuilder builtQuery = new StringBuilder();
        builtQuery.append(query);
        for (int i = 0; i < filterOut.length; i++) {
            builtQuery.append("?,");
        }
        builtQuery.deleteCharAt(builtQuery.lastIndexOf(","));
        builtQuery.append(")");

        return (ArrayList<Node>) (List<?>) executeGetMultiple(builtQuery.toString(), Node.class, (Object[]) filterOut);
    }

    /**
     * get all nodes from the specified floor
     *
     * @param floor the floor to retrieve all nodes from
     * @return an arraylist of all nodes on the given floor.
     */
    public ArrayList<Node> getNodesByFloor(String floor) {
        String query = "Select * FROM NODE WHERE NODE.FLOOR = ?";
        return (ArrayList<Node>) (List<?>) executeGetMultiple(query, Node.class, floor);
    }

    public int getNumNodeTypeByFloor(String nodeType, String floor) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = connection.prepareStatement("SELECT COUNT (*) AS TOTAL FROM NODE WHERE (floor=? AND nodeType=?)");
            prepareStatement(stmt, floor, nodeType);

            // execute the query
            res = stmt.executeQuery();
            int num = -1;
            while (res.next()) {
                num = res.getInt("TOTAL");
            }
            stmt.close();
            res.close();
            return num;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeAll(stmt, res);
        }
    }

    // EDGE FUNCTIONS

    /**
     * returns a list of nodes that are connected to the given node
     *
     * @param n the node to retrieve all nodes connected to from
     * @return A list of all nodes connected to the given node.
     */
    public ArrayList<Node> getNodesConnectedTo(Node n) {
        String nodeID = n.getNodeID();
        String query = "SELECT NODE.NodeID, NODE.xcoord, NODE.ycoord, NODE.floor, NODE.building, NODE.nodeType, NODE.longName, NODE.shortName FROM NODE INNER JOIN EDGE ON (NODE.NodeID = EDGE.node1 AND EDGE.node2 = ?) OR (NODE.NodeID = EDGE.node2 AND EDGE.Node1 = ?)";

        return (ArrayList<Node>) (List<?>) executeGetMultiple(query, Node.class, nodeID, nodeID);
    }

    /**
     * returns all edges on a specific floor
     * not fully implemented yet
     * not really that necessary
     *
     * @param floor
     * @return
     */
    // get edges from a specific floor
    public static Collection<Edge> getEdges(int floor) {
        // DEPRECATED
        return null;
    }

    /**
     * insert an edge. The method will fail and return false if the two nodes it points to
     * do not already exist in the database.
     *
     * @param e the edge to insert
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertEdge(Edge e) {
        String insertStatement = ("INSERT INTO EDGE VALUES(?,?,?)");
        String node1ID = e.getNode1().getNodeID();
        String node2ID = e.getNode2().getNodeID();

        boolean successful = executeInsert(insertStatement, e.getEdgeID(), node1ID, node2ID);
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    /**
     * get an edge. This also pulls out the nodes that edge connects.
     *
     * @param edgeID the ID of the edge to retrieve
     * @return the edge corresponding to the given ID
     */
    public Edge getEdge(String edgeID) {
        String query = "SELECT e.*, n1.nodeID as n1nodeID, n1.xcoord as n1xcoord, n1.ycoord as n1ycoord, n1.floor as n1floor, n1.building as n1building, n1.nodeType as n1nodeType, n1.longName as n1longName, n1.shortName as n1shortName, n2.nodeID as n2nodeID, n2.xcoord as n2xcoord, n2.ycoord as n2ycoord, n2.floor as n2floor, n2.building as n2building, n2.nodeType as n2nodeType, n2.longName as n2longName, n2.shortName as n2shortName FROM EDGE e Join NODE n1 on e.NODE1 = n1.NODEID Join NODE n2 on e.NODE2 = n2.NODEID WHERE (EDGEID = ?)";
        return (Edge) executeGetById(query, Edge.class, edgeID);
    }

    /**
     * updates an edge with new node IDs.
     *
     * @param e the edge to update
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateEdge(Edge e) {
        String query = "UPDATE EDGE SET edgeID=?, NODE1=?, NODE2=? WHERE(EDGEID = ?)";
        boolean successful = executeUpdate(query, e.getEdgeID(), e.getNode1().getNodeID(), e.getNode2().getNodeID(), e.getEdgeID());
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    /**
     * Deletes an edge from the database.
     *
     * @param e edge to delete from the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteEdge(Edge e) {
        String query = "DELETE FROM EDGE WHERE (edgeID = ?)";
        boolean successful = executeUpdate(query, e.getEdgeID());
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    public ArrayList<Edge> getAllEdges() {
        String query = "Select e.*, n1.nodeID as n1nodeID, n1.xcoord as n1xcoord, n1.ycoord as n1ycoord, n1.floor as n1floor, n1.building as n1building, n1.nodeType as n1nodeType, n1.longName as n1longName, n1.shortName as n1shortName, n2.nodeID as n2nodeID, n2.xcoord as n2xcoord, n2.ycoord as n2ycoord, n2.floor as n2floor, n2.building as n2building, n2.nodeType as n2nodeType, n2.longName as n2longName, n2.shortName as n2shortName FROM EDGE e Join NODE n1 on e.NODE1 = n1.NODEID Join NODE n2 on e.NODE2 = n2.NODEID";
        return (ArrayList<Edge>) (List<?>) executeGetMultiple(query, Edge.class, new Object[]{});
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation reservation to insert into the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean insertReservation(Reservation reservation) {
        String insertStatement = ("INSERT INTO RESERVATION(EVENTNAME, spaceID, STARTTIME, ENDTIME, PRIVACYLEVEL, EMPLOYEEID) VALUES(?, ?, ?, ?, ?, ?)");
        return executeInsert(insertStatement, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(), reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId());
    }

    /**
     * retrieves a single reservation from the database with it's ID.
     *
     * @param id id of the reservation to get from the database
     * @return the reservation object corresponding to the ID
     */
    public Reservation getReservation(int id) {
        String query = "SELECT * FROM RESERVATION WHERE (EVENTID = ?)";
        return (Reservation) executeGetById(query, Reservation.class, id);
    }

    /**
     * retrieves all reservations from the database
     *
     * @return a list of all reservations in the database
     */
    public List<Reservation> getAllReservations() {
        String query = "Select * FROM RESERVATION";
        return (List<Reservation>) (List<?>) executeGetMultiple(query, Reservation.class, new Object[]{});
    }

    /**
     * updates a reservation in the database.
     *
     * @param reservation reservation to update in the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateReservation(Reservation reservation) {
        String query = "UPDATE RESERVATION SET eventName=?, spaceID=?, startTime=?, endTime=?, privacyLevel=?, employeeID=? WHERE (eventID = ?)";
        return executeUpdate(query, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId(), reservation.getEventID());
    }

    /**
     * Removes a reservation from the database.
     *
     * @param reservation a reservation object
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteReservation(Reservation reservation) {
        String query = "DELETE FROM RESERVATION WHERE (eventID = ?)";
        return executeUpdate(query, reservation.getEventID());
    }

    /**
     * Query all reservations made for a given {@link ReservableSpace}.
     *
     * @param id the spaceID of the ReservableSpace being requested for
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceId(String id) {
        String query = "SELECT * FROM RESERVATION WHERE (spaceID = ?)";
        return (List<Reservation>) (List<?>) executeGetMultiple(query, Reservation.class, id);
    }

    /**
     * Get all reservations made for the given space ID that fall entirely within {@param from} and {@param to}.
     *
     * @param id   the spaceID of the reservable space being requested for
     * @param from start of the window
     * @param to   end of the window
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {
        String query = "SELECT * FROM RESERVATION WHERE (spaceID = ? and (STARTTIME between ? and ?) and (ENDTIME between ? and ?))";
        System.out.println(id);
        System.out.println("dbs" + from.get(Calendar.YEAR) + " " + from.get(Calendar.MONTH) + " " + from.get(Calendar.DATE) + " " + from.get(Calendar.HOUR));
        System.out.println(to.get(Calendar.YEAR) + " " + to.get(Calendar.MONTH) + " " + to.get(Calendar.DATE) + " " + to.get(Calendar.HOUR));

        return (List<Reservation>) (List<?>) executeGetMultiple(query, Reservation.class, id, from, to, from, to);
    }

    /**
     * @param employee the employee to insert into the database
     * @return true if the insert succeeded or false if otherwise.
     */
    public boolean insertEmployee(Employee employee) {
        String insertStatement = ("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertStatement, employee.getID(), employee.getUsername(), employee.getJob().name(), employee.isAdmin(), employee.getPassword());
    }

    /**
     * @param id the id of the employee to get from the database
     * @return the object belonging to the employee of the given ID
     */
    public Employee getEmployee(int id) {
        String query = "SELECT * FROM EMPLOYEE WHERE (EMPLOYEEID = ?)";
        return (Employee) executeGetById(query, Employee.class, id);
    }

    /**
     * retrieves a list of all employees from the database.
     *
     * @return a list of all employees in the database.
     */
    public List<Employee> getAllEmployees() {
        String query = "Select * FROM EMPLOYEE";
        return (List<Employee>) (List<?>) executeGetMultiple(query, Employee.class, new Object[]{});
    }

    /**
     * @param employee the employee to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE EMPLOYEE SET username=?, job=?, isAdmin=? WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getUsername(), employee.getJob().name(), employee.isAdmin(), employee.getID());
    }

    /**
     * @param employee employee to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM EMPLOYEE WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getID());
    }

    public Employee getEmployeeByUsername(String username) {
        String query = "SELECT * FROM EMPLOYEE WHERE (username = ?)";
        return (Employee) executeGetById(query, Employee.class, username);
    }

    /**
     * @param space space to insert into the database
     * @return true if the insert succeeded and false if otherwise
     */
    public boolean insertReservableSpace(ReservableSpace space) {
        String insertQuery = ("INSERT INTO RESERVABLESPACE VALUES(?, ?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, space.getSpaceID(), space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed());
    }

    /**
     * @param id ID of the space to get from the database
     * @return a reservable space with matching ID to the given one
     */
    public ReservableSpace getReservableSpace(String id) {
        String query = "SELECT * FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return (ReservableSpace) executeGetById(query, ReservableSpace.class, id);
    }

    /**
     * @return a list of all reservable spaces in the database
     */
    public List<ReservableSpace> getAllReservableSpaces() {
        String query = "Select * FROM RESERVABLESPACE";
        return (List<ReservableSpace>) (List<?>) executeGetMultiple(query, ReservableSpace.class, new Object[]{});
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces with any reservation in the given time frame
     */
    public List<ReservableSpace> getBookedReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {
        String query = "Select * From RESERVABLESPACE Where SPACEID In (Select Distinct SPACEID From RESERVATION Where ((STARTTIME <= ? and ENDTIME > ?) or (STARTTIME >= ? and STARTTIME < ?)))";

        return (List<ReservableSpace>) (List<?>) executeGetMultiple(query, ReservableSpace.class, from, from, from, to);
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces without any reservations in the given time frame
     */
    public List<ReservableSpace> getAvailableReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {
        String query = "Select * From RESERVABLESPACE Where SPACEID Not In (Select Distinct SPACEID From RESERVATION Where ((STARTTIME <= ? and ENDTIME > ?) or (STARTTIME >= ? and STARTTIME < ?)))";

        return (List<ReservableSpace>) (List<?>) executeGetMultiple(query, ReservableSpace.class, from, from, from, to);
    }

    /**
     * @param space the reservable space to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateReservableSpace(ReservableSpace space) {
        String query = "UPDATE RESERVABLESPACE SET spaceName=?, spaceType=?, locationNode=?, timeOpen=?, timeClosed=? WHERE (spaceID = ?)";
        return executeUpdate(query, space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed(), space.getSpaceID());
    }

    /**
     * @param space space to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteReservableSpace(ReservableSpace space) {
        String query = "DELETE FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return executeUpdate(query, space.getSpaceID());
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertITRequest(ITRequest req) {
        String insertQuery = ("INSERT INTO ITREQUEST(notes, locationNodeID, completed, type) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getItRequestType().name());
    }

    /**
     * @param id the id of the request to get from the database
     * @return the IT request object with the given ID
     */
    public ITRequest getITRequest(int id) {
        String query = "SELECT * FROM ITREQUEST WHERE (serviceID = ?)";
        return (ITRequest) executeGetById(query, ITRequest.class, id);
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<ITRequest> getAllITRequests() {
        String query = "Select * FROM ITREQUEST";
        return (List<ITRequest>) (List<?>) executeGetMultiple(query, ITRequest.class, new Object[]{});
    }

    /**
     * updates a given IT request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateITRequest(ITRequest req) {
        String query = "UPDATE ITREQUEST SET notes=?, locationNodeID=?, completed=?, type=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getItRequestType().name(), req.getId());
    }

    /**
     * deletes a given IT request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteITRequest(ITRequest req) {
        String query = "DELETE FROM ITREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<ITRequest> getAllIncompleteITRequests() {
        String query = "Select * FROM ITREQUEST WHERE (completed = ?)";
        return (List<ITRequest>) (List<?>) executeGetMultiple(query, ITRequest.class, false);
    }


    /**
     * @param req the request to insert into the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertMedicineRequest(MedicineRequest req) {
        String insertQuery = ("INSERT INTO MEDICINEREQUEST(notes, locationNodeID, completed, medicineType, quantity) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMedicineType(), req.getQuantity());
    }

    /**
     * @param id the id of the medicine request to get
     * @return the medicine request with the given ID
     */
    public MedicineRequest getMedicineRequest(int id) {
        String query = "SELECT * FROM MEDICINEREQUEST WHERE (serviceID = ?)";
        return (MedicineRequest) executeGetById(query, MedicineRequest.class, id);
    }

    /**
     * @return all medicine requests in the database
     */
    public List<MedicineRequest> getAllMedicineRequests() {
        String query = "Select * FROM MEDICINEREQUEST";
        return (List<MedicineRequest>) (List<?>) executeGetMultiple(query, MedicineRequest.class, new Object[]{});
    }

    /**
     * @param req the given request to update
     * @return true if the update succeeded and false if otherwise.
     */
    public boolean updateMedicineRequest(MedicineRequest req) {
        String query = "UPDATE MEDICINEREQUEST SET notes=?, locationNodeID=?, completed=?, medicineType=?, quantity=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMedicineType(), req.getQuantity(), req.getId());
    }

    /**
     * @param req the given request to delete
     * @return true if the delete succeeded and false if otherwise.
     */
    public boolean deleteMedicineRequest(MedicineRequest req) {
        String query = "DELETE FROM MEDICINEREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of all medicine requests that haven't been completed yet
     */
    public List<MedicineRequest> getAllIncompleteMedicineRequests() {
        String query = "Select * FROM MEDICINEREQUEST where (completed = ?)";
        return (List<MedicineRequest>) (List<?>) executeGetMultiple(query, MedicineRequest.class, false);
    }

    /**
     * @param id the ID of the request to retrieve
     * @return
     */
    public FloristRequest getFloristRequest(int id) {
        String query = "SELECT * FROM FLORISTREQUEST WHERE (serviceID = ?)";
        return (FloristRequest) executeGetById(query, FloristRequest.class, id);
    }

    /**
     * @param req a florist request to insert into the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertFloristRequest(FloristRequest req) {
        String insertQuery = ("INSERT INTO FLORISTREQUEST(notes, locationNodeID, completed, bouquetType, quantity) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getBouquetType(), req.getQuantity());
    }

    public boolean updateFloristRequest(FloristRequest req) {
        String query = "UPDATE FLORISTREQUEST SET notes=?, locationNodeID=?, completed=?, bouquetType=?, quantity=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getBouquetType(), req.getQuantity(), req.getId());
    }

    /**
     * @param req the given request to delete
     * @return true if the delete succeeded and false if otherwise.
     */
    public boolean deleteFloristRequest(FloristRequest req) {
        String query = "DELETE FROM FLORISTREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    public List<FloristRequest> getAllFloristRequests() {
        String query = "Select * FROM FLORISTREQUEST";
        return (List<FloristRequest>) (List<?>) executeGetMultiple(query, FloristRequest.class, new Object[]{});
    }

    /**
     * @param id the id of the request to get from the database
     * @return the Security request object with the given ID
     */
    public SecurityRequest getSecurityRequest(int id) {
        String query = "SELECT * FROM SECURITYREQUEST WHERE (serviceID = ?)";
        return (SecurityRequest) executeGetById(query, SecurityRequest.class, id);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertSecurityRequest(SecurityRequest req) {
        String insertQuery = ("INSERT INTO SECURITYREQUEST(notes, locationNodeID, completed, urgency) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency().name());
    }

    /**
     * updates a given Security request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateSecurityRequest(SecurityRequest req) {
        String query = "UPDATE SECURITYREQUEST SET notes=?, locationNodeID=?, completed=?, urgency=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency().name(), req.getId());
    }

    /**
     * deletes a given Security request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteSecurityRequest(SecurityRequest req) {
        String query = "DELETE FROM SECURITYREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return all Security requests stored in the database in a List.
     */
    public List<SecurityRequest> getAllSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST";
        return (List<SecurityRequest>) (List<?>) executeGetMultiple(query, SecurityRequest.class, new Object[]{});
    }

    /**
     * @return a list of every Security request that has not been completed yet.
     */
    public List<SecurityRequest> getAllIncompleteSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST WHERE (completed = ?)";
        return (List<SecurityRequest>) (List<?>) executeGetMultiple(query, SecurityRequest.class, false);
    }

    /**
     * @return a list of every Security request that has not been completed yet.
     */
    public List<SecurityRequest> getAllCompleteSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST WHERE (completed = ?)";
        return (List<SecurityRequest>) (List<?>) executeGetMultiple(query, SecurityRequest.class, true);
    }
    //////////////////////// END REQUEST 2 QUERIES /////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 3 QUERIES ////////////////////////////////////////////////////////////////////////

    /**
     * @param id the id of the request to get from the database
     * @return the sanitation request object with the given ID
     */
    public SanitationRequest getSanitationRequest(int id) {
        String query = "SELECT * FROM SANITATIONREQUEST WHERE (serviceID = ?)";
        return (SanitationRequest) executeGetById(query, SanitationRequest.class, id);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertSanitationRequest(SanitationRequest req) {
        String insertQuery = ("INSERT INTO SANITATIONREQUEST(notes, locationNodeID, completed, urgency, materialState) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency(), req.getMaterialState());
    }

    /**
     * updates a given sanitation request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateSanitationRequest(SanitationRequest req) {
        String query = "UPDATE SANITATIONREQUEST SET notes=?, locationNodeID=?, completed=?, urgency=?, materialState=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency(), req.getMaterialState(), req.getId());
    }

    /**
     * deletes a given sanitation request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteSanitationRequest(SanitationRequest req) {
        String query = "DELETE FROM SANITATIONREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return all sanitation requests stored in the database in a List.
     */
    public List<SanitationRequest> getAllSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST";
        return (List<SanitationRequest>) (List<?>) executeGetMultiple(query, SanitationRequest.class, new Object[]{});
    }

    /**
     * @return a list of every sanitation request that has not been completed yet.
     */
    public List<SanitationRequest> getAllIncompleteSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST WHERE (completed = ?)";
        return (List<SanitationRequest>) (List<?>) executeGetMultiple(query, SanitationRequest.class, false);
    }

    /**
     * @return a list of every sanitation request that has been completed.
     */
    public List<SanitationRequest> getAllCompleteSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST WHERE (completed = ?)";
        return (List<SanitationRequest>) (List<?>) executeGetMultiple(query, SanitationRequest.class, true);
    }

    /**
     * @param id the id of the request to get from the database
     * @return the GiftRequest request object with the given ID
     */
    public GiftStoreRequest getGiftStoreRequest(int id) {
        String query = "SELECT * FROM GIFTSTOREREQUEST WHERE (serviceID = ?)";
        return (GiftStoreRequest) executeGetById(query, GiftStoreRequest.class, id);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertGiftStoreRequest(GiftStoreRequest req) {
        String insertQuery = ("INSERT INTO GIFTSTOREREQUEST(notes, locationNodeID, completed, gType, patientName) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getgType().name(), req.getPatientName());
    }


    /**
     * updates a given GiftStoreRequest request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateGiftStoreRequest(GiftStoreRequest req) {
        String query = "UPDATE GIFTSTOREREQUEST SET notes=?, locationNodeID=?, completed=?, gType=?, patientName=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getgType().name(), req.getPatientName(), req.getId());
    }


    /**
     * deletes a given GiftStoreRequest request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteGiftStoreRequest(GiftStoreRequest req) {
        String query = "DELETE FROM GIFTSTOREREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every GiftStoreRequests request that has not been completed yet.
     */
    public List<GiftStoreRequest> getAllIncompleteGiftStoreRequests() {
        String query = "Select * FROM GIFTSTOREREQUEST WHERE (completed = ?)";
        return (List<GiftStoreRequest>) (List<?>) executeGetMultiple(query, GiftStoreRequest.class, false);
    }

    /**
     * @return a list of every GiftStoreRequests request that has not been completed yet.
     */
    public List<GiftStoreRequest> getAllCompleteGiftStoreRequests() {
        String query = "Select * FROM GIFTSTOREREQUEST WHERE (completed = ?)";
        return (List<GiftStoreRequest>) (List<?>) executeGetMultiple(query, GiftStoreRequest.class, true);
    }

    public ReligiousRequest getReligiousRequest(int id) {
        String query = "SELECT * FROM RELIGIOUSREQUEST WHERE (serviceID = ?)";
        return (ReligiousRequest) executeGetById(query, ReligiousRequest.class, id);
    }


    public boolean insertReligiousRequest(ReligiousRequest req) {
        String insertQuery = ("INSERT INTO RELIGIOUSREQUEST(notes, locationNodeID, completed, religion) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getReligion().name());
    }

    public boolean updateReligiousRequest(ReligiousRequest req) {
        String query = "UPDATE RELIGIOUSREQUEST SET notes=?, locationNodeID=?, completed=?, religion=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getReligion().name(), req.getId());
    }

    public boolean deleteReligiousRequest(ReligiousRequest req) {
        String query = "DELETE FROM RELIGIOUSREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    public List<ReligiousRequest> getAllReligiousRequests() {
        String query = "Select * FROM RELIGIOUSREQUEST";
        return (List<ReligiousRequest>)(List<?>) executeGetMultiple(query, ReligiousRequest.class, new Object[]{});
    }
    public List<ReligiousRequest> getAllIncompleteReligiousRequests() {
        String query = "Select * FROM RELIGIOUSREQUEST WHERE (completed = ?)";
        return (List<ReligiousRequest>) (List<?>) executeGetMultiple(query, ReligiousRequest.class, false);
    }

    public InterpreterRequest getInterpreterRequest(int id) {
        String query = "SELECT * FROM INTERPRETERREQUEST WHERE (serviceID = ?)";
        return (InterpreterRequest) executeGetById(query, InterpreterRequest.class, id);
    }


    public boolean insertInterpreterRequest(InterpreterRequest req) {
        String insertQuery = ("INSERT INTO INTERPRETERREQUEST(notes, locationNodeID, completed, language) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getLanguageType().name());
    }

    public boolean updateInterpreterRequest(InterpreterRequest req) {
        String query = "UPDATE INTERPRETERREQUEST SET notes=?, locationNodeID=?, completed=?, language=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getLanguageType().name(), req.getId());
    }

    public boolean deleteInterpreterRequest(InterpreterRequest req) {
        String query = "DELETE FROM INTERPRETERREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    public List<InterpreterRequest> getAllInterpreterRequests() {
        String query = "Select * FROM INTERPRETERREQUEST";
        return (List<InterpreterRequest>) (List<?>) executeGetMultiple(query, InterpreterRequest.class, new Object[]{});
    }

    public List<InterpreterRequest> getAllIncompleteInterpreterRequests() {
        String query = "Select * FROM INTERPRETERREQUEST WHERE (completed = ?)";
        return (List<InterpreterRequest>) (List<?>) executeGetMultiple(query, InterpreterRequest.class, false);
    }

    /**
     * @param id the id of the request to get from the database
     * @return the controller.PatientInfo request object with the given ID
     */
    public PatientInfoRequest getPatientInfoRequest(int id) {
        String query = "SELECT * FROM PATIENTINFOREQUEST WHERE (serviceID = ?)";
        return (PatientInfoRequest) executeGetById(query, PatientInfoRequest.class, id);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertPatientInfoRequest(PatientInfoRequest req) {
        String insertQuery = ("INSERT INTO PATIENTINFOREQUEST(notes, locationNodeID, completed, firstName, lastName, birthDay, description) VALUES(?, ?, ?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getFirstName(), req.getLastName(), req.getBirthDay(), req.getDescription());
    }

    /**
     * updates a given controller.PatientInfo request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updatePatientInfoRequest(PatientInfoRequest req) {
        String query = "UPDATE PATIENTINFOREQUEST SET notes=?, locationNodeID=?, completed=?, firstName=?, lastName=?, birthDay=?, description=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getFirstName(), req.getLastName(), req.getBirthDay(), req.getDescription(), req.getId());
    }

    /**
     * deletes a given controller.PatientInfo request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deletePatientInfoRequest(PatientInfoRequest req) {
        String query = "DELETE FROM PATIENTINFOREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return all Patient Info requests stored in the database in a List.
     */
    public List<PatientInfoRequest> getAllPatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST";
        return (List<PatientInfoRequest>) (List<?>) executeGetMultiple(query, PatientInfoRequest.class, new Object[]{});
    }

    /**
     * @return a list of every Patient Info request that has not been completed yet.
     */
    public List<PatientInfoRequest> getAllIncompletePatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST WHERE (completed = ?)";
        return (List<PatientInfoRequest>) (List<?>) executeGetMultiple(query, PatientInfoRequest.class, false);
    }

    /**
     * @return a list of every Patient Info request that has been completed.
     */
    public List<PatientInfoRequest> getAllCompletePatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST WHERE (completed = ?)";
        return (List<PatientInfoRequest>) (List<?>) executeGetMultiple(query, PatientInfoRequest.class, true);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertInternalTransportRequest(InternalTransportRequest req) {
        String insertQuery = ("INSERT INTO INTERNALTRANSPORTREQUEST(notes, locationNodeID, completed, transportType) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getTransport().name());
    }

    /**
     * @param id the id of the request to get from the database
     * @return the InternalTransportRequest request object with the given ID
     */
    public InternalTransportRequest getInternalTransportRequest(int id) {
        String query = "SELECT * FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return (InternalTransportRequest) executeGetById(query, InternalTransportRequest.class, id);
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<InternalTransportRequest> getAllInternalTransportRequest() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST";
        return (List<InternalTransportRequest>) (List<?>) executeGetMultiple(query, InternalTransportRequest.class, new Object[]{});
    }

    /**
     * updates a given IT request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateInternalTransportRequest(InternalTransportRequest req) {
        String query = "UPDATE INTERNALTRANSPORTREQUEST SET notes=?, locationNodeID=?, completed=?, transportType=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getTransport().name(), req.getId());
    }

    /**
     * deletes a given IT request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteInternalTransportRequest(InternalTransportRequest req) {
        String query = "DELETE FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<InternalTransportRequest> getAllIncompleteInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<InternalTransportRequest>) (List<?>) executeGetMultiple(query, InternalTransportRequest.class, false);
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<InternalTransportRequest> getAllCompleteInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<InternalTransportRequest>) (List<?>) executeGetMultiple(query, InternalTransportRequest.class, true);
    }


    //////////////////////// END REQUEST 8 QUERIES /////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 9 QUERIES ////////////////////////////////////////////////////////////////////////
    public ExternalTransportRequest getExtTransRequest(int id) {
        String query = "SELECT * FROM EXTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return (ExternalTransportRequest) executeGetById(query, ExternalTransportRequest.class, id);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertExtTransRequest(ExternalTransportRequest req) {
        String insertQuery = ("INSERT INTO EXTERNALTRANSPORTREQUEST(notes, locationNodeID, completed, time, transportType, description) VALUES(?, ?, ?, ?, ?,?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDate(), req.getTransportationType().name(), req.getDescription());
    }


    /**
     * deletes a given IT request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteExtTransRequest(ExternalTransportRequest req) {
        String query = "DELETE FROM EXTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<ExternalTransportRequest> getAllExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST";
        return (List<ExternalTransportRequest>) (List<?>) executeGetMultiple(query, ExternalTransportRequest.class, new Object[]{});
    }

    /**
     * updates a given IT request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateExtTransRequest(ExternalTransportRequest req) {
        String query = "UPDATE EXTERNALTRANSPORTREQUEST SET notes=?, locationNodeID=?, completed=?, time=?, transportType=?, description=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDate(), req.getTransportationType().name(), req.getDescription(), req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<ExternalTransportRequest> getAllIncompleteExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<ExternalTransportRequest>) (List<?>) executeGetMultiple(query, ExternalTransportRequest.class, false);
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<ExternalTransportRequest> getAllCompleteExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<ExternalTransportRequest>) (List<?>) executeGetMultiple(query, MaintenanceRequest.class, true);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertAVServiceRequest(AVServiceRequest req) {
        String insertQuery = ("INSERT INTO AVSERVICEREQUEST(notes, locationNodeID, completed, avServiceType) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getAVServiceType().name());
    }

    /**
     * @param id the id of the request to get from the database
     * @return the IT request object with the given ID
     */
    public AVServiceRequest getAVServiceRequest(int id) {
        String query = "SELECT * FROM AVSERVICEREQUEST WHERE (serviceID = ?)";
        return (AVServiceRequest) executeGetById(query, AVServiceRequest.class, id);
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<AVServiceRequest> getAllAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST";
        return (List<AVServiceRequest>)(List<?>) executeGetMultiple(query, AVServiceRequest.class, new Object[]{});
    }

    /** updates a given IT request in the database.
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateAVServiceRequest(AVServiceRequest req) {
        String query = "UPDATE AVSERVICEREQUEST SET notes=?, locationNodeID=?, completed=?, avServiceType=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getAVServiceType().name(), req.getId());
    }

    /** deletes a given IT request from the database
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteAVServiceRequest(AVServiceRequest req) {
        String query = "DELETE FROM AVSERVICEREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<AVServiceRequest> getAllIncompleteAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST WHERE (completed = ?)";
        return (List<AVServiceRequest>)(List<?>) executeGetMultiple(query, AVServiceRequest.class, false);
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<AVServiceRequest> getAllCompleteAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST WHERE (completed = ?)";
        return (List<AVServiceRequest>)(List<?>) executeGetMultiple(query, AVServiceRequest.class, true);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertMaintenanceRequest(MaintenanceRequest req) {
        String insertQuery = ("INSERT INTO MAINTENANCEREQUEST(notes, locationNodeID, completed, maintenanceType) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMaintenanceType().name());
    }

    /**
     * @param id the id of the request to get from the database
     * @return the IT request object with the given ID
     */
    public MaintenanceRequest getMaintenanceRequest(int id) {
        String query = "SELECT * FROM MAINTENANCEREQUEST WHERE (serviceID = ?)";
        return (MaintenanceRequest) executeGetById(query, MaintenanceRequest.class, id);
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<MaintenanceRequest> getAllMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST";
        return (List<MaintenanceRequest>) (List<?>) executeGetMultiple(query, MaintenanceRequest.class, new Object[]{});
    }

    /**
     * updates a given IT request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateMaintenanceRequest(MaintenanceRequest req) {
        String query = "UPDATE MAINTENANCEREQUEST SET notes=?, locationNodeID=?, completed=?, maintenanceType=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMaintenanceType().name(), req.getId());
    }

    /**
     * deletes a given IT request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteMaintenanceRequest(MaintenanceRequest req) {
        String query = "DELETE FROM MAINTENANCEREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<MaintenanceRequest> getAllIncompleteMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST WHERE (completed = ?)";
        return (List<MaintenanceRequest>) (List<?>) executeGetMultiple(query, MaintenanceRequest.class, false);
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<MaintenanceRequest> getAllCompleteMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST WHERE (completed = ?)";
        return (List<MaintenanceRequest>) (List<?>) executeGetMultiple(query, MaintenanceRequest.class, true);
    }

    /**
     * @param req the request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertToyRequest(ToyRequest req) {
        String insertQuery = ("INSERT INTO TOYREQUEST(notes, locationNodeID, completed, toyName) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getToyName());
    }

    /**
     * @param id the id of the request to get from the database
     * @return the IT request object with the given ID
     */
    public ToyRequest getToyRequest(int id) {
        String query = "SELECT * FROM TOYREQUEST WHERE (serviceID = ?)";
        return (ToyRequest) executeGetById(query, ToyRequest.class, id);
    }

    /**
     * @return all IT requests stored in the database in a List.
     */
    public List<ToyRequest> getAllToyRequests() {
        String query = "Select * FROM TOYREQUEST";
        return (List<ToyRequest>) (List<?>) executeGetMultiple(query, ToyRequest.class, new Object[]{});
    }

    /**
     * updates a given IT request in the database.
     *
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateToyRequest(ToyRequest req) {
        String query = "UPDATE TOYREQUEST SET notes=?, locationNodeID=?, completed=?, toyName=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getToyName(), req.getId());
    }

    /**
     * deletes a given IT request from the database
     *
     * @param req the request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteToyRequest(ToyRequest req) {
        String query = "DELETE FROM TOYREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT request that has not been completed yet.
     */
    public List<ToyRequest> getAllIncompleteToyRequests() {
        String query = "Select * FROM TOYREQUEST WHERE (completed = ?)";
        return (List<ToyRequest>) (List<?>) executeGetMultiple(query, ToyRequest.class, false);
    }

    public List<ToyRequest> getAllCompleteToyRequests() {
        String query = "Select * FROM TOYREQUEST WHERE (completed = ?)";
        return (List<ToyRequest>) (List<?>) executeGetMultiple(query, ToyRequest.class, true);
    }

    /**
     * @param table table to check
     * @return true if a table with the given name exists in the database and false if otherwise.
     */
    // CONTROLS
    boolean tableExists(String table) {
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

    /**
     * closes the connection when the database is done.
     */
    public void close() {
        try {
            connection.close();
            Connection closeConnection = DriverManager.getConnection(
                    "jdbc:derby:" + DATABASE_NAME + ";shutdown=true");
            closeConnection.close();
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Database '" + DATABASE_NAME + "' shutdown successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * helper function to close result sets and SQL statments after they've been used.
     *
     * @param stmt
     * @param rs
     */
    private void closeAll(Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        closeStatement(stmt);
    }

    /**
     * deletes everything from each table. Used exclusively for testing.
     */
    public void wipeTables() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // these must be wiped first to prevent constraint issues
            statement.addBatch("DELETE FROM EDGE");
            statement.addBatch("DELETE FROM RESERVATION");

            statement.addBatch("DELETE FROM EMPLOYEE");
            statement.addBatch("DELETE FROM RESERVABLESPACE");

            statement.addBatch("DELETE FROM ITREQUEST");
            statement.addBatch("DELETE FROM MEDICINEREQUEST");
            statement.addBatch("DELETE FROM FLORISTREQUEST");
            statement.addBatch("DELETE FROM GIFTSTOREREQUEST");
            statement.addBatch("DELETE FROM SECURITYREQUEST");
            statement.addBatch("DELETE FROM SANITATIONREQUEST");
            statement.addBatch("DELETE FROM RELIGIOUSREQUEST");
            statement.addBatch("DELETE FROM INTERPRETERREQUEST");
            statement.addBatch("DELETE FROM INTERNALTRANSPORTREQUEST");
            statement.addBatch("DELETE FROM PATIENTINFOREQUEST");
            statement.addBatch("DELETE FROM AVSERVICEREQUEST");
            statement.addBatch("DELETE FROM EXTERNALTRANSPORTREQUEST");
            statement.addBatch("DELETE FROM MAINTENANCEREQUEST");
            statement.addBatch("DELETE FROM TOYREQUEST");

            // restart the auto-generated keys
            statement.addBatch("ALTER TABLE RESERVATION ALTER COLUMN eventID RESTART WITH 0");

            statement.addBatch("ALTER TABLE ITREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE MEDICINEREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE FLORISTREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE GIFTSTOREREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE SECURITYREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE SANITATIONREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE RELIGIOUSREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE INTERPRETERREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE INTERNALTRANSPORTREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE PATIENTINFOREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE AVSERVICEREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE EXTERNALTRANSPORTREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE MAINTENANCEREQUEST ALTER COLUMN serviceID RESTART WITH 0");
            statement.addBatch("ALTER TABLE TOYREQUEST ALTER COLUMN serviceID RESTART WITH 0");

            statement.addBatch("DELETE FROM NODE");

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }


    //<editor-fold desc="Generic Execution Methods">

    /**
     * execute get multiple
     *
     * @param query      the query to use as the prepared statement.
     * @param cls        the class that the method should return
     * @param parameters the parameters for the prepared statement. There must be an equal number of ?s in the query and parameters in here for the query to run properly.
     * @return a list of the given object type, based on the database query
     */
    private <T> List<Object> executeGetMultiple(String query, Class<T> cls, Object... parameters) {
        ArrayList<Object> reqs = new ArrayList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, parameters);

            // execute the query
            rs = stmt.executeQuery();
            while (rs.next()) {
                reqs.add(extractGeneric(rs, cls));
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeAll(stmt, rs);
        }

        return reqs;
    }

    /**
     * Run an executeUpdate query - for UPDATE AND DELETE
     *
     * @param query
     * @param parameters
     * @return a boolean indicating success
     */
    private boolean executeUpdate(String query, Object... parameters) {
        boolean modifyResult = false;
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            prepareStatement(stmt, parameters);

            int res = stmt.executeUpdate();
            modifyResult = res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(stmt);
        }
        return modifyResult;
    }

    /**
     * a helper method used to insert into the database
     *
     * @param insertQuery a string to work as the query
     * @param values      the values to go into the insert statement
     * @return true if every value went in and false if otherwise
     */
    private boolean executeInsert(String insertQuery, Object... values) {
        PreparedStatement insertStatement = null;

        // Track the status of the insert
        boolean insertStatus = false;

        try {
            // Prep the statement
            insertStatement = connection.prepareStatement(insertQuery);
            prepareStatement(insertStatement, values);

            // Execute
            insertStatement.execute();

            // If we made it this far, we're successful!
            insertStatus = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(insertStatement);
        }
        return insertStatus;
    }

    /**
     * returns an object from the database based on a given ID
     *
     * @param query the query to
     * @param cls   the class of object to return
     * @param id    the id that functions as the key to retrieve
     * @return an object of type cls
     */
    private <T> Object executeGetById(String query, Class<T> cls, Object id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result;

        try {
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, id);

            // execute the query
            rs = stmt.executeQuery();

            boolean hasNext = rs.next();

            // If there is no next node, return null
            if (!hasNext) {
                return null;
            }

            result = extractGeneric(rs, cls);

            stmt.close();
            rs.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(stmt, rs);
        }
        return null;
    }

    //</editor-fold>


    //<editor-fold desc="Extraction Methods">
    /////////// EXTRACTION METHODS /////////////////////////////////////////////////////////////////////////////////////
    // all of the methods below are helpers for extracting results from ResultSets generated by queries.
    // either extractGeneric or the specific ExtractType methods can be used.

    /**
     * @param rs
     * @param cls
     * @param <T>
     * @return
     * @throws SQLException when extraction fails.
     */
    private <T> Object extractGeneric(ResultSet rs, Class<T> cls) throws SQLException {
        if (cls.equals(Node.class)) return extractNode(rs);
        else if (cls.equals(Edge.class)) return extractEdge(rs);
        else if (cls.equals(ReservableSpace.class)) return extractReservableSpace(rs);
        else if (cls.equals(Reservation.class)) return extractReservation(rs);
        else if (cls.equals(ITRequest.class)) return extractITRequest(rs);
        else if (cls.equals(MedicineRequest.class)) return extractMedicineRequest(rs);
        else if (cls.equals(Employee.class)) return extractEmployee(rs);
        else if (cls.equals(FloristRequest.class)) return extractFloristRequest(rs);
        else if (cls.equals(GiftStoreRequest.class)) return extractGiftStoreRequest(rs);
        else if (cls.equals(SecurityRequest.class)) return extractSecurityRequest(rs);
        else if (cls.equals(SanitationRequest.class)) return extractSanitationRequest(rs);
        else if (cls.equals(ReligiousRequest.class)) return extractReligiousRequest(rs);
        else if (cls.equals(InterpreterRequest.class)) return extractInterpreterRequest(rs);
        else if (cls.equals(InternalTransportRequest.class)) return extractInternalTransportRequest(rs);
        else if (cls.equals(PatientInfoRequest.class)) return extractPatientInfoRequest(rs);
        else if (cls.equals(AVServiceRequest.class)) return extractAVServiceRequest(rs);
        else if (cls.equals(ExternalTransportRequest.class)) return extractExtTransRequest(rs);
        else if (cls.equals(MaintenanceRequest.class)) return extractMaintenanceRequest(rs);
        else if (cls.equals(ToyRequest.class)) return extractToyRequest(rs);
        else return null;
    }

    private FloristRequest extractFloristRequest(ResultSet rs, String name) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String bouquetType = rs.getString("bouquetType");
        int quantity = rs.getInt("quantity");
        // construct the new node and return it
        return new FloristRequest(serviceID, notes, locationNode, completed, bouquetType, quantity);
    }

    private FloristRequest extractFloristRequest(ResultSet rs) throws SQLException {
        return extractFloristRequest(rs, "");
    }

    private SecurityRequest extractSecurityRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("urgency");

        return new SecurityRequest(serviceID, notes, locationNode, completed, SecurityRequest.Urgency.valueOf(typeString));
    }

    private SanitationRequest extractSanitationRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String urgency = rs.getString("urgency");
        String materialState = rs.getString("materialState");

        return new SanitationRequest(serviceID, notes, locationNode, completed, urgency, materialState);
    }

    private GiftStoreRequest extractGiftStoreRequest(ResultSet rs) throws SQLException {
        // Extract data
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("gType");
        String patientName = rs.getString("patientName");

        return new GiftStoreRequest(serviceID, notes, locationNode, completed, GiftStoreRequest.GiftType.valueOf(typeString), patientName);
    }

    private ReligiousRequest extractReligiousRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String religion = rs.getString("religion");

        return new ReligiousRequest(serviceID, notes, locationNode, completed, ReligiousRequest.Religion.valueOf(religion));
    }

    private InterpreterRequest extractInterpreterRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String language = rs.getString("language");

        return new InterpreterRequest(serviceID, notes, locationNode, completed, InterpreterRequest.Language.valueOf(language));
    }

    private PatientInfoRequest extractPatientInfoRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String birthDay = rs.getString("birthDay");
        String description = rs.getString("description");

        return new PatientInfoRequest(serviceID, notes, locationNode, completed, firstName, lastName, birthDay, description);
    }

    private InternalTransportRequest extractInternalTransportRequest(ResultSet rs) throws SQLException {
        // locationNodeID varchar (255), completed boolean, transportType varchar(40)
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String enumVal = rs.getString("transportType");

        return new InternalTransportRequest(serviceID, notes, locationNode, completed, InternalTransportRequest.TransportType.valueOf(enumVal));
    }

    private ExternalTransportRequest extractExtTransRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String transType = rs.getString("transportType");
        String descript = rs.getString("description");
        Date t = new Date(rs.getTimestamp("time").getTime());

        return new ExternalTransportRequest(serviceID, notes, locationNode, completed, t, ExternalTransportRequest.TransportationType.valueOf(transType), descript);
    }

    private AVServiceRequest extractAVServiceRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("avServiceType");

        return new AVServiceRequest(serviceID, notes, locationNode, completed, AVServiceRequest.AVServiceType.valueOf(typeString));
    }

    private MaintenanceRequest extractMaintenanceRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("maintenanceType");

        return new MaintenanceRequest(serviceID, notes, locationNode, completed, MaintenanceRequest.MaintenanceType.valueOf(typeString));
    }

    private ToyRequest extractToyRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String toyName = rs.getString("toyName");

        return new ToyRequest(serviceID, notes, locationNode, completed, toyName);
    }

    private Node extractNode(ResultSet rs, String name) throws SQLException {
        String newNodeID = rs.getString(name + "nodeID");
        int newxcoord = rs.getInt(name + "xcoord");
        int newycoord = rs.getInt(name + "ycoord");
        String newFloor = rs.getString(name + "floor");
        String newBuilding = rs.getString(name + "building");
        String newNodeType = rs.getString(name + "nodeType");
        String newLongName = rs.getString(name + "longName");
        String newShortName = rs.getString(name + "shortName");
        // construct the new node and return it
        return new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
    }

    private Node extractNode(ResultSet rs) throws SQLException {
        return extractNode(rs, "");
    }

    private Edge extractEdge(ResultSet rs) throws SQLException {
        String newEdgeID = rs.getString("edgeID");
        Node node1 = extractNode(rs, "n1");
        Node node2 = extractNode(rs, "n2");
        return new Edge(newEdgeID, node1, node2);
    }

    private Reservation extractReservation(ResultSet rs) throws SQLException {
        // Extract data
        int eventID = rs.getInt("eventID");
        String eventName = rs.getString("eventName");
        String locationID = rs.getString("spaceID");
        Date startTime = new Date(rs.getTimestamp("startTime").getTime());
        Date endTime = new Date(rs.getTimestamp("endTime").getTime());
        int privacyLevel = rs.getInt("privacyLevel");
        int employeeID = rs.getInt("employeeID");

        GregorianCalendar startTimeCalendar = new GregorianCalendar();
        startTimeCalendar.setTime(startTime);
        GregorianCalendar endTimeCalendar = new GregorianCalendar();
        endTimeCalendar.setTime(endTime);

        // construct the new reservation and return it
        return new Reservation(eventID, privacyLevel, employeeID, eventName, locationID, startTimeCalendar, endTimeCalendar);
    }

    private Employee extractEmployee(ResultSet rs) throws SQLException {
        // Extract data
        int empID = rs.getInt("employeeID");
        String jobString = rs.getString("job");
        boolean isAdmin = rs.getBoolean("isAdmin");
        String password = rs.getString("password");
        String username = rs.getString("username");

        return new Employee(empID, username, JobType.valueOf(jobString), isAdmin, password);
    }

    private ReservableSpace extractReservableSpace(ResultSet rs) throws SQLException {
        String spaceID = rs.getString("spaceID");
        String spaceName = rs.getString("spaceName");
        String spaceType = rs.getString("spaceType");
        String locationNodeID = rs.getString("locationNode");
        Date timeOpen = new Date(rs.getTimestamp("timeOpen").getTime());
        Date timeClosed = new Date(rs.getTimestamp("timeClosed").getTime());

        GregorianCalendar timeOpenCalendar = new GregorianCalendar();
        timeOpenCalendar.setTime(timeOpen);
        GregorianCalendar timeClosedCalendar = new GregorianCalendar();
        timeClosedCalendar.setTime(timeClosed);

        return new ReservableSpace(spaceID, spaceName, spaceType, locationNodeID, timeOpenCalendar, timeClosedCalendar);
    }

    private ITRequest extractITRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String type = rs.getString("type");

        return new ITRequest(serviceID, notes, locationNode, completed, ITRequest.ITRequestType.valueOf(type));
    }

    private MedicineRequest extractMedicineRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String medicineType = rs.getString("medicineType");
        double qty = rs.getDouble("quantity");

        return new MedicineRequest(serviceID, notes, locationNode, completed, medicineType, qty);
    }
    ////////////////END EXTRACTION METHODS /////////////////////////////////////////////////////////////////////////////
    //</editor-fold>


    /////////////////////////////////////// CALLBACKS //////////////////////////////////////////////////////////////////


    private void executeNodeCallbacks() {
        for (Function<Void, Void> callback : nodeCallbacks) {
            callback.apply(null);
        }
    }

    public void registerNodeCallback(Function<Void, Void> callback) {
        nodeCallbacks.add(callback);
    }


    private void executeEdgeCallbacks() {
        for (Function<Void, Void> callback : edgeCallbacks) {
            callback.apply(null);
        }
    }

    public void registerEdgeCallback(Function<Void, Void> callback) {
        edgeCallbacks.add(callback);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Set the values of a prepared statement. The number of variables in the prepared statement and the number of
     * values must match.
     *
     * @param preparedStatement the prepared statement to prepare
     * @param values            the values to insert
     * @throws SQLException there is a mismatch in number of variables or there is a database access error
     */
    private void prepareStatement(PreparedStatement preparedStatement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setObject(i + 1, values[i]);
        }
    }

    /**
     * Attempt to close a statement
     *
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

    public static int getDatabaseVersion() {
        return DATABASE_VERSION.intValue();
    }
}
