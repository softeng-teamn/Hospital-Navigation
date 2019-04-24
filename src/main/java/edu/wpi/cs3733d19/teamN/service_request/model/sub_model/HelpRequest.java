package edu.wpi.cs3733d19.teamN.service_request.model.sub_model;

import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import edu.wpi.cs3733d19.teamN.employee.model.JobType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.service_request.model.Request;

public class HelpRequest extends Request {
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    public HelpRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertHelpRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateHelpRequest((HelpRequest)this);
    }

    @Override
    public ObservableList<Request> showProperRequest() {
        return (ObservableList) myDBS.getAllHelpRequests();
    }

    @Override
    public ObservableList<Employee> returnCorrectEmployee() {
        ObservableList<Employee> allEmployee = FXCollections.observableArrayList();
        allEmployee.addAll(myDBS.getAllEmployees()) ;

        return allEmployee ;
    }

    @Override
    public void updateEmployee(Request selectedTask, Employee selectedEmp) {
        myDBS.updateHelpRequest((HelpRequest) selectedTask) ;
    }

    @Override
    public boolean fulfillableByType(JobType jobType) {
        return true;
    }

    @Override
    public String toDisplayString() {
        return "";
    }

    @Override
    public boolean isOfType(String typeString) {
        return typeString.equals("Help");
    }


    @Override
    public String getType(){
        return "Help me";
    }
}
