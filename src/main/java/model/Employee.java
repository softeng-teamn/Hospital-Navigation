package model;

import java.util.Objects;

public class Employee {
    int ID;
    String job;
    boolean isAdmin;

    public Employee(int ID, String job, boolean isAdmin) {
        this.ID = ID;
        this.job = job;
        this.isAdmin = isAdmin;
    }

    public int getID() {
        return ID;
    }

    public String getJob() {
        return job;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return ID == employee.ID &&
                isAdmin == employee.isAdmin &&
                Objects.equals(job, employee.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, job, isAdmin);
    }
}
