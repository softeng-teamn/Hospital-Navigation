package service_request.model.sub_model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import employee.model.Employee;
import employee.model.JobType;
import map.Node;
import database.DatabaseService;
import service_request.model.Request;

import java.util.Objects;

import static employee.model.JobType.*;

public class AVServiceRequest extends Request {

    public enum AVServiceType{
        Audio,
        Visual,
        Other
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
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("AV Request %d, Description: %s, Type: %s, Assigned To: %s, Fulfilled: %s",
                this.getId(), this.getNotes(), this.getAVServiceType().name(), this.getAssignedTo() == -1 ? "None" : "" + this.getAssignedTo(), this.isCompleted() ? "Yes" : "No");
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("AV Service");
    }
}
