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

public class ITRequest extends Request {

    public enum ITRequestType {
        Maintenance,
        New_Computer,
        Accessories,
        Assistance
    }

    ITRequestType itRequestType;

    public ITRequest(int id, String notes, Node location, boolean completed, ITRequestType type) {
        super(id, notes, location, completed);
        this.itRequestType = type;
    }

    public ITRequestType getItRequestType() {
        return itRequestType;
    }

    public void setItRequestType(ITRequestType itRequestType) {
        this.itRequestType = itRequestType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ITRequest itRequest = (ITRequest) o;
        return itRequestType == itRequest.itRequestType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itRequestType);
    }

    @Override
    public String toString() {
        return "ITRequest{" +
                "itRequestType=" + itRequestType +
                '}';
    }

    @Override
    public void makeRequest () {
        DatabaseService.getDatabaseService().insertITRequest(this);
    }

    @Override
    public void fillRequest () {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == IT || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }
    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllITRequests() ;
    }


    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateITRequest((ITRequest) selectedTask) ;
    }


    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == IT || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("IT Request %d, Description: %s, Type: %s, Assigned To: %s, Fulfilled: %s",
                this.getId(), this.getNotes(), this.getItRequestType().name(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No");
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("IT");
    }

}



