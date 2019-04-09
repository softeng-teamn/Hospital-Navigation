package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static model.JobType.ADMINISTRATOR;
import static model.JobType.MISCELLANEOUS;

public class ExternalTransportRequest extends Request {

    public enum TransportationType{
        BUS,
        TAXI,
        CAR
    }

    private String description;
    private Date date;
    private TransportationType transportationType;

    public ExternalTransportRequest(int id, String notes, Node location, boolean completed, Date date, TransportationType transportationType, String description) {
        super(id, notes, location, completed);
        Date d = new Date(date.getTime());
        this.date = d;
        this.transportationType = transportationType;
        this.description = description;
    }

    public Date getDate() {
        Date d = new Date(date.getTime());
        return d;
    }

    public void setDate(Date date) {
        Date d = new Date(date.getTime());
        this.date = d;
    }

    public TransportationType getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType transportationType) {
        this.transportationType = transportationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ExternalTransportRequest{" +
                "description='" + description + '\'' +
                ", date=" + date +
                ", transportationType=" + transportationType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExternalTransportRequest that = (ExternalTransportRequest) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                transportationType == that.transportationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, date, transportationType);
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertExtTransRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateExtTransRequest(this);
    }


    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = (ObservableList<Employee>) myDBS.getAllEmployees() ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == MISCELLANEOUS || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllExtTransRequests() ;
    }
}
