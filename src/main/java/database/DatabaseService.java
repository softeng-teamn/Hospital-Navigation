package database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import employee.model.Employee;
import employee.model.JobType;
import map.Edge;
import map.Node;
import scheduler.model.ReservableSpace;
import scheduler.model.Reservation;
import service_request.model.sub_model.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Function;

/** Singleton that controls access to the database
 *
 */
public class DatabaseService {

    public static final String DATABASE_NAME = "hospital-db";
    public static final Integer DATABASE_VERSION = 10;

    // delegates for table access methods
    private final NodeDatabase nodeDatabase = new NodeDatabase(this);
    private final EdgeDatabase edgeDatabase = new EdgeDatabase(this);
    private final ReservationDatabase reservationDatabase = new ReservationDatabase(this);
    private final EmployeeDatabase employeeDatabase = new EmployeeDatabase(this);
    private final ReservableSpaceDatabase reservableSpaceDatabase = new ReservableSpaceDatabase(this);
    private final database.ITRequestDatabase ITRequestDatabase = new ITRequestDatabase(this);
    private final MedicineRequestDatabase medicineRequestDatabase = new MedicineRequestDatabase(this);
    private final FloristRequestDatabase floristRequestDatabase = new FloristRequestDatabase(this);
    private final SecurityRequestDatabase securityRequestDatabase = new SecurityRequestDatabase(this);
    private final SanitationRequestDatabase sanitationRequestDatabase = new SanitationRequestDatabase(this);
    private final GiftStoreDatabase giftStoreDatabase = new GiftStoreDatabase(this);
    private final ReligiousRequestDatabase religiousRequestDatabase = new ReligiousRequestDatabase(this);
    private final InterpreterRequestDatabase interpreterRequestDatabase = new InterpreterRequestDatabase(this);
    private final PatientInfoDatabase patientInfoDatabase = new PatientInfoDatabase(this);
    private final InternalTransportRequestDatabase internalTransportRequestDatabase = new InternalTransportRequestDatabase(this);
    private final ExternalTransportRequestDatabase externalTransportRequestDatabase = new ExternalTransportRequestDatabase(this);
    private final database.AVRequestDatabase AVRequestDatabase = new AVRequestDatabase(this);
    private final MaintenanceRequestDatabase maintenanceRequestDatabase = new MaintenanceRequestDatabase(this);
    private final ToyRequestDatabase toyRequestDatabase = new ToyRequestDatabase(this);
    private final HelpRequestDatabase helpRequestDatabase = new HelpRequestDatabase(this);

    private Connection connection;
    private ArrayList<Function<Void, Void>> nodeCallbacks;
    private ArrayList<Function<Void, Void>> edgeCallbacks;
    public boolean createFlag;

    public Connection getConnection() {
        return connection;
    }

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

    /**
     * Deletes the local database files from disk. Used exclusively for testing.
     */
    public static void wipeOutFiles() {
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
            statement.addBatch("CREATE TABLE NODE (nodeID varchar(255) PRIMARY KEY, xcoord int, ycoord int, floor varchar(255), building varchar(255), nodeType varchar(255), longName varchar(255), shortName varchar(255), isClosed boolean default false)");

            statement.addBatch("CREATE TABLE EDGE(edgeID varchar(21) PRIMARY KEY, node1 varchar(255), node2 varchar(255))");

            statement.addBatch("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, username varchar(255) UNIQUE, firstname varchar(255), lastname varchar(255), job varchar(25), isAdmin boolean, password varchar(50), phone varchar(255), email varchar(255), CONSTRAINT chk_job CHECK (job IN ('ADMINISTRATOR', 'DOCTOR', 'NURSE', 'JANITOR', 'SECURITY_PERSONNEL', 'MAINTENANCE_WORKER', 'IT', 'GUEST', 'RELIGIOUS_OFFICIAL', 'GIFT_SERVICES', 'MISCELLANEOUS', 'AV', 'INTERPRETER', 'TOY', 'PATIENT_INFO', 'FLORIST', 'INTERNAL_TRANSPORT', 'EXTERNAL_TRANSPORT')))");

            statement.addBatch("CREATE TABLE RESERVATION(eventID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), eventName varchar(50), spaceID varchar(30), startTime timestamp, endTime timestamp, privacyLevel int, employeeID int)");
            statement.addBatch("CREATE TABLE RESERVABLESPACE(spaceID varchar(30) PRIMARY KEY, spaceName varchar(50), spaceType varchar(4), locationNode varchar(10), timeOpen timestamp, timeClosed timestamp)");

            statement.addBatch("CREATE TABLE ITREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, type varchar(30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE MEDICINEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, medicineType varchar(50), quantity double, assignedEmployee int)");
            statement.addBatch("CREATE TABLE FLORISTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, bouquetType varchar(255), quantity int, assignedEmployee int)");
            statement.addBatch("CREATE TABLE SECURITYREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, urgency varchar(30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE SANITATIONREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, urgency varchar(255), materialState varchar(255), assignedEmployee int)");
            statement.addBatch("CREATE TABLE RELIGIOUSREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),notes varchar(225), locationNodeID varchar (255), completed boolean, religion varchar (30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE GIFTSTOREREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean,  gType varchar(30), patientName varchar(255), assignedEmployee int)");
            statement.addBatch("CREATE TABLE INTERNALTRANSPORTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar (255), completed boolean, transportType varchar(40), urgency varchar(40), assignedEmployee int)");
            statement.addBatch("CREATE TABLE PATIENTINFOREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, firstName varchar(255), lastName varchar(255), birthDay varchar(255), description varchar(255), assignedEmployee int)");
            statement.addBatch("CREATE TABLE AVSERVICEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, avServiceType varchar(30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE EXTERNALTRANSPORTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, time TIMESTAMP, transportType varchar(30), description varchar(255), assignedEmployee int)");
            statement.addBatch("CREATE TABLE MAINTENANCEREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, maintenanceType varchar(30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE TOYREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255),completed boolean, toyName varchar(255), assignedEmployee int)");
            statement.addBatch("CREATE TABLE INTERPRETERREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255), completed boolean, language varchar(30), assignedEmployee int)");
            statement.addBatch("CREATE TABLE HELPREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar(255),completed boolean, assignedEmployee int)");


            // DATABASE CONSTRAINTS
            statement.addBatch("CREATE TABLE META_DB_VER(id int PRIMARY KEY , version int)");
            statement.addBatch("INSERT INTO META_DB_VER values(0, " + getDatabaseVersion() + ")");

            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node1) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE EDGE ADD FOREIGN KEY (node2) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE RESERVATION ADD FOREIGN KEY (employeeID) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");

