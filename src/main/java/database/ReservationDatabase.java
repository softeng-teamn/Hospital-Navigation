package database;

import scheduler.model.ReservableSpace;
import scheduler.model.Reservation;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

class ReservationDatabase {
    private final DatabaseService databaseService;

    ReservationDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation reservation to insert into the database
     * @return true or false based on whether the insert succeeded or not
     */
    boolean insertReservation(Reservation reservation) {
        String insertStatement = ("INSERT INTO RESERVATION(EVENTNAME, spaceID, STARTTIME, ENDTIME, PRIVACYLEVEL, EMPLOYEEID) VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertStatement, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(), reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId());
    }

    /**
     * retrieves a single reservation from the database with it's ID.
     *
     * @param id id of the reservation to get from the database
     * @return the reservation object corresponding to the ID
     */
    Reservation getReservation(int id) {
        String query = "SELECT * FROM RESERVATION WHERE (EVENTID = ?)";
        return (Reservation) databaseService.executeGetById(query, Reservation.class, id);
    }

    /**
     * retrieves all reservations from the database
     *
     * @return a list of all reservations in the database
     */
    ArrayList<Reservation> getAllReservations() {
        String query = "Select * FROM RESERVATION";
        return (ArrayList<Reservation>) (List<?>) databaseService.executeGetMultiple(query, Reservation.class, new Object[]{});
    }

    /**
     * updates a reservation in the database.
     *
     * @param reservation reservation to update in the database
     * @return true or false based on whether the insert succeeded or not
     */
    boolean updateReservation(Reservation reservation) {
        String query = "UPDATE RESERVATION SET eventName=?, spaceID=?, startTime=?, endTime=?, privacyLevel=?, employeeID=? WHERE (eventID = ?)";
        return databaseService.executeUpdate(query, reservation.getEventName(), reservation.getLocationID(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getPrivacyLevel(), reservation.getEmployeeId(), reservation.getEventID());
    }

    /**
     * Removes a reservation from the database.
     *
     * @param reservation a reservation object
     * @return true or false based on whether the insert succeeded or not
     */
    boolean deleteReservation(Reservation reservation) {
        String query = "DELETE FROM RESERVATION WHERE (eventID = ?)";
        return databaseService.executeUpdate(query, reservation.getEventID());
    }

    /**
     * Query all reservations made for a given {@link ReservableSpace}.
     *
     * @param id the spaceID of the ReservableSpace being requested for
     * @return a list of the requested reservations
     */
    List<Reservation> getReservationsBySpaceId(String id) {
        String query = "SELECT * FROM RESERVATION WHERE (spaceID = ?)";
        return (List<Reservation>) (List<?>) databaseService.executeGetMultiple(query, Reservation.class, id);
    }

    /**
     * Get all reservations made for the given space ID that fall entirely within from and to.
     *
     * @param id   the spaceID of the reservable space being requested for
     * @param from start of the window
     * @param to   end of the window
     * @return a list of the requested reservations
     */
    List<Reservation> getReservationsBySpaceIdBetween(String id, GregorianCalendar from, GregorianCalendar to) {
        String query = "SELECT * FROM RESERVATION WHERE (spaceID = ? and (STARTTIME between ? and ?) and (ENDTIME between ? and ?))";
        return (List<Reservation>) (List<?>) databaseService.executeGetMultiple(query, Reservation.class, id, from, to, from, to);
    }
}