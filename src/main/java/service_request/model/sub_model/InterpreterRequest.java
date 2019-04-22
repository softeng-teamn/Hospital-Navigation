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


public class InterpreterRequest extends Request {

    public enum Language{
        SPANISH("Spanish"),
        FRENCH("French"),
        MANDARIN("Mandarin"),
        ENGLISH("English");

        private String string;

        Language(String name){string = name;}

        @Override
        public String toString() {
            return string;
        }
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
        if (jobType == INTERPRETER || jobType == ADMINISTRATOR) return true;
        return false;
    }

    @Override
    public String toDisplayString() {
        if (this.getAssignedTo() == 0) this.setAssignedTo(-1);
        return String.format("Description: %s, Language: %s", this.getNotes(), this.getLanguageType().name());
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Interpreter");
    }


    @Override
    public String getType(){
        return "Interpreter";
    }
}
