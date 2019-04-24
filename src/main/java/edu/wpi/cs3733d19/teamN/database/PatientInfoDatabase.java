package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.PatientInfoRequest;

import java.util.List;

class PatientInfoDatabase {
    private final DatabaseService databaseService;

    PatientInfoDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param id the id of the service_request to get from the database
     * @return the edu.wpi.cs3733d19.teamN.controller.PatientInfo service_request object with the given ID
     */
    PatientInfoRequest getPatientInfoRequest(int id) {
        String query = "SELECT * FROM PATIENTINFOREQUEST WHERE (serviceID = ?)";
        return (PatientInfoRequest) databaseService.executeGetById(query, PatientInfoRequest.class, id);
    }

    /**
     * @param req the service_request to insert to the database
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertPatientInfoRequest(PatientInfoRequest req) {
        String insertQuery = ("INSERT INTO PATIENTINFOREQUEST(notes, locationNodeID, completed, firstName, lastName, birthDay, description, assignedEmployee) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getFirstName(), req.getLastName(), req.getBirthDay(), req.getDescription(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null));
    }

    /**
     * updates a given edu.wpi.cs3733d19.teamN.controller.PatientInfo service_request in the database.
     *
     * @param req the service_request to update
     * @return true if the update succeeds and false if otherwise
     */
    boolean updatePatientInfoRequest(PatientInfoRequest req) {
        String query = "UPDATE PATIENTINFOREQUEST SET notes=?, locationNodeID=?, completed=?, firstName=?, lastName=?, birthDay=?, description=?, assignedEmployee=? WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getNotes(), req.getLocation().getNodeID(), req.isCompleted(), req.getFirstName(), req.getLastName(), req.getBirthDay(), req.getDescription(), ((req.getAssignedTo() != -1 && req.getAssignedTo() != 0) ? req.getAssignedTo() : null), req.getId());
    }

    /**
     * deletes a given edu.wpi.cs3733d19.teamN.controller.PatientInfo service_request from the database
     *
     * @param req the service_request to delete
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deletePatientInfoRequest(PatientInfoRequest req) {
        String query = "DELETE FROM PATIENTINFOREQUEST WHERE (serviceID = ?)";
        return databaseService.executeUpdate(query, req.getId());
    }

    /**
     * @return all Patient Info service_request stored in the database in a List.
     */
    List<PatientInfoRequest> getAllPatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST";
        return (List<PatientInfoRequest>) (List<?>) databaseService.executeGetMultiple(query, PatientInfoRequest.class, new Object[]{});
    }

    /**
     * @return a list of every Patient Info service_request that has not been completed yet.
     */
    List<PatientInfoRequest> getAllIncompletePatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST WHERE (completed = ?)";
        return (List<PatientInfoRequest>) (List<?>) databaseService.executeGetMultiple(query, PatientInfoRequest.class, false);
    }

    /**
     * @return a list of every Patient Info service_request that has been completed.
     */
    List<PatientInfoRequest> getAllCompletePatientInfoRequests() {
        String query = "Select * FROM PATIENTINFOREQUEST WHERE (completed = ?)";
        return (List<PatientInfoRequest>) (List<?>) databaseService.executeGetMultiple(query, PatientInfoRequest.class, true);
    }
}