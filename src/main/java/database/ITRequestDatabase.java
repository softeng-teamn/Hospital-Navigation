package database;

import service_request.model.sub_model.ITRequest;

import java.util.List;

class ITRequestDatabase {
    private final DatabaseService databaseService;

    ITRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertITRequest(ITRequest req) {
        String insertQuery = ("INSERT INTO ITREQUEST(notes, locationNodeID, completed, type, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getItRequestType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    ITRequest getITRequest(int id) {
        String query = "SELECT * FROM ITREQUEST WHERE (serviceID = ?)";
        return (ITRequest) databaseService.executeGetById(query, ITRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<ITRequest> getAllITRequests() {
        String query = "Select * FROM ITREQUEST";
        return (List<ITRequest>) (List<?>) databaseService.executeGetMultiple(query, ITRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateITRequest(ITRequest req) {
        String query = "UPDATE ITREQUEST SET notes=?, locationNodeID=?, completed=?, type=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getItRequestType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteITRequest(ITRequest req) {
        String query = "DELETE FROM ITREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<ITRequest> getAllIncompleteITRequests() {
        String query = "Select * FROM ITREQUEST WHERE (completed = ?)";
        return (List<ITRequest>) (List<?>) databaseService.executeGetMultiple(query, ITRequest.class, false);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<ITRequest> getAllCompleteITRequests() {
        String query = "Select * FROM ITREQUEST WHERE (completed = ?)";
        return (List<ITRequest>) (List<?>) databaseService.executeGetMultiple(query, ITRequest.class, true);
    }
}