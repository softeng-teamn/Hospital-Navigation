package edu.wpi.cs3733d19.teamN.elevator_api;

import java.util.Objects;

/**
 * API's internal transport request edu.wpi.cs3733d19.teamN.controller
 */
public class ApiInternalTransportRequest {

    /**
     * possible types of internal transportation
     */
    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Stretcher
    }

    /**
     * urgency level
     */
    public enum Urgency {
        NOT,
        SOMEWHAT,
        VERY
    }


    private int id, assignedTo;
    private String notes, location;

    private Urgency urgency;
    private TransportType transport;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ApiInternalTransportRequest(int id, String notes, String location, TransportType transportType, Urgency urgency) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.transport = transportType;
        this.assignedTo = -1;
        this.urgency = urgency;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public void makeRequest() {
        ApiDatabaseService.getDatabaseService().insertInternalTransportRequest(this);
    }

    public TransportType getTransport() {
        return transport;
    }

    @Override
    public String toString() {
        return "InternalTransportRequest{" +
                "transport=" + transport +
                '}';
    }

    public void setTransport(TransportType transport) {
        this.transport = transport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApiInternalTransportRequest that = (ApiInternalTransportRequest) o;
        return transport == that.transport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transport);
    }

    public boolean isOfType(String typeString) {
        return typeString.equals("Internal Transport");
    }
}
