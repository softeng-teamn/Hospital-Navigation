package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.ADMINISTRATOR;
import static model.JobType.IT;

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


}
