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
        return String.format("Description: %s, Type: %s, Quantity: %.2f", this.getNotes(), this.getMedicineType(), this.getQuantity());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Medicine");
    }

    @Override
    public String getType(){
        return "Medicine";
    }

}