package model;

import java.util.Date;

public class Reservation {
    int eventID, privacyLevel, employeeId;
    String eventName, locationID;
    Date startTime, endTime;

    public Reservation(int eventID, int privacyLevel, int employeeId, String eventName, String locationID, Date startTime, Date endTime) {
        this.eventID = eventID;
        this.privacyLevel = privacyLevel;
        this.employeeId = employeeId;
        this.eventName = eventName;
        this.locationID = locationID;
        this.startTime = (Date) startTime.clone();
        this.endTime = (Date) endTime.clone();
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

    public Date getStartTime() {
        return (Date) startTime.clone();
    }

    public void setStartTime(Date startTime) {
        this.startTime = (Date) startTime.clone();
    }

    public Date getEndTime() {
        return (Date) endTime.clone();
    }

    public void setEndTime(Date endTime) {
        this.endTime = (Date) endTime.clone();
    }
}
