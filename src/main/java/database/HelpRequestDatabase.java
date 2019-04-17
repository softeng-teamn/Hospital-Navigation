package database;

import service_request.model.sub_model.HelpRequest;

import java.util.List;

public class HelpRequestDatabase {
    private final DatabaseService databaseService;

    HelpRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertHelpRequest(HelpRequest req) {
        String insertQuery = ("INSERT INTO HELPREQUEST(notes, locationNodeID, completed, assignedEmployee) VALUES(?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    HelpRequest getHelpRequest(int id) {
        String query = "SELECT * FROM HELPREQUEST WHERE (serviceID = ?)";
        return (HelpRequest) databaseService.executeGetById(query, HelpRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<HelpRequest> getAllHelpRequests() {
        String query = "Select * FROM HELPREQUEST";
        return (List<HelpRequest>) (List<?>) databaseService.executeGetMultiple(query, HelpRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateHelpRequest(HelpRequest req) {
        String query = "UPDATE HELPREQUEST SET notes=?, locationNodeID=?, completed=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteHelpRequest(HelpRequest req) {
        String query = "DELETE FROM HELPREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<HelpRequest> getAllIncompleteHelpRequests() {
        String query = "Select * FROM HELPREQUEST WHERE (completed = ?)";
        return (List<HelpRequest>) (List<?>) databaseService.executeGetMultiple(query, HelpRequest.class, false);
    }

    List<HelpRequest> getAllCompleteHelpRequests() {
        String query = "Select * FROM HELPREQUEST WHERE (completed = ?)";
        return (List<HelpRequest>) (List<?>) databaseService.executeGetMultiple(query, HelpRequest.class, true);
    }
}
