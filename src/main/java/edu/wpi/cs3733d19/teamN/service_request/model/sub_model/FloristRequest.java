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


public class FloristRequest extends Request {

    String bouquetType;
    int quantity;

    public FloristRequest(int id, String notes, Node location, boolean completed, String bouquetType, int quantity) {
        super(id, notes, location, completed);
        this.bouquetType = bouquetType;
        this.quantity = quantity;
    }

    public FloristRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
    }

    public String getBouquetType() {
        return bouquetType;
    }

    public void setBouquetType(String bouquetType) {
        this.bouquetType = bouquetType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "FloristRequest{" +
                "bouquetType='" + bouquetType + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FloristRequest that = (FloristRequest) o;
        return quantity == that.quantity &&
                Objects.equals(bouquetType, that.bouquetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bouquetType, quantity);
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertFloristRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateFloristRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == GIFT_SERVICES || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }
    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllFloristRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateFloristRequest((FloristRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == FLORIST || jobType == ADMINISTRATOR) return true;
        return false;
    }


    @Override
    public String toDisplayString() {
        return String.format("Description: %s, Bouquet Type: %s, Quantity: %d", this.getNotes(), this.getBouquetType(), this.getQuantity());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Florist");
    }


    @Override
    public String getType(){
        return "Florist";
    }
}
