package model.request;

import com.jfoenix.controls.JFXToggleNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.JobType;
import model.Node;
import service.DatabaseService;


import java.util.ArrayList;
import java.util.Objects;

import java.util.Objects;

import static model.JobType.*;

public class MedicineRequest extends Request {

    String medicineType;
    double quantity;

    public MedicineRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.medicineType = "";
        this.quantity = 0;
    }

    public MedicineRequest(int id, String notes, Node location, boolean completed, String medicineType, double quantity) {
        super(id, notes, location, completed);
        this.quantity = quantity;
        this.medicineType = medicineType;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "MedicineRequest{" +
                "medicineType='" + medicineType + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MedicineRequest that = (MedicineRequest) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                Objects.equals(medicineType, that.medicineType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), medicineType, quantity);
    }

    // overides abstract Request method - called in Request Facade
    @Override
    public void makeRequest () {
        DatabaseService.getDatabaseService().insertMedicineRequest(this);
    }

    // overides abstract Request method - called in Request Facade
    @Override
    public void fillRequest () {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateMedicineRequest(this);
    }


    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == NURSE || allEmployee.get(i).getJob() == DOCTOR || allEmployee.get(i).getJob() == ADMINISTRATOR)  {
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
        myDBS.updateMedicineRequest((MedicineRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == DOCTOR || jobType == NURSE || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Medicine Request %d, Description: %s, Type: %s, Assigned To: %s, Fulfilled: %s, Quantity: %f",
                this.getId(), this.getNotes(), this.getMedicineType(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No", this.getQuantity());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Medicine");
    }
}