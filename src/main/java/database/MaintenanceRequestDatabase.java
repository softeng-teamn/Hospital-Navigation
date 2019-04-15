package database;

import service_request.model.sub_model.MaintenanceRequest;

import java.util.List;

class MaintenanceRequestDatabase {
    private final DatabaseService databaseService;

    MaintenanceRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertMaintenanceRequest(MaintenanceRequest req) {
        String insertQuery = ("INSERT INTO MAINTENANCEREQUEST(notes, locationNodeID, completed, maintenanceType, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMaintenanceType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    MaintenanceRequest getMaintenanceRequest(int id) {
        String query = "SELECT * FROM MAINTENANCEREQUEST WHERE (serviceID = ?)";
        return (MaintenanceRequest) databaseService.executeGetById(query, MaintenanceRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<MaintenanceRequest> getAllMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST";
        return (List<MaintenanceRequest>) (List<?>) databaseService.executeGetMultiple(query, MaintenanceRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateMaintenanceRequest(MaintenanceRequest req) {
        String query = "UPDATE MAINTENANCEREQUEST SET notes=?, locationNodeID=?, completed=?, maintenanceType=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getMaintenanceType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteMaintenanceRequest(MaintenanceRequest req) {
        String query = "DELETE FROM MAINTENANCEREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<MaintenanceRequest> getAllIncompleteMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST WHERE (completed = ?)";
        return (List<MaintenanceRequest>) (List<?>) databaseService.executeGetMultiple(query, MaintenanceRequest.class, false);
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<MaintenanceRequest> getAllCompleteMaintenanceRequests() {
        String query = "Select * FROM MAINTENANCEREQUEST WHERE (completed = ?)";
        return (List<MaintenanceRequest>) (List<?>) databaseService.executeGetMultiple(query, MaintenanceRequest.class, true);
    }
}