package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.*;

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
        this.setCompletedBy(this.getCompletedBy());
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)this);

    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = (ObservableList<Employee>) myDBS.getAllEmployees() ;

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

}



