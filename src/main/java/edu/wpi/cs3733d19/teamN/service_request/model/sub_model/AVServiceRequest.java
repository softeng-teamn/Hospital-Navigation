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

public class AVServiceRequest extends Request {

    public enum AVServiceType{
        Audio("Audio"),
        Visual("Visual"),
        Other("Other");

        private String string;

        AVServiceType(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
    }

    AVServiceType avServiceType;

   public AVServiceRequest(int id, String notes, Node location, boolean completed, AVServiceType avServiceType){
        super(id,notes,location,completed);
        this.avServiceType = avServiceType;
    }

    public AVServiceType getAVServiceType(){return avServiceType;}

   public void setAVServiceType(AVServiceType avServiceType){this.avServiceType = avServiceType;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AVServiceRequest that = (AVServiceRequest)o;
        return avServiceType == that.avServiceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), avServiceType);
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertAVServiceRequest(this);
    }

    @Override
    public void fillRequest() {
    this.setCompleted(true);
    DatabaseService.getDatabaseService().updateAVServiceRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll( myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == IT || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllAVServiceRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateAVServiceRequest((AVServiceRequest)selectedTask) ;
        System.out.println("NEW DATABASE ID : " + (selectedTask.getAssignedTo())) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == AV || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format("Description: %s, Type: %s", this.getNotes(), this.getAVServiceType().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("AV Service");
    }

    @Override
    public String getType(){
        return "Audio Visual";
    }
}
