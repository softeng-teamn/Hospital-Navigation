package model.request;

import model.Node;

import java.util.Objects;

public abstract class Request {
    int id;
    String notes;
    Node location;
    boolean completed;

    public Request(int id, String notes, Node location, boolean completed) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
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
}
