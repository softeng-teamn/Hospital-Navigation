package database;

import scheduler.model.ReservableSpace;

import java.util.GregorianCalendar;
import java.util.List;

 class ReservableSpaceDatabase {
    private final DatabaseService databaseService;

    ReservableSpaceDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param space space to insert into the database
     * @return true if the insert succeeded and false if otherwise
     */
    boolean insertReservableSpace(ReservableSpace space) {
        String insertQuery = ("INSERT INTO RESERVABLESPACE VALUES(?, ?, ?, ?, ?, ?)");
        return databaseService.executeInsert(insertQuery, space.getSpaceID(), space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed());
    }

    /**
     * @param id ID of the space to get from the database
     * @return a reservable space with matching ID to the given one
     */
    ReservableSpace getReservableSpace(String id) {
        String query = "SELECT * FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return (ReservableSpace) databaseService.executeGetById(query, ReservableSpace.class, id);
    }

    /**
     * @return a list of all reservable spaces in the database
     */
    List<ReservableSpace> getAllReservableSpaces() {
        String query = "Select * FROM RESERVABLESPACE";
        return (List<ReservableSpace>) (List<?>) databaseService.executeGetMultiple(query, ReservableSpace.class, new Object[]{});
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces with any reservation in the given time frame
     */
    List<ReservableSpace> getBookedReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {
        String query = "Select * From RESERVABLESPACE Where SPACEID In (Select Distinct SPACEID From RESERVATION Where ((STARTTIME <= ? and ENDTIME > ?) or (STARTTIME >= ? and STARTTIME < ?)))";

        return (List<ReservableSpace>) (List<?>) databaseService.executeGetMultiple(query, ReservableSpace.class, from, from, from, to);
    }

    /**
     * @param from start time
     * @param to   end time
     * @return list of reservable spaces without any reservations in the given time frame
     */
    List<ReservableSpace> getAvailableReservableSpacesBetween(GregorianCalendar from, GregorianCalendar to) {
        String query = "Select * From RESERVABLESPACE Where SPACEID Not In (Select Distinct SPACEID From RESERVATION Where ((STARTTIME <= ? and ENDTIME > ?) or (STARTTIME >= ? and STARTTIME < ?)))";

        return (List<ReservableSpace>) (List<?>) databaseService.executeGetMultiple(query, ReservableSpace.class, from, from, from, to);
    }

    /**
     * @param space the reservable space to update in the database
     * @return true if the update succeeds and false if otherwise
     */
    boolean updateReservableSpace(ReservableSpace space) {
        String query = "UPDATE RESERVABLESPACE SET spaceName=?, spaceType=?, locationNode=?, timeOpen=?, timeClosed=? WHERE (spaceID = ?)";
        return databaseService.executeUpdate(query, space.getSpaceName(), space.getSpaceType(), space.getLocationNodeID(), space.getTimeOpen(), space.getTimeClosed(), space.getSpaceID());
    }

    /**
     * @param space space to delete from the database
     * @return true if the delete succeeds and false if otherwise
     */
    boolean deleteReservableSpace(ReservableSpace space) {
        String query = "DELETE FROM RESERVABLESPACE WHERE (spaceID = ?)";
        return databaseService.executeUpdate(query, space.getSpaceID());
    }

    ReservableSpace getReservableSpaceByNodeID(String nodeID) {
        String query = "SELECT * FROM RESERVABLESPACE WHERE (locationNode = ?)";
        return (ReservableSpace) databaseService.executeGetById(query, ReservableSpace.class, nodeID);
    }
}