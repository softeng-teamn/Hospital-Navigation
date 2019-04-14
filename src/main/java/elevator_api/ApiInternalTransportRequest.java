package elevator_api;

import java.util.Objects;

public class ApiInternalTransportRequest {

    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Stretcher
    }


    private int id, assignedTo;
    private String notes, location;
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

    public ApiInternalTransportRequest(int id, String notes, String location, TransportType transportType) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.transport = transportType;
        this.assignedTo = -1;
        this.transport = transportType;
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