            statement.addBatch("ALTER TABLE ITREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE MEDICINEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE SECURITYREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE FLORISTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE GIFTSTOREREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE SANITATIONREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE RELIGIOUSREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE INTERNALTRANSPORTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE PATIENTINFOREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE INTERPRETERREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE EXTERNALTRANSPORTREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE AVSERVICEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE MAINTENANCEREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE (nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE TOYREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE HELPREQUEST ADD FOREIGN KEY (locationNodeID) REFERENCES NODE(nodeID) ON DELETE CASCADE");
          
            statement.addBatch("ALTER TABLE ITREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE MEDICINEREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE SECURITYREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE FLORISTREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE GIFTSTOREREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE SANITATIONREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE RELIGIOUSREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE INTERNALTRANSPORTREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE PATIENTINFOREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE INTERPRETERREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE EXTERNALTRANSPORTREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE AVSERVICEREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE MAINTENANCEREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE TOYREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");
            statement.addBatch("ALTER TABLE HELPREQUEST ADD FOREIGN KEY (assignedEmployee) REFERENCES EMPLOYEE(employeeID) ON DELETE CASCADE");

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
        return nodeDatabase.insertNode(n);
    }

    /**
     * Update the database entry for a given node
     *
     * @param n A {@link Node} to update. The node must have a valid ID
     * @return true if the update is successful, false otherwise
     */
    public boolean updateNode(Node n) {
        return nodeDatabase.updateNode(n);
    }

    /**
     * Delete a node if it exists
     *
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    public boolean deleteNode(Node n) {
        return nodeDatabase.deleteNode(n);
    }

    /**
     * retrieves the given node from the database
     *
     * @param nodeID the ID of the node to be retrieved
     * @return a node with the given ID
     */
    public Node getNode(String nodeID) {
        return nodeDatabase.getNode(nodeID);
    }

    /** Takes a list of nodes and adds all of them to the database.
     * @param nodes A list of nodes to add to the database
     * @return true if the insertion is successful, and false if otherwise
     */
    public boolean insertAllNodes(List<Node> nodes) {

        // Track the status of the insert

        return nodeDatabase.insertAllNodes(nodes);
    }

    /**
     * Returns all nodes in the database.
     *
     * @return list of all nodes in the database
     */
    public ArrayList<Node> getAllNodes() {
        return nodeDatabase.getAllNodes();
    }

    /**
     * get nodes filtered by specific type
     *
     * @param filterOut the parameter to exclude specific nodes by
     * @return an arraylist of nodes that do not include the specified parameter
     */
    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", justification = "Not a security issue - just add question marks based on number of types to filter out.")
    public ArrayList<Node> getNodesFilteredByType(String... filterOut) {
        return nodeDatabase.getNodesFilteredByType(filterOut);
    }

    /**
     * get all nodes from the specified floor
     *
     * @param floor the floor to retrieve all nodes from
     * @return an arraylist of all nodes on the given floor.
     */
    public ArrayList<Node> getNodesByFloor(String floor) {
        return nodeDatabase.getNodesByFloor(floor);
    }

    public int getNumNodeTypeByFloor(String nodeType, String floor) {
        return nodeDatabase.getNumNodeTypeByFloor(nodeType, floor);
    }

    /** method name
     * @param floor the floor
     * @param type the node type
     * @return all nodes on the floor of the given type
     */
    ArrayList<Node> getNodesByFloorByType(String floor, String type){ return nodeDatabase.getNodesByFloorByType(floor, type);}

    // EDGE FUNCTIONS

    /**
     * returns a list of nodes that are connected to the given node
     *
     * @param n the node to retrieve all nodes connected to from
     * @return A list of all nodes connected to the given node.
     */
    public ArrayList<Node> getNodesConnectedTo(Node n) {

        return nodeDatabase.getNodesConnectedTo(n);
    }

    /**
     * insert an edge. The method will fail and return false if the two nodes it points to
     * do not already exist in the database.
     *
     * @param e the edge to insert
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertEdge(Edge e) {

        return edgeDatabase.insertEdge(e);
    }

    /**
     * get an edge. This also pulls out the nodes that edge connects.
     *
     * @param edgeID the ID of the edge to retrieve
     * @return the edge corresponding to the given ID
     */
    public Edge getEdge(String edgeID) {
        return edgeDatabase.getEdge(edgeID);
    }

    /**
     * updates an edge with new node IDs.
     *
     * @param e the edge to update
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateEdge(Edge e) {
        return edgeDatabase.updateEdge(e);
    }

    /**
     * Deletes an edge from the database.
     *
     * @param e edge to delete from the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteEdge(Edge e) {
        return edgeDatabase.deleteEdge(e);
    }

    /** Retrieves every edge from the database.
     * @return An ArrayList of every edge in the database.
     */
    public ArrayList<Edge> getAllEdges() {
        return edgeDatabase.getAllEdges();
    }

    /** Retrieves all edges connected to the given node
     * @param nodeId The node to retrieve edges from
     * @return All edges connected to the given node.
     */
    public ArrayList<Edge> getAllEdgesWithNode(String nodeId) {
        return edgeDatabase.getAllEdgesWithNode(nodeId);
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation reservation to insert into the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean insertReservation(Reservation reservation) {
        return reservationDatabase.insertReservation(reservation);
    }

    /**
     * retrieves a single reservation from the database with it's ID.
     *
     * @param id id of the reservation to get from the database
     * @return the reservation object corresponding to the ID
     */
    public Reservation getReservation(int id) {
        return reservationDatabase.getReservation(id);
    }

    /**
     * retrieves all reservations from the database
     *
     * @return a list of all reservations in the database
     */
    public ArrayList<Reservation> getAllReservations() {
        return reservationDatabase.getAllReservations();
    }

    /**
     * updates a reservation in the database.
     *
     * @param reservation reservation to update in the database
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean updateReservation(Reservation reservation) {
        return reservationDatabase.updateReservation(reservation);
    }

    /**
     * Removes a reservation from the database.
     *
     * @param reservation a reservation object
     * @return true or false based on whether the insert succeeded or not
     */
    public boolean deleteReservation(Reservation reservation) {
        return reservationDatabase.deleteReservation(reservation);
    }

    /**
     * Query all reservations made for a given {@link ReservableSpace}.
     *
     * @param id the spaceID of the ReservableSpace being requested for
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceId(String id) {
        return reservationDatabase.getReservationsBySpaceId(id);
    }

    /**
     * Get all reservations made for the given space ID that fall entirely within from and to.
     *
     * @param id   the spaceID of the reservable space being requested for
     * @param from start of the window
     * @param to   end of the window
     * @return a list of the requested reservations
     */
    public List<Reservation> getReservationsBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {

        return reservationDatabase.getReservationsBySpaceIdBetween(id, from, to);
    }

    /**
     * Get all reservations made for the given space ID that conflict with from and to.
     *
     * @param id   the spaceID of the reservable space being requested for
     * @param from start of the window
     * @param to   end of the window
     * @return a list of the requested reservations
     */
    public List<Reservation> getConflictingReservationsBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {

        return reservationDatabase.getConflictingReservationsBySpaceIdBetween(id, from, to);
    }

    /**
     * @param employee the employee to insert into the database
     * @return true if the insert succeeded or false if otherwise.
     */
    public boolean insertEmployee(Employee employee) {
        String insertStatement = ("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return executeInsert(insertStatement, employee.getID(), employee.getUsername(), employee.getFirstName(), employee.getLastName(), employee.getJob().name(), employee.isAdmin(), employee.getPassword(), employee.getPhone(), employee.getEmail());
    }

    /**
     * @param id the id of the employee to get from the database
     * @return the object belonging to the employee of the given ID
     */
    public Employee getEmployee(int id) {
        return employeeDatabase.getEmployee(id);
    }

    /**
     * retrieves a list of all employees from the database.
     * @return a list of all employees in the database.
     */
    public List<Employee> getAllEmployees() {
        return employeeDatabase.getAllEmployees();
    }

    /**
     * @param employee the employee to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateEmployee(Employee employee) {
        return employeeDatabase.updateEmployee(employee);
    }

    /**
     * @param employee employee to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteEmployee(Employee employee) {
        return employeeDatabase.deleteEmployee(employee);
    }

    /** Retrieves the Employee with the given username
     * @param username the username of the desired employee
     * @return the employee object associated with the given username.
     */
    public Employee getEmployeeByUsername(String username) {
        return employeeDatabase.getEmployeeByUsername(username);
    }

    /**
     * @param space space to insert into the database
     * @return true if the insert succeeded and false if otherwise
     */
    public boolean insertReservableSpace(ReservableSpace space) {
        return reservableSpaceDatabase.insertReservableSpace(space);
    }

    /**
     * @param id ID of the space to get from the database
     * @return a reservable space with matching ID to the given one
     */
    public ReservableSpace getReservableSpace(String id) {
        return reservableSpaceDatabase.getReservableSpace(id);
    }

    /**
     * @return a list of all reservable spaces in the database
     */
    public List<ReservableSpace> getAllReservableSpaces() {
        return reservableSpaceDatabase.getAllReservableSpaces();
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces with any reservation in the given time frame
     */
    public List<ReservableSpace> getBookedReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {

        return reservableSpaceDatabase.getBookedReservableSpacesBetween(from, to);
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces without any reservations in the given time frame
     */
    public List<ReservableSpace> getAvailableReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {

        return reservableSpaceDatabase.getAvailableReservableSpacesBetween(from, to);
    }

    /**
     * @param space the reservable space to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateReservableSpace(ReservableSpace space) {
        return reservableSpaceDatabase.updateReservableSpace(space);
    }

    /**
     * @param space space to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteReservableSpace(ReservableSpace space) {
        return reservableSpaceDatabase.deleteReservableSpace(space);
    }

    public ReservableSpace getReservableSpaceByNodeID(String nodeID) {
        return reservableSpaceDatabase.getReservableSpaceByNodeID(nodeID);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertITRequest(ITRequest req) {
        return ITRequestDatabase.insertITRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    public ITRequest getITRequest(int id) {
        return ITRequestDatabase.getITRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<ITRequest> getAllITRequests() {
        return ITRequestDatabase.getAllITRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateITRequest(ITRequest req) {
        return ITRequestDatabase.updateITRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteITRequest(ITRequest req) {
        return ITRequestDatabase.deleteITRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<ITRequest> getAllIncompleteITRequests() {
        return ITRequestDatabase.getAllIncompleteITRequests();
    }


    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<ITRequest> getAllCompleteITRequests() {
        return ITRequestDatabase.getAllCompleteITRequests();
    }
    /**
     * @param req the service_request to insert into the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertMedicineRequest(MedicineRequest req) {
        return medicineRequestDatabase.insertMedicineRequest(req);
    }

    /**
     * @param id the id of the medicine service_request to get
     * @return the medicine service_request with the given ID
     */
    public MedicineRequest getMedicineRequest(int id) {
        return medicineRequestDatabase.getMedicineRequest(id);
    }

    /**
     * @return all medicine service_request in the database
     */
    public List<MedicineRequest> getAllMedicineRequests() {
        return medicineRequestDatabase.getAllMedicineRequests();
    }

    /**
     * @param req the given service_request to update
     * @return true if the update succeeded and false if otherwise.
     */
    public boolean updateMedicineRequest(MedicineRequest req) {
        return medicineRequestDatabase.updateMedicineRequest(req);
    }

    /**
     * @param req the given service_request to delete
     * @return true if the delete succeeded and false if otherwise.
     */
    public boolean deleteMedicineRequest(MedicineRequest req) {
        return medicineRequestDatabase.deleteMedicineRequest(req);
    }

    /**
     * @return a list of all medicine service_request that haven't been completed yet
     */
    public List<MedicineRequest> getAllIncompleteMedicineRequests() {
        return medicineRequestDatabase.getAllIncompleteMedicineRequests();
    }

    /**
     * @return a list of all medicine service_request that haven't been completed yet
     */
    public List<MedicineRequest> getAllCompleteMedicineRequests() {
        return medicineRequestDatabase.getAllCompleteMedicineRequests();
    }

    /** retrieves a florist request from the database
     * @param id the ID of the service_request to retrieve
     * @return the florist request associated with the given ID.
     */
    public FloristRequest getFloristRequest(int id) {
        return floristRequestDatabase.getFloristRequest(id);
    }

    /**
     * @param req a florist service_request to insert into the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertFloristRequest(FloristRequest req) {
        return floristRequestDatabase.insertFloristRequest(req);
    }

    /**
     * @param req
     * @return
     */
    public boolean updateFloristRequest(FloristRequest req) {
        return floristRequestDatabase.updateFloristRequest(req);
    }

    /**
     * @param req the given service_request to delete
     * @return true if the delete succeeded and false if otherwise.
     */
    public boolean deleteFloristRequest(FloristRequest req) {
        return floristRequestDatabase.deleteFloristRequest(req);
    }

    public List<FloristRequest> getAllFloristRequests() {
        return floristRequestDatabase.getAllFloristRequests();
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    public List<FloristRequest> getAllIncompleteFloristRequests() {
        return floristRequestDatabase.getAllIncompleteFloristRequests();
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    public List<FloristRequest> getAllCompleteFloristRequests() {
        return floristRequestDatabase.getAllCompleteFloristRequests();
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the Security service_request object with the given ID
     */
    public SecurityRequest getSecurityRequest(int id) {
        return securityRequestDatabase.getSecurityRequest(id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertSecurityRequest(SecurityRequest req) {
        return securityRequestDatabase.insertSecurityRequest(req);
    }

    /**
     * updates a given Security service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateSecurityRequest(SecurityRequest req) {
        return securityRequestDatabase.updateSecurityRequest(req);
    }

    /**
     * deletes a given Security service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteSecurityRequest(SecurityRequest req) {
        return securityRequestDatabase.deleteSecurityRequest(req);
    }

    /**
     * @return all Security service_request stored in the database in a List.
     */
    public List<SecurityRequest> getAllSecurityRequests() {
        return securityRequestDatabase.getAllSecurityRequests();
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    public List<SecurityRequest> getAllIncompleteSecurityRequests() {
        return securityRequestDatabase.getAllIncompleteSecurityRequests();
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    public List<SecurityRequest> getAllCompleteSecurityRequests() {
        return securityRequestDatabase.getAllCompleteSecurityRequests();
    }

  /**
     * @param id the id of the service_request to get from the database
     * @return the sanitation service_request object with the given ID
     */
    public SanitationRequest getSanitationRequest(int id) {
        return sanitationRequestDatabase.getSanitationRequest(id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertSanitationRequest(SanitationRequest req) {
        return sanitationRequestDatabase.insertSanitationRequest(req);
    }

    /**
     * updates a given sanitation service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateSanitationRequest(SanitationRequest req) {
        return sanitationRequestDatabase.updateSanitationRequest(req);
    }

    /**
     * deletes a given sanitation service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteSanitationRequest(SanitationRequest req) {
        return sanitationRequestDatabase.deleteSanitationRequest(req);
    }

    /**
     * @return all sanitation service_request stored in the database in a List.
     */
    public List<SanitationRequest> getAllSanitationRequests() {
        return sanitationRequestDatabase.getAllSanitationRequests();
    }

    /**
     * @return a list of every sanitation service_request that has not been completed yet.
     */
    public List<SanitationRequest> getAllIncompleteSanitationRequests() {
        return sanitationRequestDatabase.getAllIncompleteSanitationRequests();
    }

    /**
     * @return a list of every sanitation service_request that has been completed.
     */
    public List<SanitationRequest> getAllCompleteSanitationRequests() {
        return sanitationRequestDatabase.getAllCompleteSanitationRequests();
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the GiftRequest service_request object with the given ID
     */
    public GiftStoreRequest getGiftStoreRequest(int id) {
        return giftStoreDatabase.getGiftStoreRequest(id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertGiftStoreRequest(GiftStoreRequest req) {
        return giftStoreDatabase.insertGiftStoreRequest(req);
    }


    /**
     * updates a given GiftStoreRequest service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateGiftStoreRequest(GiftStoreRequest req) {
        return giftStoreDatabase.updateGiftStoreRequest(req);
    }

    /**
     * deletes a given GiftStoreRequest service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteGiftStoreRequest(GiftStoreRequest req) {
        return giftStoreDatabase.deleteGiftStoreRequest(req);
    }

    /**
     * @return a list of every GiftStoreRequests service_request that has not been completed yet.
     */
    public List<GiftStoreRequest> getAllIncompleteGiftStoreRequests() {
        return giftStoreDatabase.getAllIncompleteGiftStoreRequests();
    }

    /**
     * @return a list of every GiftStoreRequests service_request that has not been completed yet.
     */
    public List<GiftStoreRequest> getAllCompleteGiftStoreRequests() {
        return giftStoreDatabase.getAllCompleteGiftStoreRequests();
    }

    public List<GiftStoreRequest> getAllGiftStoreRequests() {
        return giftStoreDatabase.getAllGiftStoreRequests();
    }

    public ReligiousRequest getReligiousRequest(int id) {
        return religiousRequestDatabase.getReligiousRequest(id);
    }


    public boolean insertReligiousRequest(ReligiousRequest req) {
        return religiousRequestDatabase.insertReligiousRequest(req);
    }

    public boolean updateReligiousRequest(ReligiousRequest req) {
        return religiousRequestDatabase.updateReligiousRequest(req);
    }

    public boolean deleteReligiousRequest(ReligiousRequest req) {
        return religiousRequestDatabase.deleteReligiousRequest(req);
    }

    public List<ReligiousRequest> getAllReligiousRequests() {
        return religiousRequestDatabase.getAllReligiousRequests();
    }
    public List<ReligiousRequest> getAllIncompleteReligiousRequests() {
        return religiousRequestDatabase.getAllIncompleteReligiousRequests();
    }
    public List<ReligiousRequest> getAllCompleteReligiousRequests() {
        return religiousRequestDatabase.getAllCompleteReligiousRequests();
    }

    public InterpreterRequest getInterpreterRequest(int id) {
        return interpreterRequestDatabase.getInterpreterRequest(id);
    }


    public boolean insertInterpreterRequest(InterpreterRequest req) {
        return interpreterRequestDatabase.insertInterpreterRequest(req);
    }

    public boolean updateInterpreterRequest(InterpreterRequest req) {
        return interpreterRequestDatabase.updateInterpreterRequest(req);
    }

    public boolean deleteInterpreterRequest(InterpreterRequest req) {
        return interpreterRequestDatabase.deleteInterpreterRequest(req);
    }

    public List<InterpreterRequest> getAllInterpreterRequests() {
        return interpreterRequestDatabase.getAllInterpreterRequests();
    }

    public List<InterpreterRequest> getAllIncompleteInterpreterRequests() {
        return interpreterRequestDatabase.getAllIncompleteInterpreterRequests();
    }

    /**
     * @return A list of interpreter requests
     */
    public List<InterpreterRequest> getAllCompleteInterpreterRequests() {
        return interpreterRequestDatabase.getAllCompleteInterpreterRequests();
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the controller.PatientInfo service_request object with the given ID
     */
    public PatientInfoRequest getPatientInfoRequest(int id) {
        return patientInfoDatabase.getPatientInfoRequest(id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertPatientInfoRequest(PatientInfoRequest req) {
        return patientInfoDatabase.insertPatientInfoRequest(req);
    }

    /**
     * updates a given controller.PatientInfo service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updatePatientInfoRequest(PatientInfoRequest req) {
        return patientInfoDatabase.updatePatientInfoRequest(req);
    }

    /**
     * deletes a given controller.PatientInfo service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deletePatientInfoRequest(PatientInfoRequest req) {
        return patientInfoDatabase.deletePatientInfoRequest(req);
    }

    /**
     * @return all Patient Info service_request stored in the database in a List.
     */
    public List<PatientInfoRequest> getAllPatientInfoRequests() {
        return patientInfoDatabase.getAllPatientInfoRequests();
    }

    /**
     * @return a list of every Patient Info service_request that has not been completed yet.
     */
    public List<PatientInfoRequest> getAllIncompletePatientInfoRequests() {
        return patientInfoDatabase.getAllIncompletePatientInfoRequests();
    }

    /**
     * @return a list of every Patient Info service_request that has been completed.
     */
    public List<PatientInfoRequest> getAllCompletePatientInfoRequests() {
        return patientInfoDatabase.getAllCompletePatientInfoRequests();
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertInternalTransportRequest(InternalTransportRequest req) {
        return internalTransportRequestDatabase.insertInternalTransportRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the InternalTransportRequest service_request object with the given ID
     */
    public InternalTransportRequest getInternalTransportRequest(int id) {
        return internalTransportRequestDatabase.getInternalTransportRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<InternalTransportRequest> getAllInternalTransportRequests() {
        return internalTransportRequestDatabase.getAllInternalTransportRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateInternalTransportRequest(InternalTransportRequest req) {
        return internalTransportRequestDatabase.updateInternalTransportRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteInternalTransportRequest(InternalTransportRequest req) {
        return internalTransportRequestDatabase.deleteInternalTransportRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<InternalTransportRequest> getAllIncompleteInternalTransportRequests() {
        return internalTransportRequestDatabase.getAllIncompleteInternalTransportRequests();
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<InternalTransportRequest> getAllCompleteInternalTransportRequests() {
        return internalTransportRequestDatabase.getAllCompleteInternalTransportRequests();
    }

    public ExternalTransportRequest getExtTransRequest(int id) {
        return externalTransportRequestDatabase.getExtTransRequest(id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertExtTransRequest(ExternalTransportRequest req) {
        return externalTransportRequestDatabase.insertExtTransRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteExtTransRequest(ExternalTransportRequest req) {
        return externalTransportRequestDatabase.deleteExtTransRequest(req);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<ExternalTransportRequest> getAllExtTransRequests() {
        return externalTransportRequestDatabase.getAllExtTransRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateExtTransRequest(ExternalTransportRequest req) {
        return externalTransportRequestDatabase.updateExtTransRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<ExternalTransportRequest> getAllIncompleteExtTransRequests() {
        return externalTransportRequestDatabase.getAllIncompleteExtTransRequests();
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<ExternalTransportRequest> getAllCompleteExtTransRequests() {
        return externalTransportRequestDatabase.getAllCompleteExtTransRequests();
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertAVServiceRequest(AVServiceRequest req) {
        return AVRequestDatabase.insertAVServiceRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    public AVServiceRequest getAVServiceRequest(int id) {
        return AVRequestDatabase.getAVServiceRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<AVServiceRequest> getAllAVServiceRequests() {
        return AVRequestDatabase.getAllAVServiceRequests();
    }

    /** updates a given IT service_request in the database.
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateAVServiceRequest(AVServiceRequest req) {
        return AVRequestDatabase.updateAVServiceRequest(req);
    }

    /** deletes a given IT service_request from the database
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteAVServiceRequest(AVServiceRequest req) {
        return AVRequestDatabase.deleteAVServiceRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<AVServiceRequest> getAllIncompleteAVServiceRequests() {
        return AVRequestDatabase.getAllIncompleteAVServiceRequests();
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<AVServiceRequest> getAllCompleteAVServiceRequests() {
        return AVRequestDatabase.getAllCompleteAVServiceRequests();
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertMaintenanceRequest(MaintenanceRequest req) {
        return maintenanceRequestDatabase.insertMaintenanceRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    public MaintenanceRequest getMaintenanceRequest(int id) {
        return maintenanceRequestDatabase.getMaintenanceRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<MaintenanceRequest> getAllMaintenanceRequests() {
        return maintenanceRequestDatabase.getAllMaintenanceRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateMaintenanceRequest(MaintenanceRequest req) {
        return maintenanceRequestDatabase.updateMaintenanceRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteMaintenanceRequest(MaintenanceRequest req) {
        return maintenanceRequestDatabase.deleteMaintenanceRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<MaintenanceRequest> getAllIncompleteMaintenanceRequests() {
        return maintenanceRequestDatabase.getAllIncompleteMaintenanceRequests();
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<MaintenanceRequest> getAllCompleteMaintenanceRequests() {
        return maintenanceRequestDatabase.getAllCompleteMaintenanceRequests();
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertToyRequest(ToyRequest req) {
        return toyRequestDatabase.insertToyRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    public ToyRequest getToyRequest(int id) {
        return toyRequestDatabase.getToyRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<ToyRequest> getAllToyRequests() {
        return toyRequestDatabase.getAllToyRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateToyRequest(ToyRequest req) {
        return toyRequestDatabase.updateToyRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteToyRequest(ToyRequest req) {
        return toyRequestDatabase.deleteToyRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<ToyRequest> getAllIncompleteToyRequests() {
        return toyRequestDatabase.getAllIncompleteToyRequests();
    }

    public List<ToyRequest> getAllCompleteToyRequests() {
        return toyRequestDatabase.getAllCompleteToyRequests();
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    public boolean insertHelpRequest(HelpRequest req) {
        return helpRequestDatabase.insertHelpRequest(req);
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    public HelpRequest getHelpRequest(int id) {
        return helpRequestDatabase.getHelpRequest(id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    public List<HelpRequest> getAllHelpRequests() {
        return helpRequestDatabase.getAllHelpRequests();
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    public boolean updateHelpRequest(HelpRequest req) {
        return helpRequestDatabase.updateHelpRequest(req);
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    public boolean deleteHelpRequest(HelpRequest req) {
        return helpRequestDatabase.deleteHelpRequest(req);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    public List<HelpRequest> getAllIncompleteHelpRequests() {
        return helpRequestDatabase.getAllIncompleteHelpRequests();
    }

    public List<HelpRequest> getAllCompleteHelpRequests() {
        return helpRequestDatabase.getAllCompleteHelpRequests();
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
     * @param stmt The statement to close
     * @param rs The ResultSet to close
     */
    void closeAll(Statement stmt, ResultSet rs) {
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
            statement.addBatch("DELETE FROM HELPREQUEST");

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
            statement.addBatch("ALTER TABLE HELPREQUEST ALTER COLUMN serviceID RESTART WITH 0");


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
    <T> List<Object> executeGetMultiple(String query, Class<T> cls, Object... parameters) {
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
     * @param query the delete or update query to run
     * @param parameters the parameters for that query, to be inserted into the prepared statement.
     * @return a boolean indicating success
     */
    boolean executeUpdate(String query, Object... parameters) {
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
    boolean executeInsert(String insertQuery, Object... values) {
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
     * @param query the query to execute
     * @param cls   the class of object to return
     * @param id    the id that functions as the key to retrieve
     * @return an object of type cls
     */
    <T> Object executeGetById(String query, Class<T> cls, Object id) {
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
     * @param rs ResultSet to extract objects from
     * @param cls the class of objec that should be extracted
     * @return an object of the class specified
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
        else if (cls.equals(HelpRequest.class)) return extractHelpRequest(rs);
        else return null;
    }

    private FloristRequest extractFloristRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String bouquetType = rs.getString("bouquetType");
        int quantity = rs.getInt("quantity");
        int assignedEmployee = rs.getInt("assignedEmployee");
        // construct the new node and return it
        FloristRequest req = new FloristRequest(serviceID, notes, locationNode, completed, bouquetType, quantity);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private SecurityRequest extractSecurityRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("urgency");
        int assignedEmployee = rs.getInt("assignedEmployee");

        SecurityRequest req = new SecurityRequest(serviceID, notes, locationNode, completed, SecurityRequest.Urgency.valueOf(typeString));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private SanitationRequest extractSanitationRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String urgency = rs.getString("urgency");
        String materialState = rs.getString("materialState");
        int assignedEmployee = rs.getInt("assignedEmployee");

        SanitationRequest req = new SanitationRequest(serviceID, notes, locationNode, completed, urgency, materialState);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private GiftStoreRequest extractGiftStoreRequest(ResultSet rs) throws SQLException {
        // Extract data
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("gType");
        String patientName = rs.getString("patientName");
        int assignedEmployee = rs.getInt("assignedEmployee");

        GiftStoreRequest req = new GiftStoreRequest(serviceID, notes, locationNode, completed, GiftStoreRequest.GiftType.valueOf(typeString), patientName);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private ReligiousRequest extractReligiousRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String religion = rs.getString("religion");
        int assignedEmployee = rs.getInt("assignedEmployee");

        ReligiousRequest req = new ReligiousRequest(serviceID, notes, locationNode, completed, ReligiousRequest.Religion.valueOf(religion));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private InterpreterRequest extractInterpreterRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String language = rs.getString("language");
        int assignedEmployee = rs.getInt("assignedEmployee");

        InterpreterRequest req = new InterpreterRequest(serviceID, notes, locationNode, completed, InterpreterRequest.Language.valueOf(language));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private PatientInfoRequest extractPatientInfoRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String birthDay = rs.getString("birthDay");
        String description = rs.getString("description");
        int assignedEmployee = rs.getInt("assignedEmployee");

        PatientInfoRequest req = new PatientInfoRequest(serviceID, notes, locationNode, completed, firstName, lastName, birthDay, description);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private InternalTransportRequest extractInternalTransportRequest(ResultSet rs) throws SQLException {
        // locationNodeID varchar (255), completed boolean, transportType varchar(40)
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String enumVal = rs.getString("transportType");
        String urgency = rs.getString("urgency");
        int assignedEmployee = rs.getInt("assignedEmployee");

        InternalTransportRequest req = new InternalTransportRequest(serviceID, notes, locationNode, completed, InternalTransportRequest.TransportType.valueOf(enumVal), InternalTransportRequest.Urgency.valueOf(urgency));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private ExternalTransportRequest extractExtTransRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String transType = rs.getString("transportType");
        String descript = rs.getString("description");
        Date t = new Date(rs.getTimestamp("time").getTime());
        int assignedEmployee = rs.getInt("assignedEmployee");

        ExternalTransportRequest req = new ExternalTransportRequest(serviceID, notes, locationNode, completed, t, ExternalTransportRequest.TransportationType.valueOf(transType), descript);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private AVServiceRequest extractAVServiceRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("avServiceType");
        int assignedEmployee = rs.getInt("assignedEmployee");

        AVServiceRequest req = new AVServiceRequest(serviceID, notes, locationNode, completed, AVServiceRequest.AVServiceType.valueOf(typeString));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private MaintenanceRequest extractMaintenanceRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String typeString = rs.getString("maintenanceType");
        int assignedEmployee = rs.getInt("assignedEmployee");

        MaintenanceRequest req = new MaintenanceRequest(serviceID, notes, locationNode, completed, MaintenanceRequest.MaintenanceType.valueOf(typeString));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private ToyRequest extractToyRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String toyName = rs.getString("toyName");
        int assignedEmployee = rs.getInt("assignedEmployee");

        ToyRequest req = new ToyRequest(serviceID, notes, locationNode, completed, toyName);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private HelpRequest extractHelpRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        int assignedEmployee = rs.getInt("assignedEmployee");

        HelpRequest req = new HelpRequest(serviceID, notes, locationNode, completed);
        req.setAssignedTo(assignedEmployee);
        return req;
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
        boolean isClosed = rs.getBoolean(name + "isClosed");

        // construct the new node and return it
        Node n =  new Node(newNodeID, newxcoord, newycoord, newFloor, newBuilding, newNodeType, newLongName, newShortName);
        n.setClosed(isClosed);
        return n;
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
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String firstName = rs.getString("firstname");
        String lastName = rs.getString("lastname");

        Employee emp = new Employee(empID, username, firstName, lastName, JobType.valueOf(jobString), isAdmin, password);
        emp.setEmail(email);
        emp.setPhone(phone);
        return emp;
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
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String type = rs.getString("type");
        int assignedEmployee = rs.getInt("assignedEmployee");

        ITRequest req = new ITRequest(serviceID, notes, locationNode, completed, ITRequest.ITRequestType.valueOf(type));
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private MedicineRequest extractMedicineRequest(ResultSet rs) throws SQLException {
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        Node locationNode = nodeDatabase.getNode(rs.getString("locationNodeID"));
        boolean completed = rs.getBoolean("completed");
        String medicineType = rs.getString("medicineType");
        double qty = rs.getDouble("quantity");
        int assignedEmployee = rs.getInt("assignedEmployee");

        MedicineRequest req = new MedicineRequest(serviceID, notes, locationNode, completed, medicineType, qty);
        req.setAssignedTo(assignedEmployee);
        return req;
    }
    ////////////////END EXTRACTION METHODS /////////////////////////////////////////////////////////////////////////////
    //</editor-fold>


    /////////////////////////////////////// CALLBACKS //////////////////////////////////////////////////////////////////


    void executeNodeCallbacks() {
        for (Function<Void, Void> callback : nodeCallbacks) {
            callback.apply(null);
        }
    }

    public void registerNodeCallback(Function<Void, Void> callback) {
        nodeCallbacks.add(callback);
    }


    void executeEdgeCallbacks() {
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
    void prepareStatement(PreparedStatement preparedStatement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setObject(i + 1, values[i]);
        }
    }

    /**
     * Attempt to close a statement
     *
     * @param statement the statement to close. Null is handled
     */
    void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /** Gets the current version of the database, used for verification.
     * @return the current version of the database
     */
    public static int getDatabaseVersion() {
        return DATABASE_VERSION.intValue();
    }
}
