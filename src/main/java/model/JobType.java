package model;

public enum JobType {

    // REMEMBER: if you add a job type, add it to the constraint for the employee table in DatabaseService
    // and add it to the CSVService (importEmployees)
    // and add to showProperRequest in FulfillRequestController

    ADMINISTRATOR,
    DOCTOR,
    NURSE,
    SECURITY_PERSONNEL,
    JANITOR,
    MAINTENANCE_WORKER,
    GUEST,
    IT;


}