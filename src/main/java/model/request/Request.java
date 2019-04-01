package model.request;

import model.Node;
import model.RequestType;
//import sun.plugin.services.AxBridgeBrowserService;

import static model.RequestType.RType.ABS;

import java.util.Objects;

public abstract class Request {
    private int id;
    private String notes;
    private Node location;
    private boolean completed;
    RequestType requestType;
    private String completedBy;

    public Request(int id, String notes, Node location, boolean completed, RequestType requestType) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
        this.requestType = requestType;
        this.completedBy = "";
    }

    public Request(int id, String notes, Node location, boolean completed) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
        this.requestType = new RequestType(ABS);
        this.completedBy = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node location) {
        this.location = location;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return completed == request.completed &&
                Objects.equals(id, request.id) &&
                Objects.equals(notes, request.notes) &&
                Objects.equals(location, request.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, notes, location, completed);
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
