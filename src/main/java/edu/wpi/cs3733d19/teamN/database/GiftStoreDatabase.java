package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.GiftStoreRequest;

import java.util.List;

class GiftStoreDatabase {
    private final DatabaseService databaseService;

    GiftStoreDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the GiftRequest service_request object with the given ID
     */
    GiftStoreRequest getGiftStoreRequest(int id) {
        String query = "SELECT * FROM GIFTSTOREREQUEST WHERE (serviceID = ?)";
        return (GiftStoreRequest) databaseService.executeGetById(query, GiftStoreRequest.class, id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertGiftStoreRequest(GiftStoreRequest req) {
        String insertQuery = ("INSERT INTO GIFTSTOREREQUEST(notes, locationNodeID, completed, gType, patientName, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getgType().name(), req.getPatientName(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * updates a given GiftStoreRequest service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateGiftStoreRequest(GiftStoreRequest req) {
        String query = "UPDATE GIFTSTOREREQUEST SET notes=?, locationNodeID=?, completed=?, gType=?, patientName=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getgType().name(), req.getPatientName(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given GiftStoreRequest service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteGiftStoreRequest(GiftStoreRequest req) {
        String query = "DELETE FROM GIFTSTOREREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every GiftStoreRequests service_request that has not been completed yet.
     */
    List<GiftStoreRequest> getAllIncompleteGiftStoreRequests() {
        String query = "Select * FROM GIFTSTOREREQUEST WHERE (completed = ?)";
        return (List<GiftStoreRequest>) (List<?>) databaseService.executeGetMultiple(query, GiftStoreRequest.class, false);
    }

    /**
     * @return a list of every GiftStoreRequests service_request that has not been completed yet.
     */
    List<GiftStoreRequest> getAllCompleteGiftStoreRequests() {
        String query = "Select * FROM GIFTSTOREREQUEST WHERE (completed = ?)";
        return (List<GiftStoreRequest>) (List<?>) databaseService.executeGetMultiple(query, GiftStoreRequest.class, true);
    }

    List<GiftStoreRequest> getAllGiftStoreRequests() {
        String query = "Select * FROM GIFTSTOREREQUEST";
        return (List<GiftStoreRequest>) (List<?>) databaseService.executeGetMultiple(query, GiftStoreRequest.class);
    }
}