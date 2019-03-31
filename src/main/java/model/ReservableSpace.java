package model;

import java.util.Date;

public class ReservableSpace {
    String spaceID, spaceName, spaceType, locationNodeID;
    Date timeOpen, timeClosed;

    public ReservableSpace(String spaceID, String spaceName, String spaceType, String locationNodeID, Date timeOpen, Date timeClosed) {
        this.spaceID = spaceID;
        this.spaceName = spaceName;
        this.spaceType = spaceType;
        this.locationNodeID = locationNodeID;
        this.timeOpen = (Date) timeOpen.clone();
        this.timeClosed = (Date) timeClosed.clone();
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

    public Date getTimeOpen() {
        return (Date) timeOpen.clone();
    }

    public void setTimeOpen(Date timeOpen) {
        this.timeOpen = (Date) timeOpen.clone();
    }

    public Date getTimeClosed() {
        return (Date) timeClosed.clone();
    }

    public void setTimeClosed(Date timeClosed) {
        this.timeClosed = (Date) timeClosed.clone();
    }
}
