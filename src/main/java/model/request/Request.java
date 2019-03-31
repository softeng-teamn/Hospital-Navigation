package model.request;

import model.Node;

public abstract class Request {
    String id, notes;
    Node location;
    boolean completed;

    public Request(String id, String notes, Node location, boolean completed) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
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
}
