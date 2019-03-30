package model;

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
}
