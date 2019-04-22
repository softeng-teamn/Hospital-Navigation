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

public class SanitationRequest extends Request {

    private String urgency;
    private String materialState;

    public SanitationRequest(int id, String notes, Node location, boolean completed, String urg, String ms) {
        super(id, notes, location, completed);
        this.urgency = urg;
        this.materialState = ms;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getMaterialState() {
        return materialState;
    }

    public void setMaterialState(String materialState) {
        this.materialState = materialState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SanitationRequest that = (SanitationRequest) o;
        return Objects.equals(urgency, that.urgency) &&
                Objects.equals(materialState, that.materialState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), urgency, materialState);
    }

    @Override
    public String toString() {
        return "SanitationRequest{" +
                "urgency='" + urgency + '\'' +
                ", materialState='" + materialState + '\'' +
                '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertSanitationRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateSanitationRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == MAINTENANCE_WORKER || allEmployee.get(i).getJob() == ADMINISTRATOR ) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }
    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllSanitationRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateSanitationRequest((SanitationRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == JANITOR || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Description: %s, %s urgent, Material State: %s", this.getNotes(), this.getUrgency(), this.getMaterialState());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Sanitation");
    }

    @Override
    public String getType(){
        return "Sanitation";
    }
}
