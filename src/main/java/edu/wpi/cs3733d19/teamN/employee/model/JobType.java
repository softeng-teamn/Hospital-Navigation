package edu.wpi.cs3733d19.teamN.employee.model;

/**
 * job types for employees
 */
public enum JobType {

    // REMEMBER: if you add a job getType, add it to the constraint for the employee table in DatabaseService
    // and add it to the CSVService (importEmployees)
    // and add to showProperRequest in FulfillRequestController


    ADMINISTRATOR("Administrator"),
    DOCTOR("Doctor"),
    NURSE("Nurse"),
    JANITOR("Janitor"),
    SECURITY_PERSONNEL("Security Personnel"),
    MAINTENANCE_WORKER("Maintenance Worker"),
    IT("IT"),
    GUEST("Guest"),
    RELIGIOUS_OFFICIAL("Religious Official"),
    GIFT_SERVICES("Gift Services"),
    MISCELLANEOUS("Miscellaneous"),
    AV("AV"),
    INTERPRETER("Interpreter"),
    TOY("Toy"),
    PATIENT_INFO("Patient Info"),
    FLORIST("Florist"),
    INTERNAL_TRANSPORT("Internal Transport"),
    EXTERNAL_TRANSPORT("External Transport");

    private String string;

    JobType(String name){string = name;}

    @Override
    public String toString() {
        return string;
    }


}
