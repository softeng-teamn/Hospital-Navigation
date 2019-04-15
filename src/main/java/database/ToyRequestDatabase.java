package database;

import service_request.model.sub_model.ToyRequest;

import java.util.List;

class ToyRequestDatabase {
    private final DatabaseService databaseService;

    ToyRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertToyRequest(ToyRequest req) {
        String insertQuery = ("INSERT INTO TOYREQUEST(notes, locationNodeID, completed, toyName, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getToyName(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the IT service_request object with the given ID
     */
    ToyRequest getToyRequest(int id) {
        String query = "SELECT * FROM TOYREQUEST WHERE (serviceID = ?)";
        return (ToyRequest) databaseService.executeGetById(query, ToyRequest.class, id);
    }

    /**
     * @return all IT service_request stored in the database in a List.
     */
    List<ToyRequest> getAllToyRequests() {
        String query = "Select * FROM TOYREQUEST";
        return (List<ToyRequest>) (List<?>) databaseService.executeGetMultiple(query, ToyRequest.class, new Object[]{});
    }

    /**
     * updates a given IT service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateToyRequest(ToyRequest req) {
        String query = "UPDATE TOYREQUEST SET notes=?, locationNodeID=?, completed=?, toyName=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getToyName(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given IT service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteToyRequest(ToyRequest req) {
        String query = "DELETE FROM TOYREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return a list of every IT service_request that has not been completed yet.
     */
    List<ToyRequest> getAllIncompleteToyRequests() {
        String query = "Select * FROM TOYREQUEST WHERE (completed = ?)";
        return (List<ToyRequest>) (List<?>) databaseService.executeGetMultiple(query, ToyRequest.class, false);
    }

    List<ToyRequest> getAllCompleteToyRequests() {
        String query = "Select * FROM TOYREQUEST WHERE (completed = ?)";
        return (List<ToyRequest>) (List<?>) databaseService.executeGetMultiple(query, ToyRequest.class, true);
    }
}