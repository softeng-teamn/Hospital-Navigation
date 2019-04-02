package service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.*;
import model.request.ITRequest;
import model.request.MedicineRequest;

import java.sql.*;
import java.util.*;
import java.util.Date;

@SuppressWarnings("ALL")
public class DatabaseService {

    public static final Integer DATABASE_VERSION = 6;

    private Connection connection;

    private String databaseName;

    private boolean newlyCreated;

    private DatabaseService(Connection connection) {
        this.connection = connection;
    }

    public static DatabaseService init(String dbName) throws SQLException, MismatchedDatabaseVersionException {
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        Connection connection;
        boolean createFlag = false;

        try {
            connection = DriverManager.getConnection("jdbc:derby:"+dbName+";");
        } catch (SQLException e) {
            if (e.getMessage().contains("Database '" + dbName + "' not found")) {
                System.out.print("No existing database found, creating database...");
                System.out.flush();
                connection = DriverManager.getConnection("jdbc:derby:" + dbName + ";create=true");
                System.out.println("Database created");
                createFlag = true;
            } else {
                throw e;
            }
        }

        DatabaseService myDB = new DatabaseService(connection);

        myDB.databaseName = dbName;

        if(createFlag){
            myDB.createTables();
        } else {
            myDB.validateVersion();
        }

        myDB.newlyCreated = createFlag;

        return myDB;
    }

    /**
     * Throws an exception if myDB has an invalid version
     */
    private void validateVersion() throws MismatchedDatabaseVersionException {
        String query = "SELECT * FROM META_DB_VER";

        ResultSet rs = null;
        Statement versionStatement = null;
        try {
            versionStatement = connection.createStatement();

            try {
                rs = versionStatement.executeQuery(query);
            } catch (SQLSyntaxErrorException e) {
                closeAll(versionStatement, rs);
                throw new MismatchedDatabaseVersionException("Database loaded with no version! Expected: " + getDatabaseVersion());
            }

            boolean hasNext = rs.next();

            // If no version identifier exists, assume bad database
            if (!hasNext) {
                closeAll(versionStatement, rs);
                throw new MismatchedDatabaseVersionException("Database loaded with no version! Expected: " + getDatabaseVersion());
            }

            int existingVersion = rs.getInt("version");

            if (existingVersion != getDatabaseVersion()) {
                closeAll(versionStatement, rs);
                throw new MismatchedDatabaseVersionException("Existing database version: " + existingVersion + ", expected: " + getDatabaseVersion());
            }

            rs.close();
            versionStatement.close();
        } catch (SQLException e) {
            closeAll(versionStatement, rs);
        }
    }

    public static DatabaseService init() throws SQLException, MismatchedDatabaseVersionException {
        return init("hospital-db");
    }

    // create tables in the database if they do not already exist.
    private void createTables(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // Check to see if the tables have already been created, if they have, do not create them a second time.
            if(!tableExists("NODE")){
                statement.addBatch("CREATE TABLE NODE (nodeID varchar(255) PRIMARY KEY, xcoord int, ycoord int, floor varchar(255), building varchar(255), nodeType varchar(255), longName varchar(255), shortName varchar(255))");
            }
            if(!tableExists("EDGE")){
                statement.addBatch("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(255), node2 varchar(255))");
            }
            if(!tableExists("EMPLOYEE")){
                statement.addBatch("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, job varchar(25), isAdmin boolean, password varchar(50))");
            }
            if(!tableExists("ITREQUEST")){
                statement.addBatch("CREATE TABLE ITREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(10), completed boolean, description varchar(300))");
            }
            if(!tableExists("MEDICINEREQUEST")){
                statement.addBatch("CREATE TABLE MEDICINEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(10), completed boolean, medicineType varchar(50), quantity double)");
            }
            if(!tableExists("RESERVATION")){
                statement.addBatch("CREATE TABLE RESERVATION(eventID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), eventName varchar(50), locationID varchar(30), startTime timestamp, endTime timestamp, privacyLevel int, employeeID int)");
            }
            if(!tableExists("RESERVABLESPACE")){
                statement.addBatch("CREATE TABLE RESERVABLESPACE(spaceID varchar(30) PRIMARY KEY, spaceName varchar(50), spaceType varchar(4), locationNode varchar(10), timeOpen timestamp, timeClosed timestamp)");
            }
            if(!tableExists("META_DB_VER")){
                statement.addBatch("CREATE TABLE META_DB_VER(id int PRIMARY KEY , version int)");
                statement.addBatch("INSERT INTO META_DB_VER values(0, " + getDatabaseVersion() + ")");
            }
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
        return executeInsert(nodeStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName());
    }

    /**
     * Update the database entry for a given node
     * @param n A {@link Node} to update. The node must have a valid ID
     * @return true if the update is successful, false otherwise
     */
    public boolean updateNode(Node n) {
        String query = "UPDATE NODE SET xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=? WHERE (nodeID = ?)";
        return executeUpdate(query, n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(),
                n.getLongName(), n.getShortName(), n.getNodeID());
    }

