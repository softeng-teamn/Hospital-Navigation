package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.ADMINISTRATOR;
import static model.JobType.RELIGIOUS_OFFICIAL;

public class ReligiousRequest extends Request{
    public enum Religion {
        CHRISTIAN,
        JEWISH,
        CATHOLIC,
        ISLAM,
        OTHER
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
        ObservableList<Employee> allEmployee = (ObservableList<Employee>) myDBS.getAllEmployees() ;

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
}
