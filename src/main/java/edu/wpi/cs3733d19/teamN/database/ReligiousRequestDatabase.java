package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.ReligiousRequest;

import java.util.List;

class ReligiousRequestDatabase {
    private final DatabaseService databaseService;

    ReligiousRequestDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    ReligiousRequest getReligiousRequest(int id) {
        String query = "SELECT * FROM RELIGIOUSREQUEST WHERE (serviceID = ?)";
        return (ReligiousRequest) databaseService.executeGetById(query, ReligiousRequest.class, id);
    }

    boolean insertReligiousRequest(ReligiousRequest req) {
        String insertQuery = ("INSERT INTO RELIGIOUSREQUEST(notes, locationNodeID, completed, religion, assignedEmployee) VALUES(?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getReligion().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    boolean updateReligiousRequest(ReligiousRequest req) {
        String query = "UPDATE RELIGIOUSREQUEST SET notes=?, locationNodeID=?, completed=?, religion=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getReligion().name(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    boolean deleteReligiousRequest(ReligiousRequest req) {
        String query = "DELETE FROM RELIGIOUSREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    List<ReligiousRequest> getAllReligiousRequests() {
        String query = "Select * FROM RELIGIOUSREQUEST";
        return (List<ReligiousRequest>) (List<?>) databaseService.executeGetMultiple(query, ReligiousRequest.class, new Object[]{});
    }

    List<ReligiousRequest> getAllIncompleteReligiousRequests() {
        String query = "Select * FROM RELIGIOUSREQUEST WHERE (completed = ?)";
        return (List<ReligiousRequest>) (List<?>) databaseService.executeGetMultiple(query, ReligiousRequest.class, false);
    }

    List<ReligiousRequest> getAllCompleteReligiousRequests() {
        String query = "Select * FROM RELIGIOUSREQUEST WHERE (completed = ?)";
        return (List<ReligiousRequest>) (List<?>) databaseService.executeGetMultiple(query, ReligiousRequest.class, true);
    }
}