    /**
     * Delete a node if it exists
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    public boolean deleteNode(Node n) {
        String query = "DELETE FROM NODE WHERE (nodeID = ?)";
        return executeUpdate(query, n.getNodeID());
    }

    // retrieves the given node from the database
    public Node getNode(String nodeID){
        String query = "SELECT * FROM NODE WHERE (NODEID = ?)";
        return (Node) executeGetById(query, Node.class, nodeID);
    }

    public ArrayList<Node> getAllNodes() {
        String query = "Select * FROM NODE";
        return (ArrayList<Node>)(List<?>) executeGetMultiple(query, Node.class, new Object[]{});
    }

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

    // get all nodes from the specified floor
    public ArrayList<Node> getNodesByFloor(String floor) {
        ArrayList<Node> floorNodes = new ArrayList<Node>();
        String query = "Select * FROM NODE WHERE NODE.FLOOR = ?";
        PreparedStatement stmt = null;
        ResultSet nodes = null;
        try{
            stmt = connection.prepareStatement(query);
            prepareStatement(stmt, floor);

            // execute the query
            nodes = stmt.executeQuery();
            while(nodes.next()){
                floorNodes.add(extractNode(nodes));
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

        return floorNodes;
    }

    // EDGE FUNCTIONS
    // returns a list of nodes that are connected to the given node
    public ArrayList<Node> getNodesConnectedTo(Node n) {
        String nodeID = n.getNodeID();
        String query = "SELECT NODE.NodeID, NODE.xcoord, NODE.ycoord, NODE.floor, NODE.building, NODE.nodeType, NODE.longName, NODE.shortName FROM NODE INNER JOIN EDGE ON (NODE.NodeID = EDGE.node1 AND EDGE.node2 = ?) OR (NODE.NodeID = EDGE.node2 AND EDGE.Node1 = ?)";

        return (ArrayList<Node>)(List<?>) executeGetMultiple(query, Node.class, nodeID, nodeID);
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

    // insert an edge. The method will fail and return false if the two nodes it points to
    // do not already exist in the database.
    public boolean insertEdge(Edge e){
        String insertStatement = ("INSERT INTO EDGE VALUES(?,?,?)");
        String node1ID = e.getNode1().getNodeID();
        String node2ID = e.getNode2().getNodeID();

        return executeInsert(insertStatement, e.getEdgeID(), node1ID, node2ID);
    }

    // get an edge. This also pulls out the nodes that edge connects.
    public Edge getEdge(String edgeID){
        String query = "SELECT * FROM EDGE WHERE (EDGEID = ?)";
        return (Edge) executeGetById(query, Edge.class, edgeID);
    }

    public boolean updateEdge(Edge e){
        String query = "UPDATE EDGE SET edgeID=?, NODE1=?, NODE2=? WHERE(EDGEID = ?)";
        return executeUpdate(query, e.getEdgeID(), e.getNode1().getNodeID(), e.getNode2().getNodeID(), e.getEdgeID());
    }

    public boolean deleteEdge(Edge e){
        String query = "DELETE FROM EDGE WHERE (edgeID = ?)";
        return executeUpdate(query, e.getEdgeID());
    }

    public ArrayList<Edge> getAllEdges(){
        return new ArrayList<Edge>();
    }

    public boolean insertReservation(Reservation reservation) {
        String insertStatement = ("INSERT INTO RESERVATION(EVENTNAME, LOCATIONID, STARTTIME, ENDTIME, PRIVACYLEVEL, EMPLOYEEID) VALUES(?, ?, ?, ?, ?, ?)");
        return executeInsert(insertStatement, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(), reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId());
    }

    public Reservation getReservation(int id) {
        String query = "SELECT * FROM RESERVATION WHERE (EVENTID = ?)";
        return (Reservation) executeGetById(query, Reservation.class, id);
    }

    public List<Reservation> getAllReservations() {
        String query = "Select * FROM RESERVATION";
        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, new Object[]{});
    }

    public boolean updateReservation(Reservation reservation) {
        String query = "UPDATE RESERVATION SET eventName=?, locationID=?, startTime=?, endTime=?, privacyLevel=?, employeeID=? WHERE (eventID = ?)";
        return executeUpdate(query, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId(), reservation.getEventID());
    }

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
    public List<Reservation> getReservationBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {
        String query = "SELECT * FROM RESERVATION WHERE (LOCATIONID = ? and (STARTTIME between ? and ?) and (ENDTIME between ? and ?))";
        return (List<Reservation>)(List<?>) executeGetMultiple(query, Reservation.class, id, from, to, from, to);
    }

    public boolean insertEmployee(Employee employee) {
        String insertStatement = ("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?)");
        return executeInsert(insertStatement, employee.getID(), employee.getJob(), employee.isAdmin(), employee.getPassword());
    }

    public Employee getEmployee(int id) {
        String query = "SELECT * FROM EMPLOYEE WHERE (EMPLOYEEID = ?)";
        return (Employee) executeGetById(query, Employee.class, id);
    }

    public List<Employee> getAllEmployees() {
        String query = "Select * FROM EMPLOYEE";
        return (List<Employee>)(List<?>) executeGetMultiple(query, Employee.class, new Object[]{});
    }

    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE EMPLOYEE SET job=?, isAdmin=? WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getJob(), employee.isAdmin(), employee.getID());
    }

    public boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM EMPLOYEE WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getID());
    }

    public boolean insertReservableSpace(ReservableSpace space) {
        String insertQuery = ("INSERT INTO RESERVABLESPACE VALUES(?, ?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, space.getSpaceID(), space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed());
    }

    public ReservableSpace getReservableSpace(String id) {
        String query = "SELECT * FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return (ReservableSpace) executeGetById(query, ReservableSpace.class, id);
    }

    public List<ReservableSpace> getAllReservableSpaces() {
        String query = "Select * FROM RESERVABLESPACE";
        return (List<ReservableSpace>)(List<?>) executeGetMultiple(query, ReservableSpace.class, new Object[]{});
    }

    public boolean updateReservableSpace(ReservableSpace space) {
        String query = "UPDATE RESERVABLESPACE SET spaceName=?, spaceType=?, locationNode=?, timeOpen=?, timeClosed=? WHERE (spaceID = ?)";
        return executeUpdate(query, space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed(), space.getSpaceID());
    }

    public boolean deleteReservableSpace(ReservableSpace space) {
        String query = "DELETE FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return executeUpdate(query, space.getSpaceID());
    }

    public boolean insertITRequest(ITRequest req) {
        String insertQuery = ("INSERT INTO ITREQUEST(notes, locationNodeID, completed, description) VALUES(?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDescription());
    }

    public ITRequest getITRequest(int id) {
        String query = "SELECT * FROM ITREQUEST WHERE (serviceID = ?)";
        return (ITRequest) executeGetById(query, ITRequest.class, id);
    }

    public List<ITRequest> getAllITRequests() {
        String query = "Select * FROM ITREQUEST";
        return (List<ITRequest>)(List<?>) executeGetMultiple(query, ITRequest.class, new Object[]{});
    }

    public boolean updateITRequest(ITRequest req) {
        String query = "UPDATE ITREQUEST SET notes=?, locationNodeID=?, completed=?, description=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDescription(), req.getId());
    }

    public boolean deleteITRequest(ITRequest req) {
        String query = "DELETE FROM ITREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    public List<ITRequest> getAllIncompleteITRequests() {
        String query = "Select * FROM ITREQUEST WHERE (completed = ?)";
        return (List<ITRequest>)(List<?>) executeGetMultiple(query, ITRequest.class, false);
    }

    public boolean insertMedicineRequest(MedicineRequest req) {
        String insertQuery = ("INSERT INTO MEDICINEREQUEST(notes, locationNodeID, completed, medicineType, quantity) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMedicineType(), req.getQuantity());
    }

    public MedicineRequest getMedicineRequest(int id) {
        String query = "SELECT * FROM MEDICINEREQUEST WHERE (serviceID = ?)";
        return (MedicineRequest) executeGetById(query, MedicineRequest.class, id);
    }

    public List<MedicineRequest> getAllMedicineRequests() {
        String query = "Select * FROM MEDICINEREQUEST";
        return (List<MedicineRequest>)(List<?>) executeGetMultiple(query, MedicineRequest.class, new Object[]{});
    }

    public boolean updateMedicineRequest(MedicineRequest req) {
        String query = "UPDATE MEDICINEREQUEST SET notes=?, locationNodeID=?, completed=?, medicineType=?, quantity=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMedicineType(), req.getQuantity(), req.getId());
    }

    public boolean deleteMedicineRequest(MedicineRequest req) {
        String query = "DELETE FROM MEDICINEREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    public List<MedicineRequest> getAllIncompleteMedicineRequests() {
        String query = "Select * FROM MEDICINEREQUEST where (completed = ?)";
        return (List<MedicineRequest>)(List<?>) executeGetMultiple(query, MedicineRequest.class, false);
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

    public void close() {
        try {
            connection.close();
            Connection closeConnection = DriverManager.getConnection(
                    "jdbc:derby:" + databaseName + ";shutdown=true");
            closeConnection.close();
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Database '" + databaseName + "' shutdown successfully!");
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
            statement.addBatch("DROP TABLE EDGE");
            statement.addBatch("DROP TABLE NODE");
            statement.addBatch("DROP TABLE EMPLOYEE");
            statement.addBatch("DROP TABLE ITREQUEST");
            statement.addBatch("DROP TABLE MEDICINEREQUEST");
            statement.addBatch("DROP TABLE RESERVATION");
            statement.addBatch("DROP TABLE RESERVABLESPACE");
            statement.executeBatch();

            this.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }


    //<editor-fold desc="Generic Execution Methods">

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

    private <T> Object executeGetById(String query, Class<T> cls, Object id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result;

        try {
            stmt = connection.prepareStatement(query);

            prepareStatement(stmt, id);

            // execute the query
            rs = stmt.executeQuery();

            // extract results, only one record should be found.
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

    public boolean isNewlyCreated() {
        return newlyCreated;
    }
}
