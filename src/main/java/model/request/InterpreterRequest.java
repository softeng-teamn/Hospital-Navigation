package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.JobType;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.*;


public class InterpreterRequest extends Request{

    public enum Language{
        SPANISH, FRENCH, MANDARIN, ENGLISH;
    }



    private Language l;

    public InterpreterRequest(int id, String notes, Node location, boolean completed, Language l){
        super(id, notes, location, completed);
        this.l = l;
    }

    public Language getLanguageType(){
        return l;
    }

    public void setLanguage(Language l){
        this.l = l;
    }

    @Override
    public String toString() {
        return "InterpreterRequest{" + "Language= " + l.name() + '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertInterpreterRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateInterpreterRequest(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InterpreterRequest that = (InterpreterRequest) o;
        return l == that.l;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), l);
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
        return (ObservableList) myDBS.getAllInterpreterRequests() ;
    }

    @Override
    public void updateEmployee (Request selectedTask, Employee selectedEmp) {
        myDBS.updateInterpreterRequest((InterpreterRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        if (jobType == INTERPRETER) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format("Interpreter Request %d", this.getId());
    }
}
