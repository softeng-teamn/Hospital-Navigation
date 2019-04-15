package service_request.model.sub_model;

//import com.sun.xml.internal.bind.v2.TODO;
import elevator.ElevatorConnnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import employee.model.Employee;
import employee.model.JobType;
import map.Node;
import database.DatabaseService;
import map.PathFindingService;
import service_request.model.Request;

import java.util.Objects;

import static employee.model.JobType.*;

public class InternalTransportRequest extends Request {

    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Stretcher
    }

    public enum Urgency {
        NOT,
        SOMEWHAT,
        VERY
    }

    private Urgency urgency;

    private TransportType transport;

    public InternalTransportRequest(int id, String notes, Node location, boolean completed, TransportType transportType, Urgency urgency) {
        super(id, notes, location, completed);
        this.transport = transportType;
        this.urgency = urgency;
    }

    public InternalTransportRequest(int id, String notes, Node location, boolean completed, TransportType transport) {
        super(id, notes, location, completed);
        this.transport = transport;
        this.urgency = Urgency.NOT;
    }

    public InternalTransportRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.transport = TransportType.Wheelchair;
        this.urgency = Urgency.NOT;
    }


    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertInternalTransportRequest(this);
    }

    @Override
    public void fillRequest() {
        if(this.urgency == Urgency.VERY){
            //change stuff here for api
           // ElevatorConnnection eCon = new ElevatorConnnection(<TEAM NUM>);
            //eCon.postFloor(); // post elevator, floornum
        }
        DatabaseService.getDatabaseService().updateInternalTransportRequest(this);
    }

    public TransportType getTransport() {
        return transport;
    }

    @Override
    public String toString() {
        return "InternalTransportRequest{" +
                "transport=" + transport +
                '}';
    }

    public void setTransport(TransportType transport) {
        this.transport = transport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InternalTransportRequest that = (InternalTransportRequest) o;
        return transport == that.transport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transport);
    }


    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll( myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == MISCELLANEOUS || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllInternalTransportRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateInternalTransportRequest((InternalTransportRequest)selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == INTERNAL_TRANSPORT || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Internal Transport Request %d, Description: %s, Type: %s, Urgency: %s Assigned To: %s, Fulfilled: %s",
                this.getId(), this.getNotes(), this.getTransport().name(), this.getUrgency(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No");
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Internal Transport");
    }
}
