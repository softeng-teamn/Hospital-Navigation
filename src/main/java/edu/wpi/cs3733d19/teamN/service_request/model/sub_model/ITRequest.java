package edu.wpi.cs3733d19.teamN.service_request.model.sub_model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import edu.wpi.cs3733d19.teamN.employee.model.JobType;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service_request.model.Request;

import java.util.Objects;

import static edu.wpi.cs3733d19.teamN.employee.model.JobType.*;

public class ITRequest extends Request {

    public enum ITRequestType {
        Maintenance("Maintenance"),
        New_Computer("New Computer"),
        Accessories("Accessories"),
        Assistance("Assistance");

        private String string;

        ITRequestType(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
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
        return String.format("Description: %s, Type: %s", this.getNotes(), this.getItRequestType().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("IT");
    }


    @Override
    public String getType(){
        return "IT";
    }
}