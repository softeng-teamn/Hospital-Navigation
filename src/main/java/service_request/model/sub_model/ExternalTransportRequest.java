package service_request.model.sub_model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import employee.model.Employee;
import employee.model.JobType;
import map.Node;
import database.DatabaseService;
import service_request.model.Request;

import java.util.Date;
import java.util.Objects;

import static employee.model.JobType.*;

public class ExternalTransportRequest extends Request {

    public enum TransportationType{
        BUS("Bus"),
        TAXI("Taxi"),
        CAR("Car");

        private String string;

        TransportationType(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
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
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

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

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateExtTransRequest((ExternalTransportRequest)selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == EXTERNAL_TRANSPORT || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format("Description: %s, Vehicle: %s", this.getNotes(), this.getTransportationType().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("External Transport");
    }


    @Override
    public String getType(){
        return "External Transport";
    }
}
