package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.IT;
import static model.JobType.MAINTENANCE_WORKER;

public class MaintenanceRequest extends Request {
    public enum MaintenanceType {
        Electrical,
        Plumbing,
        Other
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
}
