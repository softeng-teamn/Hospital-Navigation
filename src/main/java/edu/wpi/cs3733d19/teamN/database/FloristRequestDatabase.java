package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.FloristRequest;

import java.util.List;

class FloristRequestDatabase {
    private final DatabaseService databaseService;

    public FloristRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * retrieves a florist request from the database
     *
     * @param id the ID of the service_request to retrieve
     * @return the florist request associated with the given ID.
     */
    FloristRequest getFloristRequest(int id) {
        String query = "SELECT * FROM FLORISTREQUEST WHERE (serviceID = ?)";
        return (FloristRequest) databaseService.executeGetById(query, FloristRequest.class, id);
    }

    /**
     * @param req a florist service_request to insert into the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertFloristRequest(FloristRequest req) {
        String insertQuery = ("INSERT INTO FLORISTREQUEST(notes, locationNodeID, completed, bouquetType, quantity, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getBouquetType(), req.getQuantity(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    boolean updateFloristRequest(FloristRequest req) {
        String query = "UPDATE FLORISTREQUEST SET notes=?, locationNodeID=?, completed=?, bouquetType=?, quantity=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getBouquetType(), req.getQuantity(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * @param req the given service_request to delete
     * @return true if the delete succeeded and false if otherwise.
     */
    boolean deleteFloristRequest(FloristRequest req) {
        String query = "DELETE FROM FLORISTREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    List<FloristRequest> getAllFloristRequests() {
        String query = "Select * FROM FLORISTREQUEST";
        return (List<FloristRequest>) (List<?>) databaseService.executeGetMultiple(query, FloristRequest.class, new Object[]{});
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    List<FloristRequest> getAllIncompleteFloristRequests() {
        String query = "Select * FROM FLORISTREQUEST WHERE (completed = ?)";
        return (List<FloristRequest>) (List<?>) databaseService.executeGetMultiple(query, FloristRequest.class, false);
    }

    /**
     * @return a list of every Security service_request that has not been completed yet.
     */
    List<FloristRequest> getAllCompleteFloristRequests() {
        String query = "Select * FROM FLORISTREQUEST WHERE (completed = ?)";
        return (List<FloristRequest>) (List<?>) databaseService.executeGetMultiple(query, FloristRequest.class, true);
    }
}