package database;

import service_request.model.sub_model.SecurityRequest;

import java.util.List;

class SecurityRequestDatabase {
    private final DatabaseService databaseService;

    SecurityRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the Security service_request object with the given ID
     */
    SecurityRequest getSecurityRequest(int id) {
        String query = "SELECT * FROM SECURITYREQUEST WHERE (serviceID = ?)";
        return (SecurityRequest) databaseService.executeGetById(query, SecurityRequest.class, id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertSecurityRequest(SecurityRequest req) {
        String insertQuery = ("INSERT INTO SECURITYREQUEST(notes, locationNodeID, completed, urgency, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * updates a given Security service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateSecurityRequest(SecurityRequest req) {
        String query = "UPDATE SECURITYREQUEST SET notes=?, locationNodeID=?, completed=?, urgency=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given Security service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteSecurityRequest(SecurityRequest req) {
        String query = "DELETE FROM SECURITYREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return all Security service_request stored in the database in a List.
     */
    List<SecurityRequest> getAllSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST";
        return (List<SecurityRequest>) (List<?>) databaseService.executeGetMultiple(query, SecurityRequest.class, new Object[]{});
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    List<SecurityRequest> getAllIncompleteSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST WHERE (completed = ?)";
        return (List<SecurityRequest>) (List<?>) databaseService.executeGetMultiple(query, SecurityRequest.class, false);
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    List<SecurityRequest> getAllCompleteSecurityRequests() {
        String query = "Select * FROM SECURITYREQUEST WHERE (completed = ?)";
        return (List<SecurityRequest>) (List<?>) databaseService.executeGetMultiple(query, SecurityRequest.class, true);
    }
}