package model.request;

import model.Node;
import model.RequestType;
import sun.plugin.services.AxBridgeBrowserService;

import static model.RequestType.RType.ABS;

public abstract class Request {
    String id, notes;
    Node location;
    boolean completed;
    RequestType requestType;

    public Request(String id, String notes, Node location, boolean completed, RequestType requestType) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
        this.requestType = requestType;
    }

    public Request(String id, String notes, Node location, boolean completed) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
        this.requestType = new RequestType(ABS);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
