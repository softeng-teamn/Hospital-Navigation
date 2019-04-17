package elevator_api;

import employee.model.Employee;

import java.sql.*;
import java.util.*;

/**
 * Database controller for the API's internal database
 */
class ApiDatabaseService {
    public static final String DATABASE_NAME = "internal-transport-request-db";

    private Connection connection;
    public boolean createFlag;
    public static String team;
    public static boolean callElev;
    public String callElevTo;

    public void setCallElevTo(String callElevatorTo) {
        callElevTo = callElevatorTo;
    }


    private static class SingletonHelper {
        private static final ApiDatabaseService dbs = new ApiDatabaseService();
    }

    static ApiDatabaseService getDatabaseService() {
        return SingletonHelper.dbs;
    }

    /**
     * Construct a DatabaseService
     *
     * @throws SQLException on DB connection creation error
     */
    private ApiDatabaseService() {
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

            this.connection = conn;

            if (createFlag) {
                this.createTables();
            }
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

    /**
     * creates tables in the database if they do not already exist.
     */
    private void createTables() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.addBatch("CREATE TABLE EMPLOYEE(employeeID int PRIMARY KEY, username varchar(255) UNIQUE)");

            statement.addBatch("CREATE TABLE INTERNALTRANSPORTREQUEST(serviceID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), notes varchar(255), locationNodeID varchar (255), completed boolean, transportType varchar(40), assignedEmployee int, urgency varchar(30))");

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * @param employee the employee to insert into the database
     * @return true if the insert succeeded or false if otherwise.
     */
    boolean insertEmployee(Employee employee) {
        String insertStatement = ("INSERT INTO EMPLOYEE VALUES(?, ?)");
        return executeInsert(insertStatement, employee.getID(), employee.getUsername());
    }

    /**
     * @param id the id of the employee to get from the database
     * @return the object belonging to the employee of the given ID
     */
    Employee getEmployee(int id) {
        String query = "SELECT * FROM EMPLOYEE WHERE (EMPLOYEEID = ?)";
        return (Employee) executeGetById(query, Employee.class, id);
    }

    /**
     * retrieves a list of all employees from the database.
     *
     * @return a list of all employees in the database.
     */
    ArrayList<Employee> getAllEmployees() {
        String query = "Select * FROM EMPLOYEE";
        return (ArrayList<Employee>) (List<?>) executeGetMultiple(query, Employee.class, new Object[]{});
    }


    public static boolean isCallElev() {
        return callElev;
    }

    public static void setCallElev(boolean callElev) {
        ApiDatabaseService.callElev = callElev;
    }

    /**
     * @param employee the employee to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateEmployee(Employee employee) {
        String query = "UPDATE EMPLOYEE SET username=? WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getUsername(), employee.getJob().name(), employee.isAdmin(), employee.getPhone(), employee.getEmail(), employee.getID());
    }

    /**
     * @param employee employee to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM EMPLOYEE WHERE (employeeID = ?)";
        return executeUpdate(query, employee.getID());
    }

    Employee getEmployeeByUsername(String username) {
        String query = "SELECT * FROM EMPLOYEE WHERE (username = ?)";
        return (Employee) executeGetById(query, Employee.class, username);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertInternalTransportRequest(ApiInternalTransportRequest req) {
        String insertQuery = ("INSERT INTO INTERNALTRANSPORTREQUEST(notes, locationNodeID, transportType, assignedEmployee, urgency) VALUES(?, ?, ?, ?, ?)");
        return executeInsert(insertQuery, req.getNotes(), req.getLocation(), req.getTransport().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getUrgency().name());
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the InternalTransportRequest service_request object with the given ID
     */
    ApiInternalTransportRequest getInternalTransportRequest(int id) {
        String query = "SELECT * FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return (ApiInternalTransportRequest) executeGetById(query, ApiInternalTransportRequest.class, id);
    }

    /**
     * @return all internal service requests stored in the database in a List.
     */
    ArrayList<ApiInternalTransportRequest> getAllInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST";
        return (ArrayList<ApiInternalTransportRequest>) (List<?>) executeGetMultiple(query, ApiInternalTransportRequest.class, new Object[]{});
    }

    /**
     * updates a given service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateInternalTransportRequest(ApiInternalTransportRequest req) {
        String query = "UPDATE INTERNALTRANSPORTREQUEST SET notes=?, locationNodeID=?, transportType=?, assignedEmployee=?, urgency=? WHERE (serviceID = ?)";
        return executeUpdate(query, req.getNotes(), req.getLocation(), req.getTransport().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getUrgency().name(), req.getId());
    }

    /**
     * deletes a given service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteInternalTransportRequest(ApiInternalTransportRequest req) {
        String query = "DELETE FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return executeUpdate(query, req.getId());
    }

    /**
     * closes the connection when the database is done.
     */
    void close() {
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

    /**
     * @param rs
     * @param cls
     * @param <T>
     * @return
     * @throws SQLException when extraction fails.
     */
    private <T> Object extractGeneric(ResultSet rs, Class<T> cls) throws SQLException {
        if (cls.equals(Employee.class)) return extractEmployee(rs);
        else if (cls.equals(ApiInternalTransportRequest.class)) return extractInternalTransportRequest(rs);
        else return null;
    }


    private ApiInternalTransportRequest extractInternalTransportRequest(ResultSet rs) throws SQLException {
        // locationNodeID varchar (255), completed boolean, transportType varchar(40)
        int serviceID = rs.getInt("serviceID");
        String notes = rs.getString("notes");
        String locationNodeId = rs.getString("locationNodeID");
        String enumVal = rs.getString("transportType");
        int assignedEmployee = rs.getInt("assignedEmployee");
        ApiInternalTransportRequest.Urgency urgency = ApiInternalTransportRequest.Urgency.valueOf(rs.getString("urgency"));

        ApiInternalTransportRequest req = new ApiInternalTransportRequest(serviceID, notes, locationNodeId, ApiInternalTransportRequest.TransportType.valueOf(enumVal), urgency);
        req.setAssignedTo(assignedEmployee);
        return req;
    }

    private Employee extractEmployee(ResultSet rs) throws SQLException {
        // Extract data
        int empID = rs.getInt("employeeID");
        String username = rs.getString("username");

        Employee emp = new Employee(empID, username, null, false, null);
        return emp;
    }


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

    public static String getTeam() {
        return team;
    }

    public static void setTeam(String team) {
        ApiDatabaseService.team = team;
    }
}
