package model.request;

import model.Node;
import service.DatabaseService;

import java.util.Objects;

public class PatientInfoRequest extends Request{

    private String firstName;
    private String lastName;
    private String birthDay;
    private String description;

    public PatientInfoRequest(int id, String notes, Node location, boolean completed, String firstName, String lastName, String birthDay, String description) {
        super(id, notes, location, completed);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.description = description;
    }

    public PatientInfoRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertPatientInfoRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updatePatientInfoRequest(this);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PatientInfoRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PatientInfoRequest that = (PatientInfoRequest) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(birthDay, that.birthDay) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, birthDay, description);
    }
}
