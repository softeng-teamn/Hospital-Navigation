package employee.model;

public enum JobType {

    // REMEMBER: if you add a job type, add it to the constraint for the employee table in DatabaseService
    // and add it to the CSVService (importEmployees)
    // and add to showProperRequest in FulfillRequestController


    ADMINISTRATOR,
    DOCTOR,
    NURSE,
    JANITOR,
    SECURITY_PERSONNEL,
    MAINTENANCE_WORKER,
    IT,
    GUEST,
    RELIGIOUS_OFFICIAL,
    GIFT_SERVICES,
    MISCELLANEOUS,
    AV,
    INTERPRETER,
    TOY,
    PATIENT_INFO,
    FLORIST,
    INTERNAL_TRANSPORT,
    EXTERNAL_TRANSPORT


}
