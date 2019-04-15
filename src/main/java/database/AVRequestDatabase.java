package database;

import service_request.model.sub_model.AVServiceRequest;

import java.util.List;

class AVRequestDatabase {
    private final DatabaseService databaseService;

    AVRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertAVServiceRequest(AVServiceRequest req) {
        String insertQuery = ("INSERT INTO AVSERVICEREQUEST(notes, locationNodeID, completed, avServiceType, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getAVServiceType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    AVServiceRequest getAVServiceRequest(int id) {
        String query = "SELECT * FROM AVSERVICEREQUEST WHERE (serviceID = ?)";
        return (AVServiceRequest) databaseService.executeGetById(query, AVServiceRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<AVServiceRequest> getAllAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST";
        return (List<AVServiceRequest>) (List<?>) databaseService.executeGetMultiple(query, AVServiceRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateAVServiceRequest(AVServiceRequest req) {
        String query = "UPDATE AVSERVICEREQUEST SET notes=?, locationNodeID=?, completed=?, avServiceType=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getAVServiceType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteAVServiceRequest(AVServiceRequest req) {
        String query = "DELETE FROM AVSERVICEREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<AVServiceRequest> getAllIncompleteAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST WHERE (completed = ?)";
        return (List<AVServiceRequest>) (List<?>) databaseService.executeGetMultiple(query, AVServiceRequest.class, false);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<AVServiceRequest> getAllCompleteAVServiceRequests() {
        String query = "Select * FROM AVSERVICEREQUEST WHERE (completed = ?)";
        return (List<AVServiceRequest>) (List<?>) databaseService.executeGetMultiple(query, AVServiceRequest.class, true);
    }
}