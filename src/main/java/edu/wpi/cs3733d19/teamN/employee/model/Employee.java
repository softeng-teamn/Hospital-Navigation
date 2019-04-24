package edu.wpi.cs3733d19.teamN.employee.model;

import java.util.Objects;

/**
 * Employee Object
 */
public class Employee {
    int ID;
    JobType job;
    boolean isAdmin;
    String password;
    String email, phone;
    String lastName, firstName;

    String username;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Employee(int ID, String username, String firstName, String lastName, JobType job, boolean isAdmin, String password) {
        this.username = username;
        this.ID = ID;
        this.job = job;
        this.isAdmin = isAdmin;
        this.password = password;
        this.email = "";
        this.phone = "";
        this.lastName = lastName;
        this.firstName = firstName;
        System.out.println(job);
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

    // Required in for JavaFx PropertyValueFactory
    public boolean getIsAdmin() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", job=" + job +
                ", isAdmin=" + isAdmin +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
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
                job == employee.job &&
                Objects.equals(password, employee.password) &&
                Objects.equals(email, employee.email) &&
                Objects.equals(phone, employee.phone) &&
                Objects.equals(lastName, employee.lastName) &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(username, employee.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, job, isAdmin, password, email, phone, lastName, firstName, username);
    }
}
