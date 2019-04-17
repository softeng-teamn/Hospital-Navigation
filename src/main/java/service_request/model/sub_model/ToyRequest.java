package service_request.model.sub_model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import employee.model.Employee;
import employee.model.JobType;
import map.Node;
import database.DatabaseService;
import service_request.model.Request;

import java.util.Objects;

import static employee.model.JobType.*;

public class ToyRequest extends Request {

    String toyName;

    public ToyRequest(int id, String notes, Node location, boolean completed, String toyName) {
        super(id, notes, location, completed);
        this.toyName = toyName;
    }

    public ToyRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.toyName = "";
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertToyRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateToyRequest((ToyRequest)this);
    }

    public String getToyName() {
        return toyName;
    }

    public void setToyName(String toyName) {
        this.toyName = toyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ToyRequest that = (ToyRequest) o;
        return Objects.equals(toyName, that.toyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), toyName);
    }

    @Override
    public String toString() {
        return "ToyRequest{" +
                "toyName='" + toyName + '\'' +
                '}';
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == GIFT_SERVICES || allEmployee.get(i).getJob() == ADMINISTRATOR ) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }
    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllToyRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateToyRequest((ToyRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == TOY || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Toy Request %d, Description: %s, Toy Name: %s, Assigned To: %s, Fulfilled: %s",
                this.getId(), this.getNotes(), this.getToyName(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No");
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Toy");
    }
}
