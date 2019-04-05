package service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.*;
import model.request.ITRequest;
import model.request.MedicineRequest;
import org.apache.derby.iapi.services.io.FileUtil;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Function;

public class DatabaseService {

    public static final String DATABASE_NAME = "hospital-db";
    public static final Integer DATABASE_VERSION = 6;
    private static DatabaseService _dbs;

    private Connection connection;
    private ArrayList<Function<Void, Void>> nodeCallbacks;
    private ArrayList<Function<Void, Void>> edgeCallbacks;

    /**
     * Construct a DatabaseService
     * @param startFresh if true, blow away any database existing on disk
     * @param loadCSVs if true, load the CSVs
     * @throws SQLException on DB connection creation error
     */
    private DatabaseService(boolean startFresh, boolean loadCSVs) throws SQLException {
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        boolean createFlag = false;

        // Start by trying to open connection with existing database
        Connection conn = openConnection(false);

        if (conn != null) { // Database exists on file
            if (startFresh) { // We don't want to use an existing database
                // Close initial connection
                conn.close();

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
        } else { // No database exists on disk, so create a new one
            conn = openConnection(true);
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

        if(createFlag) {
            this.createTables();

            if (loadCSVs) {
                CSVService.importNodes();
                CSVService.importEdges();
                CSVService.importEmployees();
                CSVService.importReservableSpaces();
            }
        }

        nodeCallbacks = new ArrayList<>();
        edgeCallbacks = new ArrayList<>();
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


    /**
     * This overrides the global dbs - only use to mock the database!!
     * @param dbs
     */
    public static void setDatabaseForMocking(DatabaseService dbs) {
        _dbs = dbs;
    }

    public static synchronized DatabaseService getDatabaseService(boolean startFresh, boolean loadCSVs) {
        // Case 1: Database already exists in memory and we want to start fresh
        // Execute later if statement as well
        if (startFresh && _dbs != null) {
            _dbs.close();
            _dbs = null;
            wipeOutFiles();
        }

        // Create a new database service, telling it to start over if necessary
        if (_dbs == null) {
            try {
                _dbs = new DatabaseService(startFresh, loadCSVs);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("WARNING!");
                System.out.println("Database not created due to the above error!");
            }
        }

        return _dbs;
    }

    // Default loadCSVs to true
    public static synchronized DatabaseService getDatabaseService(boolean startFresh) {
        return getDatabaseService(startFresh, true);
    }

    // Default start fresh to false
    public static synchronized DatabaseService getDatabaseService() {
        return getDatabaseService(false);
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
        if(!deleted)
            System.err.println("File not deleted: " + f.getPath());
    }

    public static void wipeOutFiles() {
        if(_dbs != null) {
            _dbs.close();
        }
        wipeOutFiles(new File(DATABASE_NAME));
    }


    /**
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
     *
     */
    private void createTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // Check to see if the tables have already been created, if they have, do not create them a second time.
            statement.addBatch("CREATE TABLE NODE (nodeID varchar(255) PRIMARY KEY, xcoord int, ycoord int, floor varchar(255), building varchar(255), nodeType varchar(255), longName varchar(255), shortName varchar(255))");

            statement.addBatch("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(255), node2 varchar(255))");

            statement.addBatch("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, job varchar(25), isAdmin boolean, password varchar(50))");

            statement.addBatch("CREATE TABLE ITREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(10), completed boolean, description varchar(300))");

            statement.addBatch("CREATE TABLE MEDICINEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(10), completed boolean, medicineType varchar(50), quantity double)");

            statement.addBatch("CREATE TABLE RESERVATION(eventID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), eventName varchar(50), locationID varchar(30), startTime timestamp, endTime timestamp, privacyLevel int, employeeID int)");

            statement.addBatch("CREATE TABLE RESERVABLESPACE(spaceID varchar(30) PRIMARY KEY, spaceName varchar(50), spaceType varchar(4), locationNode varchar(10), timeOpen timestamp, timeClosed timestamp)");

            statement.addBatch("CREATE TABLE META_DB_VER(id int PRIMARY KEY , version int)");
            statement.addBatch("INSERT INTO META_DB_VER values(0, " + getDatabaseVersion() + ")");

            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node1) REFERENCES NODE(nodeID)");
            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node2) REFERENCES NODE(nodeID)");
            // constraints that matter less but will be fully implemented later
            //statement.execute("ALTER TABLE RESERVATION ADD FOREIGN KEY (LOCATIONID) REFERENCES RESERVABLESPACE(SPACEID)");
            //statement.execute("ALTER TABLE RESERVATION ADD FOREIGN KEY (employeeID) REFERENCES EMPLOYEE(employeeID)");


            statement.addBatch("CREATE INDEX LocationIndex ON RESERVATION (locationID)");


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
     * @param n A {@link Node} to insert into the database
     * @return true if the node is successfully inserted, false otherwise.
     */
    public boolean insertNode(Node n){
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        boolean successful = executeInsert(nodeStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName());
        if (successful) executeNodeCallbacks();
        return successful;
    }

    /**
     * Update the database entry for a given node
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
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    public boolean deleteNode(Node n) {
        String query = "DELETE FROM NODE WHERE (nodeID = ?)";
        boolean successful = executeUpdate(query, n.getNodeID());
        if (successful) executeNodeCallbacks();
        return successful;
    }

    /** retrieves the given node from the database
     * @param nodeID the ID of the node to be retrieved
     * @return a node with the given ID
     */
    public Node getNode(String nodeID){
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
                for (int j = (i*1000); j < i*1000+1000 && j < nodes.size(); j++) {
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

    /** Returns all nodes in the database.
     * @return list of all nodes in the database
     */
    public ArrayList<Node> getAllNodes() {
        String query = "Select * FROM NODE";
        return (ArrayList<Node>)(List<?>) executeGetMultiple(query, Node.class, new Object[]{});
    }

    /** get nodes filtered by specific type
     * @param filterOut the parameter to exclude specific nodes by
     * @return an arraylist of nodes that do not include the specified parameter
     */
    @SuppressFBWarnings(value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", justification="Not a security issue - just add question marks based on number of types to filter out.")
    public ArrayList<Node> getNodesFilteredByType(String... filterOut) {
        String query = "Select * from NODE where NODETYPE not in (";
        StringBuilder builtQuery = new StringBuilder();
        builtQuery.append(query);
        for (int i = 0; i < filterOut.length; i++) {
            builtQuery.append("?,");
        }
        builtQuery.deleteCharAt(builtQuery.lastIndexOf(","));
        builtQuery.append(")");

        return (ArrayList<Node>)(List<?>) executeGetMultiple(builtQuery.toString(), Node.class, (Object[]) filterOut);
    }

    /** get all nodes from the specified floor
     * @param floor the floor to retrieve all nodes from
     * @return an arraylist of all nodes on the given floor.
     */
    public ArrayList<Node> getNodesByFloor(String floor) {
        String query = "Select * FROM NODE WHERE NODE.FLOOR = ?";
        return (ArrayList<Node>)(List<?>) executeGetMultiple(query, Node.class, floor);
    }

    public int getNumNodeTypeByFloor(String nodeType, String floor) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try{
            stmt = connection.prepareStatement("SELECT COUNT (*) AS TOTAL FROM NODE WHERE (floor=? AND nodeType=?)");
            prepareStatement(stmt, floor, nodeType);

            // execute the query
            res = stmt.executeQuery();
            int num = -1;
            while(res.next()){
                num = res.getInt("TOTAL");
            }
            stmt.close();
            res.close();
            return num;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeAll(stmt, res);
        }
    }

    // EDGE FUNCTIONS
    /** returns a list of nodes that are connected to the given node
     * @param n the node to retrieve all nodes connected to from
     * @return A list of all nodes connected to the given node.
     */
    public ArrayList<Node> getNodesConnectedTo(Node n) {
        String nodeID = n.getNodeID();
        String query = "SELECT NODE.NodeID, NODE.xcoord, NODE.ycoord, NODE.floor, NODE.building, NODE.nodeType, NODE.longName, NODE.shortName FROM NODE INNER JOIN EDGE ON (NODE.NodeID = EDGE.node1 AND EDGE.node2 = ?) OR (NODE.NodeID = EDGE.node2 AND EDGE.Node1 = ?)";

        return (ArrayList<Node>)(List<?>) executeGetMultiple(query, Node.class, nodeID, nodeID);
    }

    /** returns all edges on a specific floor
     * not fully implemented yet
     * not really that necessary
     * @param floor
     * @return
     */
    // get edges from a specific floor
    public static Collection<Edge> getEdges(int floor) {
        // DEPRECATED
        return null;
    }

    /** insert an edge. The method will fail and return false if the two nodes it points to
     * do not already exist in the database.
     * @param e the edge to insert
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertEdge(Edge e){
        String insertStatement = ("INSERT INTO EDGE VALUES(?,?,?)");
        String node1ID = e.getNode1().getNodeID();
        String node2ID = e.getNode2().getNodeID();

        boolean successful = executeInsert(insertStatement, e.getEdgeID(), node1ID, node2ID);
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    /** get an edge. This also pulls out the nodes that edge connects.
     * @param edgeID the ID of the edge to retrieve
     * @return the edge corresponding to the given ID
     */
    public Edge getEdge(String edgeID){
        String query = "SELECT * FROM EDGE WHERE (EDGEID = ?)";
        return (Edge) executeGetById(query, Edge.class, edgeID);
    }

    /** updates an edge with new node IDs.
     * @param e the edge to update
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateEdge(Edge e){
        String query = "UPDATE EDGE SET edgeID=?, NODE1=?, NODE2=? WHERE(EDGEID = ?)";
        boolean successful = executeUpdate(query, e.getEdgeID(), e.getNode1().getNodeID(), e.getNode2().getNodeID(), e.getEdgeID());
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    /** Deletes an edge from the database.
     * @param e edge to delete from the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteEdge(Edge e){
        String query = "DELETE FROM EDGE WHERE (edgeID = ?)";
        boolean successful = executeUpdate(query, e.getEdgeID());
        if (successful) executeEdgeCallbacks();
        return successful;
    }

    public ArrayList<Edge> getAllEdges(){
        String query = "Select * FROM EDGE";
        return (ArrayList<Edge>)(List<?>) executeGetMultiple(query, Edge.class, new Object[]{});
    }

    /** Inserts a new reservation into the database.
     * @param reservation reservation to insert into the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean insertReservation(Reservation reservation) {
        String insertStatement = ("INSERT INTO RESERVATION(EVENTNAME, LOCATIONID, STARTTIME, ENDTIME, PRIVACYLEVEL, EMPLOYEEID) VALUES(?, ?, ?, ?, ?, ?)");
        return executeInsert(insertStatement, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(), reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId());
    }

    /** retrieves a single reservation from the database with it's ID.
     * @param id id of the reservation to get from the database
     * @return the reservation object corresponding to the ID
     */
    public Reservation getReservation(int id) {
        String query = "SELECT * FROM RESERVATION WHERE (EVENTID = ?)";
        return (Reservation) executeGetById(query, Reservation.class, id);
    }

    /** retrieves all reservations from the database
     * @return a list of all reservations in the database
     */
    public List<Reservation> getAllReservations() {
        String query = "Select * FROM RESERVATION";
        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, new Object[]{});
    }

    /** updates a reservation in the database.
     * @param reservation reservation to update in the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateReservation(Reservation reservation) {
        String query = "UPDATE RESERVATION SET eventName=?, locationID=?, startTime=?, endTime=?, privacyLevel=?, employeeID=? WHERE (eventID = ?)";
        return executeUpdate(query, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId(), reservation.getEventID());
    }

    /** Removes a reservation from the database.
     * @param reservation a reservation object
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteReservation(Reservation reservation) {
        String query = "DELETE FROM RESERVATION WHERE (eventID = ?)";
        return executeUpdate(query, reservation.getEventID());
    }

    /**
     * Query all reservations made for a given {@link ReservableSpace}.
     * @param id the spaceID of the ReservableSpace being requested for
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceId(String id) {
        String query = "SELECT * FROM RESERVATION WHERE (LOCATIONID = ?)";
        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, id);
    }

    /**
     * Get all reservations made for the given space ID that fall entirely within {@param from} and {@param to}.
     * @param id the spaceID of the reservable space being requested for
     * @param from start of the window
     * @param to end of the window
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {
        String query = "SELECT * FROM RESERVATION WHERE (LOCATIONID = ? and (STARTTIME between ? and ?) and (ENDTIME between ? and ?))";
        System.out.println(id);
        System.out.println("dbs" + from.get(Calendar.YEAR) +  " " + from.get(Calendar.MONTH) + " " + from.get(Calendar.DATE) + " " + from.get(Calendar.HOUR));
        System.out.println(to.get(Calendar.YEAR) +  " " + to.get(Calendar.MONTH) + " " + to.get(Calendar.DATE)+ " " + to.get(Calendar.HOUR));

        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, id, from, to, from, to);
    }

    /**
     * @param employee the employee to insert into the database
     * @return true if the insert succeeded or false if otherwise.
     */
    public boolean insertEmployee(Employee employee) {
        String insertStatement = ("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?)");
        return executeInsert(insertStatement, employee.getID(), employee.getJob(), employee.isAdmin(), employee.getPassword());
    }

    /**
     * @param id the id of the employee to get from the database
     * @return the object belonging to the employee of the given ID
     */
    public Employee getEmployee(int id) {
        String query = "SELECT * FROM EMPLOYEE WHERE (EMPLOYEEID = ?)";
        return (Employee) executeGetById(query, Employee.class, id);
    }

    /** retrieves a list of all employees from the database.
     * @return a list of all employees in the database.
     */
    public List<Employee> getAllEmployees() {
        String query = "Select * FROM EMPLOYEE";
        return (List<Employee>)(List<?>) executeGetMultiple(query, Employee.class, new Object[]{});
    }

    /**
     * @param employee the employee to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE EMPLOYEE SET job=?, isAdmin=? WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getJob(), employee.isAdmin(), employee.getID());
    }

    /**
     * @param employee employee to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM EMPLOYEE WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getID());
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
        return (List<ReservableSpace>)(List<?>) executeGetMultiple(query, ReservableSpace.class, new Object[]{});
    }

    /**
     * @param from gregorian calendar start time
     * @param to gregorian calendar end time
     * @return a list of reservations  between start and end times
     */
    public List<Reservation> getReservationsBetween(GregorianCalendar from, GregorianCalendar to) {
        String query = "SELECT * FROM RESERVATION WHERE ((STARTTIME <= ? and ENDTIME > ?) or (STARTTIME >= ? and STARTTIME < ?))";

        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, from, from, from, to);

        // TODO write query
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
        String insertQuery = ("INSERT INTO ITREQUEST(notes, locationNodeID, completed, description) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDescription());
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
        return (List<ITRequest>)(List<?>) executeGetMultiple(query, ITRequest.class, new Object[]{});
    }

    /** updates a given IT request in the database.
     * @param req the request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateITRequest(ITRequest req) {
        String query = "UPDATE ITREQUEST SET notes=?, locationNodeID=?, completed=?, description=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDescription(), req.getId());
    }

    /** deletes a given IT request from the database
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
        return (List<ITRequest>)(List<?>) executeGetMultiple(query, ITRequest.class, false);
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
        return (List<MedicineRequest>)(List<?>) executeGetMultiple(query, MedicineRequest.class, new Object[]{});
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
        return (List<MedicineRequest>)(List<?>) executeGetMultiple(query, MedicineRequest.class, false);
    }

    /**
     * @param table table to check
     * @return true if a table with the given name exists in the database and false if otherwise.
     */
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

    /** helper function to close result sets and SQL statments after they've been used.
     * @param stmt
     * @param rs
     */
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

    /**
     * deletes everything from each table. Used exclusively for testing.
     */
    public void wipeTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.addBatch("DROP TABLE EDGE");
            statement.addBatch("DROP TABLE NODE");
            statement.addBatch("DROP TABLE EMPLOYEE");
            statement.addBatch("DROP TABLE ITREQUEST");
            statement.addBatch("DROP TABLE MEDICINEREQUEST");
            statement.addBatch("DROP TABLE RESERVATION");
            statement.addBatch("DROP TABLE RESERVABLESPACE");
            statement.addBatch("DROP TABLE META_DB_VER");
            statement.executeBatch();

            this.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }


    //<editor-fold desc="Generic Execution Methods">

    /** execute get multiple
     * @param query the query to use as the prepared statement.
     * @param cls the class that the method should return
     * @param parameters the parameters for the prepared statement. There must be an equal number of ?s in the query and parameters in here for the query to run properly.
     * @return a list of the given object type, based on the database query
     */
    private <T> List<Object> executeGetMultiple(String query, Class<T> cls, Object... parameters) {
        ArrayList<Object> reqs = new ArrayList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, parameters);

            // execute the query
            rs = stmt.executeQuery();
            while(rs.next()){
                reqs.add(extractGeneric(rs, cls));
            }
            stmt.close();
            rs.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeAll(stmt, rs);
        }

