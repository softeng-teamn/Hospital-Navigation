package elevator_api;

import elevator.ElevatorConnnection;

import java.io.IOException;
import java.util.Objects;

public class ApiInternalTransportRequest {

    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Stretcher
    }

    public enum Urgency {
        NOT,
        SOMEWHAT,
        VERY
    }


    private int id, assignedTo;
    private String notes, location;
    private boolean completed;

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
        this.completed = false;
    }


    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public void fillRequest() {
        this.setCompleted(true);
        if(this.urgency == Urgency.VERY){
            ElevatorConnnection eCon = new ElevatorConnnection();
            try {
                System.out.println("elevator = " + location.charAt(7) + "Floor = " + location.substring(8));
                eCon.postFloor("" + location.charAt(7), location.substring(8)); // post elevator, floornum
            } catch (IOException e) {
                System.out.println("error posting elevator check WIFI from Internal Transport API");
                e.printStackTrace();
            }
        }
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
