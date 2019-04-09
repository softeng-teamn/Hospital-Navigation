package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.ADMINISTRATOR;
import static model.JobType.GIFT_SERVICES;


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
        ObservableList<Employee> allEmployee = (ObservableList<Employee>) myDBS.getAllEmployees() ;

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

}
