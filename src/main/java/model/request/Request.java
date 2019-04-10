package model.request;

import javafx.collections.ObservableList;
import model.Employee;
import model.JobType;
import model.Node;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Request {
    private int id;
    private String notes;
    private Node location;
    private boolean completed;
    private int assignedTo;

    public Request(int id, String notes, Node location, boolean completed) {
        this.id = id;
        this.notes = notes;
        this.location = location;
        this.completed = completed;
        this.assignedTo = -1;
    }

    public abstract void makeRequest();

    public abstract void fillRequest();

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

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
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

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", notes='" + notes + '\'' +
                ", location=" + location +
                ", completed=" + completed +
               // ", requestType=" + requestType +
                ", assignedTo='" + assignedTo + '\'' +
                '}';
    }

    //Show Requests based on Job
    public abstract ObservableList<Request> showProperRequest();

    public abstract ObservableList<Employee> returnCorrectEmployee();

    public abstract void updateEmployee (Request selectedTask, Employee selectedEmp);

    public abstract boolean fulfillableByType(JobType jobType);

    public abstract String toDisplayString();

}