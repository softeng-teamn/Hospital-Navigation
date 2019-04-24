package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.InterpreterRequest;

import java.util.List;

class InterpreterRequestDatabase {
    private final DatabaseService databaseService;

    InterpreterRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    InterpreterRequest getInterpreterRequest(int id) {
        String query = "SELECT * FROM INTERPRETERREQUEST WHERE (serviceID = ?)";
        return (InterpreterRequest) databaseService.executeGetById(query, InterpreterRequest.class, id);
    }

    boolean insertInterpreterRequest(InterpreterRequest req) {
        String insertQuery = ("INSERT INTO INTERPRETERREQUEST(notes, locationNodeID, completed, language, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getLanguageType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    boolean updateInterpreterRequest(InterpreterRequest req) {
        String query = "UPDATE INTERPRETERREQUEST SET notes=?, locationNodeID=?, completed=?, language=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getLanguageType().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    boolean deleteInterpreterRequest(InterpreterRequest req) {
        String query = "DELETE FROM INTERPRETERREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    List<InterpreterRequest> getAllInterpreterRequests() {
        String query = "Select * FROM INTERPRETERREQUEST";
        return (List<InterpreterRequest>) (List<?>) databaseService.executeGetMultiple(query, InterpreterRequest.class, new Object[]{});
    }

    List<InterpreterRequest> getAllIncompleteInterpreterRequests() {
        String query = "Select * FROM INTERPRETERREQUEST WHERE (completed = ?)";
        return (List<InterpreterRequest>) (List<?>) databaseService.executeGetMultiple(query, InterpreterRequest.class, false);
    }

    /**
     * @return A list of interpreter requests
     */
    List<InterpreterRequest> getAllCompleteInterpreterRequests() {
        String query = "Select * FROM INTERPRETERREQUEST WHERE (completed = ?)";
        return (List<InterpreterRequest>) (List<?>) databaseService.executeGetMultiple(query, InterpreterRequest.class, true);
    }
}