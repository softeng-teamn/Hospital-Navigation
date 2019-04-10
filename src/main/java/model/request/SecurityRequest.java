package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.JobType;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.*;

public class SecurityRequest extends Request {

    public enum Urgency {
        NOT, SOMEWHAT, VERY;
    }

    private Urgency urgency;

    public SecurityRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.urgency = Urgency.NOT;
    }

    public SecurityRequest(int id, String notes, Node location, boolean completed, Urgency urgency) {
        super(id, notes, location, completed);
        this.urgency = urgency;
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertSecurityRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateSecurityRequest((SecurityRequest) this);
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityRequest that = (SecurityRequest) o;
        return urgency == that.urgency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), urgency);
    }

    @Override
    public String toString() {
        return "SecurityRequest: " +
                "urgency=" + urgency;
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == SECURITY_PERSONNEL || allEmployee.get(i).getJob() == ADMINISTRATOR ) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllSecurityRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateSecurityRequest((SecurityRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == SECURITY_PERSONNEL || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Security Request %d, Description: %s, Urgency: %s, Assigned To: %s, Fulfilled: %s",
                this.getId(), this.getNotes(), this.getUrgency().name(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No");
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Security");
    }
}
