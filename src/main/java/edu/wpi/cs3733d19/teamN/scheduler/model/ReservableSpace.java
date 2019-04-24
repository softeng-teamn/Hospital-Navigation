package edu.wpi.cs3733d19.teamN.scheduler.model;

import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * reservable space object
 */
public class ReservableSpace implements Comparable<ReservableSpace>{
    String spaceID, spaceName, spaceType, locationNodeID;
    GregorianCalendar timeOpen, timeClosed;

    public ReservableSpace(String spaceID, String spaceName, String spaceType, String locationNodeID, GregorianCalendar timeOpen, GregorianCalendar timeClosed) {
        this.spaceID = spaceID;
        this.spaceName = spaceName;
        this.spaceType = spaceType;
        this.locationNodeID = locationNodeID;
        this.timeOpen = (GregorianCalendar) timeOpen.clone();
        this.timeClosed = (GregorianCalendar) timeClosed.clone();
    }

    public String getSpaceID() {
        return spaceID;
    }

    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public String getLocationNodeID() {
        return locationNodeID;
    }

    public void setLocationNodeID(String locationNodeID) {
        this.locationNodeID = locationNodeID;
    }

    public GregorianCalendar getTimeOpen() {
        return (GregorianCalendar) timeOpen.clone();
    }

    public void setTimeOpen(GregorianCalendar timeOpen) {
        this.timeOpen = (GregorianCalendar) timeOpen.clone();
    }

    public GregorianCalendar getTimeClosed() {
        return (GregorianCalendar) timeClosed.clone();
    }

    public void setTimeClosed(GregorianCalendar timeClosed) {
        this.timeClosed = (GregorianCalendar) timeClosed.clone();
    }

    /**
     * checks if two ReservableSpaces are the same space
     * @param o the reservable space to compare
     * @return true if the objects are equal and false if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservableSpace that = (ReservableSpace) o;
        return Objects.equals(spaceID, that.spaceID) &&
                Objects.equals(spaceName, that.spaceName) &&
                Objects.equals(spaceType, that.spaceType) &&
                Objects.equals(locationNodeID, that.locationNodeID) &&
                Objects.equals(timeOpen.getTime(), that.timeOpen.getTime()) &&
                Objects.equals(timeClosed.getTime(), that.timeClosed.getTime());
    }

    /**
     * hash code
     * @return a hashcode based on the given objects
     */
    @Override
    public int hashCode() {
        return Objects.hash(spaceID, spaceName, spaceType, locationNodeID, timeOpen, timeClosed);
    }

    @Override
    public String toString() {
        return "ReservableSpace{" +
                "spaceID='" + spaceID + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", spaceType='" + spaceType + '\'' +
                ", locationNodeID='" + locationNodeID + '\'' +
                ", timeOpen=" + timeOpen +
                ", timeClosed=" + timeClosed +
                '}';
    }

    @Override
    public int compareTo(ReservableSpace other) {
        return this.spaceName.compareTo(other.spaceName);
    }
}
