package database;

import service_request.model.sub_model.ExternalTransportRequest;
import service_request.model.sub_model.MaintenanceRequest;

import java.util.List;

class ExternalTransportRequestDatabase {
    private final DatabaseService databaseService;

    ExternalTransportRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    ExternalTransportRequest getExtTransRequest(int id) {
        String query = "SELECT * FROM EXTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return (ExternalTransportRequest) databaseService.executeGetById(query, ExternalTransportRequest.class, id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertExtTransRequest(ExternalTransportRequest req) {
        String insertQuery = ("INSERT INTO EXTERNALTRANSPORTREQUEST(notes, locationNodeID, completed, time, transportType, description, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDate(), req.getTransportationType().name(), req.getDescription(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteExtTransRequest(ExternalTransportRequest req) {
        String query = "DELETE FROM EXTERNALTRANSPORTREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<ExternalTransportRequest> getAllExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST";
        return (List<ExternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, ExternalTransportRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateExtTransRequest(ExternalTransportRequest req) {
        String query = "UPDATE EXTERNALTRANSPORTREQUEST SET notes=?, locationNodeID=?, completed=?, time=?, transportType=?, description=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getDate(), req.getTransportationType().name(), req.getDescription(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<ExternalTransportRequest> getAllIncompleteExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<ExternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, ExternalTransportRequest.class, false);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<ExternalTransportRequest> getAllCompleteExtTransRequests() {
        String query = "Select * FROM EXTERNALTRANSPORTREQUEST WHERE (completed = ?)";
        return (List<ExternalTransportRequest>) (List<?>) databaseService.executeGetMultiple(query, MaintenanceRequest.class, true);
    }
}