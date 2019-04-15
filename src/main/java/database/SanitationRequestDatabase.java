package database;

import service_request.model.sub_model.SanitationRequest;

import java.util.List;

public class SanitationRequestDatabase {
    private final DatabaseService databaseService;

    public SanitationRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the sanitation service_request object with the given ID
     */
    SanitationRequest getSanitationRequest(int id) {
        String query = "SELECT * FROM SANITATIONREQUEST WHERE (serviceID = ?)";
        return (SanitationRequest) databaseService.executeGetById(query, SanitationRequest.class, id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertSanitationRequest(SanitationRequest req) {
        String insertQuery = ("INSERT INTO SANITATIONREQUEST(notes, locationNodeID, completed, urgency, materialState, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency(), req.getMaterialState(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * updates a given sanitation service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateSanitationRequest(SanitationRequest req) {
        String query = "UPDATE SANITATIONREQUEST SET notes=?, locationNodeID=?, completed=?, urgency=?, materialState=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getUrgency(), req.getMaterialState(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given sanitation service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteSanitationRequest(SanitationRequest req) {
        String query = "DELETE FROM SANITATIONREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return all sanitation service_request stored in the database in a List.
     */
    List<SanitationRequest> getAllSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST";
        return (List<SanitationRequest>) (List<?>) databaseService.executeGetMultiple(query, SanitationRequest.class, new Object[]{});
    }

    /**
     * @return a list of every sanitation service_request that has not been completed yet.
     */
    List<SanitationRequest> getAllIncompleteSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST WHERE (completed = ?)";
        return (List<SanitationRequest>) (List<?>) databaseService.executeGetMultiple(query, SanitationRequest.class, false);
    }

    /**
     * @return a list of every sanitation service_request that has been completed.
     */
    List<SanitationRequest> getAllCompleteSanitationRequests() {
        String query = "Select * FROM SANITATIONREQUEST WHERE (completed = ?)";
        return (List<SanitationRequest>) (List<?>) databaseService.executeGetMultiple(query, SanitationRequest.class, true);
    }
}