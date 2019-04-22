package database;

import employee.model.Employee;

import java.util.List;

class EmployeeDatabase {
    private final DatabaseService databaseService;

    public EmployeeDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param id the id of the employee to get from the database
     * @return the object belonging to the employee of the given ID
     */
    Employee getEmployee(int id) {
        String query = "SELECT * FROM EMPLOYEE WHERE (EMPLOYEEID = ?)";
        return (Employee) databaseService.executeGetById(query, Employee.class, id);
    }

    /**
     * retrieves a list of all employees from the database.
     *
     * @return a list of all employees in the database.
     */
    List<Employee> getAllEmployees() {
        String query = "Select * FROM EMPLOYEE";
        return (List<Employee>) (List<?>) databaseService.executeGetMultiple(query, Employee.class, new Object[]{});
    }

    /**
     * @param employee the employee to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateEmployee(Employee employee) {
        String query = "UPDATE EMPLOYEE SET username=?, firstname=?, lastname=?, job=?, isAdmin=?, phone=?, email=? WHERE (employeeID = ?)";
        return databaseService.executeUpdate(query, employee.getUsername(), employee.getFirstName(), employee.getLastName(), employee.getJob().name(), employee.isAdmin(), employee.getPhone(), employee.getEmail(), employee.getID());
    }

    /**
     * @param employee employee to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM EMPLOYEE WHERE (employeeID = ?)";
        return databaseService.executeUpdate(query, employee.getID());
    }

    /**
     * Retrieves the Employee with the given username
     *
     * @param username the username of the desired employee
     * @return the employee object associated with the given username.
     */
    Employee getEmployeeByUsername(String username) {
        String query = "SELECT * FROM EMPLOYEE WHERE (username = ?)";
        return (Employee) databaseService.executeGetById(query, Employee.class, username);
    }
}