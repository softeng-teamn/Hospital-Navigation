package model.request;

import model.Node;
import service.DatabaseService;

import java.util.Objects;

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
}
