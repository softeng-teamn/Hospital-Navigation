package model;

import java.util.Objects;

public class Employee {
    int ID;
    JobType job;
    boolean isAdmin;
    String password;

    String username;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Employee(int ID, String username, JobType job, boolean isAdmin, String password) {
        this.username = username;
        this.ID = ID;
        this.job = job;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public int getID() {
        return ID;
    }

    public JobType getJob() {
        return job;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", job='" + job + '\'' +
                ", isAdmin=" + isAdmin +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return ID == employee.ID &&
                isAdmin == employee.isAdmin &&
                Objects.equals(job, employee.job) &&
                Objects.equals(password, employee.password) &&
                Objects.equals(username, employee.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, job, isAdmin, password, username);
    }
}