        return reqs;
    }

    /**
     * Run an executeUpdate query - for UPDATE AND DELETE
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

    /** a helper method used to insert into the database
     * @param insertQuery a string to work as the query
     * @param values the values to go into the insert statement
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

    /** returns an object from the database based on a given ID
     * @param query the query to
     * @param cls the class of object to return
     * @param id  the id that functions as the key to retrieve
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
        Object result;

        if (cls.equals(Node.class)) {
            result = extractNode(rs);
        } else if (cls.equals(Edge.class)) {
            result = extractEdge(rs);
        } else if (cls.equals(ReservableSpace.class)) {
            result = extractReservableSpace(rs);
        } else if (cls.equals(Reservation.class)) {
            result = extractReservation(rs);
        } else if (cls.equals(ITRequest.class)) {
            result = extractITRequest(rs);
        } else if (cls.equals(MedicineRequest.class)) {
            result = extractMedicineRequest(rs);
        } else if (cls.equals(Employee.class)) {
            result = extractEmployee(rs);
        } else {
            return null;
        }
        return result;
    }

    private Node extractNode(ResultSet rs) throws SQLException {
        String newNodeID = rs.getString("nodeID");
        int newxcoord = rs.getInt("xcoord");
        int newycoord = rs.getInt("ycoord");
        String newFloor = rs.getString("floor");
        String newBuilding = rs.getString("building");
        String newNodeType = rs.getString("nodeType");
        String newLongName = rs.getString("longName");
        String newShortName = rs.getString("shortName");
        // construct the new node and return it
        return new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
    }

    private Edge extractEdge(ResultSet rs) throws SQLException {
        String newEdgeID = rs.getString("edgeID");
        String node1Name = rs.getString("NODE1");
        String node2Name = rs.getString("NODE2");
        Node node1 = getNode(node1Name);
        Node node2 = getNode(node2Name);
        return new Edge (newEdgeID, node1, node2);
    }

    private Reservation extractReservation(ResultSet rs) throws SQLException {
        // Extract data
        int eventID = rs.getInt("eventID");
        String eventName = rs.getString("eventName");
        String locationID = rs.getString("locationID");
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
        String job = rs.getString("job");
        boolean isAdmin = rs.getBoolean("isAdmin");
        String password = rs.getString("password");

        return new Employee(empID, job, isAdmin, password);
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
        String description = rs.getString("description");

        return new ITRequest(serviceID, notes, locationNode, completed, description);
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

    public static int getDatabaseVersion() {
        return DATABASE_VERSION.intValue();
    }
}
