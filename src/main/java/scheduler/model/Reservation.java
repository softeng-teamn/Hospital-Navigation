package scheduler.model;

import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * space reservation object
 */
public class Reservation {
    int eventID, privacyLevel, employeeId;
    String eventName, locationID;
    GregorianCalendar startTime, endTime;

    public Reservation(int eventID, int privacyLevel, int employeeId, String eventName, String locationID, GregorianCalendar startTime, GregorianCalendar endTime) {
        this.eventID = eventID;
        this.privacyLevel = privacyLevel;
        this.employeeId = employeeId;
        this.eventName = eventName;
        this.locationID = locationID;
        this.startTime = (GregorianCalendar) startTime.clone();
        this.endTime = (GregorianCalendar) endTime.clone();
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(int privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public GregorianCalendar getStartTime() {
        return (GregorianCalendar) startTime.clone();
    }

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = (GregorianCalendar) startTime.clone();
    }

    public GregorianCalendar getEndTime() {
        return (GregorianCalendar) endTime.clone();
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = (GregorianCalendar) endTime.clone();
    }

    /**
     * checks if two Reservations are equal to each other.
     * @param o a given reservation
     * @return true if the reservations are equal and false if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return eventID == that.eventID &&
                privacyLevel == that.privacyLevel &&
                employeeId == that.employeeId &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(locationID, that.locationID) &&
                Objects.equals(startTime.getTime(), that.startTime.getTime()) &&
                Objects.equals(endTime.getTime(), that.endTime.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID, privacyLevel, employeeId, eventName, locationID, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "eventID=" + eventID +
                ", privacyLevel=" + privacyLevel +
                ", employeeId=" + employeeId +
                ", eventName='" + eventName + '\'' +
                ", locationID='" + locationID + '\'' +
                ", startTime=" + startTime.getTime() +
                ", endTime=" + endTime.getTime() +
                '}';
    }
}
