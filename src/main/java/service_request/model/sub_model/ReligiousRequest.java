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

public class ReligiousRequest extends Request {
    public enum Religion {
        CHRISTIAN("Christian"),
        JEWISH("Jewish"),
        CATHOLIC("Catholic"),
        ISLAM("Islam"),
        OTHER("Other");

        private String string;

        Religion(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
    }

    ReligiousRequest.Religion religion;

    public ReligiousRequest(int id, String notes, Node location, boolean completed, ReligiousRequest.Religion religion) {
        super(id, notes, location, completed);
        this.religion = religion;
    }


    public ReligiousRequest.Religion getReligion() {
        return religion;
    }

    public void setReligion(ReligiousRequest.Religion religion) {
        this.religion = religion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReligiousRequest that = (ReligiousRequest) o;
        return religion == that.religion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), religion);
    }

    @Override
    public String toString() {
        return "ReligiousRequest{" +
                "religion=" + religion +
                '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertReligiousRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateReligiousRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == RELIGIOUS_OFFICIAL || allEmployee.get(i).getJob() == ADMINISTRATOR ) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }
    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllReligiousRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateReligiousRequest((ReligiousRequest) selectedTask) ;
    }


    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == RELIGIOUS_OFFICIAL || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Description: %s, Type: %s", this.getNotes(), this.getReligion().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Religious");
    }

    @Override
    public String getType(){
        return "Religious";
    }
}
