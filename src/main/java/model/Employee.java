package model;

import java.util.Objects;

public class Employee {
    int ID;
    String job;
    boolean isAdmin;
    String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Employee(int ID, String job, boolean isAdmin, String password) {
        this.ID = ID;
        this.job = job;
        this.isAdmin = isAdmin;
        this.password = password;
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

    /**
     * checks if two employees are the same employee
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return ID == employee.ID &&
                isAdmin == employee.isAdmin &&
                Objects.equals(job, employee.job);
    }

    /**
     * TBD
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID, job, isAdmin);
    }
}
