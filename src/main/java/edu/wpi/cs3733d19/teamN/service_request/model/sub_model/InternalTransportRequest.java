package edu.wpi.cs3733d19.teamN.service_request.model.sub_model;

//import com.sun.xml.internal.bind.v2.TODO;
import edu.wpi.cs3733d19.teamN.elevator.ElevatorConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import edu.wpi.cs3733d19.teamN.employee.model.JobType;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service_request.model.Request;

import java.io.IOException;
import java.util.Objects;

import static edu.wpi.cs3733d19.teamN.employee.model.JobType.*;

public class InternalTransportRequest extends Request {

    public enum TransportType {
        Wheelchair("Wheelchair"),
        MotorScooter("Motor Scooter"),
        Stretcher("Stretcher");

        private String string;

        TransportType(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
    }

    public enum Urgency {
        NOT("Low"),
        SOMEWHAT("Medium"),
        VERY("High");

        private String string;

        Urgency(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
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
        if(this.urgency == Urgency.VERY && !isCompleted()){
           callElev();
        }
        setCompleted(true);
        DatabaseService.getDatabaseService().updateInternalTransportRequest(this);
    }

    private void callElev(){
        ElevatorConnection eCon = new ElevatorConnection();
        try {
            eCon.postFloor("L", getLocation().getFloor()); // post elevator, floornum
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error posting elevator from internal transport req");
        }
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
        return String.format("Description: %s, Transport Vehicle: %s, %s urgent", this.getNotes(), this.getTransport().name(), this.getUrgency());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Internal Transport");
    }


    @Override
    public String getType(){
        return "Internal Transport";
    }
}
