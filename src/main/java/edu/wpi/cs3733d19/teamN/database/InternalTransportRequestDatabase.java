package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.InternalTransportRequest;

import java.util.List;

class InternalTransportRequestDatabase {
    private final DatabaseService databaseService;

    InternalTransportRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertInternalTransportRequest(InternalTransportRequest req) {
        String insertQuery = ("INSERT INTO INTERNALTRANSPORTREQUEST(notes, locationNodeID, completed, transportType, urgency, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getTransport().name(), req.getUrgency().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the InternalTransportRequest service_request object with the given ID
     */
    InternalTransportRequest getInternalTransportRequest(int id) {
        String query = "SELECT * FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return (InternalTransportRequest) databaseService.executeGetById(query, InternalTransportRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<InternalTransportRequest> getAllInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST";
        return (List<InternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, InternalTransportRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateInternalTransportRequest(InternalTransportRequest req) {
        String query = "UPDATE INTERNALTRANSPORTREQUEST SET notes=?, locationNodeID=?, completed=?, transportType=?, urgency=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getTransport().name(), req.getUrgency().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteInternalTransportRequest(InternalTransportRequest req) {
        String query = "DELETE FROM INTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<InternalTransportRequest> getAllIncompleteInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<InternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, InternalTransportRequest.class, false);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<InternalTransportRequest> getAllCompleteInternalTransportRequests() {
        String query = "Select * FROM INTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<InternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, InternalTransportRequest.class, true);
    }
}