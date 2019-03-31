package service;

/**
 * Thrown in the event that a preexisting database is of a version incompatible with the current application version
 * Easiest fix: delete your database files
 */
public class MismatchedDatabaseVersionException extends Exception {
    public MismatchedDatabaseVersionException(String s) {
        super(s);
    }
}
