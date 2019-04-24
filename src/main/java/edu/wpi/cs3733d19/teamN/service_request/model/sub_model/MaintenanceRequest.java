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

public class MaintenanceRequest extends Request {
    public enum MaintenanceType {
        Electrical("Electrical"),
        Plumbing("Plumbing"),
        Other("Other");

        private String string;

        MaintenanceType(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
    }

    MaintenanceType maintenanceType;

    public MaintenanceRequest(int id, String notes, Node location, boolean completed, MaintenanceType maintenanceType) {
        super(id, notes, location, completed);
        this.maintenanceType = maintenanceType;
    }


    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MaintenanceRequest that = (MaintenanceRequest) o;
        return maintenanceType == that.maintenanceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maintenanceType);
    }

    @Override
    public String toString() {
        return "MaintenanceRequest{" +
                "maintenanceType=" + maintenanceType +
                '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertMaintenanceRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateMaintenanceRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll( myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == MAINTENANCE_WORKER) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllMaintenanceRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateMaintenanceRequest((MaintenanceRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == MAINTENANCE_WORKER || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Description: %s, Type: %s", this.getNotes(), this.getMaintenanceType().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Maintenance");
    }

    @Override
    public String getType(){
        return "Maintenance";
    }

}
