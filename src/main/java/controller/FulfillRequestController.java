package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Employee;
import model.JobType;
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static model.JobType.*;

public class FulfillRequestController extends Controller implements Initializable {

    @FXML
    private JFXButton homeBtn;
    @FXML
    private JFXButton adminBtn;
    @FXML
    private JFXListView requestListView;
    @FXML
    private JFXListView employeeListView;
    @FXML
    private JFXRadioButton allTypeRadio;
    @FXML
    private JFXRadioButton medicineRadio;
    @FXML
    private JFXRadioButton ITRadio;
    @FXML
    private JFXRadioButton allRadio;
    @FXML
    private JFXRadioButton uncRadio;
    @FXML
    private VBox typeVBox;



    static DatabaseService myDBS = DatabaseService.getDatabaseService();


    /**
     * switches window to home screen
     *
     * @throws Exception
     */
    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    /**
     * sets a request as completed in the database
     */
    @FXML
    public void fulfillRequest() {
        Request selected = (Request) requestListView.getSelectionModel().getSelectedItem();
        MedicineRequest medupdate;
        ITRequest ITupdate;

        selected.setCompleted(true);

        RequestType rType = selected.getRequestType();

        switch (rType.getrType()) {
            case ITS:
                ITupdate = (ITRequest) selected;
                myDBS.updateITRequest(ITupdate);
                break;
            case MED:
                medupdate = (MedicineRequest) selected;
                myDBS.updateMedicineRequest(medupdate);
                break;
            case ABS:
                //do nothing
        }

        reloadList();
    }

    /**
     * TBD
     */
    @FXML
    public void showAdmin() {

    }

    /**
     * changes list, employee everytime radio button changes
     *
     * @param event
     */
    @FXML
    public void radioChanged(ActionEvent event) {
        reloadList();
        reloadEmployees() ;
    }

    /**
     * reloads the list of requests
     */
    public void reloadList() {
        ObservableList<Request> newRequestlist = FXCollections.observableArrayList();

        if (allRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) myDBS.getAllITRequests();

                newRequestlist = showProperRequest(newRequestlist, allMedReqList, allITReqList);

            } else if (medicineRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            } else if (ITRadio.isSelected()) {
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) myDBS.getAllITRequests();
                newRequestlist.addAll(allITReqList);
            }
        } else if (uncRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) myDBS.getAllIncompleteMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) myDBS.getAllIncompleteITRequests();
                newRequestlist = showProperRequest(newRequestlist, allMedReqList, allITReqList);
            } else if (medicineRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) myDBS.getAllIncompleteMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            } else if (ITRadio.isSelected()) {
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) myDBS.getAllIncompleteITRequests();
                newRequestlist.addAll(allITReqList);
            }
        }

        printList(newRequestlist);
    }

    /**
     * Prints out the list of Requests
     *
     * @param newReqList
     */
    public void printList(ObservableList<Request> newReqList) {
        requestListView.getItems().clear();
        requestListView.setItems(newReqList);

        // Set the cell to display only the name of the reservableSpace
        requestListView.setCellFactory(param -> new ListCell<Request>() {
            @Override
            protected void updateItem(Request item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || printRequest(item) == null) {
                    setText(null);
                } else {
                    setText(printRequest(item));
                }
            }
        });
        requestListView.setEditable(false);
    }


    /**
     * Prints out the list of Employees
     *
     * @param employeeList
     */

    public void printEList(ObservableList<Employee> employeeList) {
        employeeListView.getItems().clear();
        employeeListView.setItems(employeeList);

        // Set the cell to display only the name of the reservableSpace
        employeeListView.setCellFactory(param -> new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || printEmployee(item) == null) {
                    setText(null);
                } else {
                    setText(printEmployee(item));
                }
            }
        });
        requestListView.setEditable(false);
    }


    /**
     * relaod employees when selected changes
     */
    @FXML
    public void requestSelectionChanged() {
        reloadEmployees() ;
    }




    /**
     * reload the list of employees - similar to reloadList for requests but
     * loads by selected Request rather than radio button change
     * Dependant on selected Request & radio button
     */
    public void reloadEmployees() {

        Request selected = (Request) requestListView.getSelectionModel().getSelectedItem();

        // fetch all the employees from databse
        ArrayList<Employee> allEmployees = (ArrayList) myDBS.getAllEmployees();
        // final list of "correct" employees
        ObservableList<Employee> returnList = FXCollections.observableArrayList();

        // if all requests radio (either fulfilled and unfulfilled) is selected
       if (allRadio.isSelected() || uncRadio.isSelected()) {
           // if all types of request radio is selected
            if (allTypeRadio.isSelected()) {
                // if no filters selected, but unfulfilled request is selected in list
                if (selected.isCompleted() == false) {
                    returnList = employeeForSelectedJob();
                    // if no filters selected, but FULFILLED request is selected in list
                } else if (selected.isCompleted() == true) {
                    // give back all employees
                    returnList.addAll(allEmployees);
                }
            } else if (medicineRadio.isSelected()) {
                // show medical staff
                returnList = employeeForSelectedJob();
                // same for IT
            } else if (ITRadio.isSelected()) {
                returnList = employeeForSelectedJob();
            }
        }
        printEList(returnList);
    }


    /**
     * returns the proper list of employee based on the selected job
     *
     * @return list of correct employees
     */
    public ObservableList<Employee> employeeForSelectedJob() {




        Request selected;
        // if there are extisting requests
        if (requestListView != null) {
            // get the select the first request in the list
            //requestListView.getSelectionModel().select(0);
            // assign the selected item to a variable
            selected = (Request) requestListView.getSelectionModel().getSelectedItem();
            // otherwise set to null
        } else {
            selected = null;
        }


        // selected request
        //Request selected = (Request) requestListView.getSelectionModel().getSelectedItem();

        // new list for correct employees based on request type
        ArrayList<Employee> newEmployeeList;
        ArrayList<Employee> newITEmployeeList = new ArrayList<>();
        ArrayList<Employee> newMedEmployeeList = new ArrayList<>();
        ArrayList<Employee> newAbsEmployeeList = new ArrayList<>();
        ObservableList<Employee> returnEmployeeList = FXCollections.observableArrayList();

        // get all Employees from database
        newEmployeeList = (ArrayList) myDBS.getAllEmployees();


        // ****** sort employees by job  **********
        // loop over all employees and sort by job responsisbilities
        for (int i = 0; i < newEmployeeList.size(); i++) {
            // if admin, add to all lists
            if (newEmployeeList.get(i).getJob() == ADMINISTRATOR) {
                newITEmployeeList.add(newEmployeeList.get(i));
                newMedEmployeeList.add(newEmployeeList.get(i));
                newAbsEmployeeList.add(newEmployeeList.get(i));
            }
            // if IT employee add to IT
            if (newEmployeeList.get(i).getJob() == IT) {
                newITEmployeeList.add(newEmployeeList.get(i));
            }
            // add doctors and nurses to medList
            if (newEmployeeList.get(i).getJob() == DOCTOR || newEmployeeList.get(i).getJob() == NURSE) {
                newMedEmployeeList.add(newEmployeeList.get(i));
            }
            // everything else add to "ABS" list - can adjust as needed as we create more Request types
            if (newEmployeeList.get(i).getJob() == SECURITY_PERSONNEL || newEmployeeList.get(i).getJob() == JANITOR || newEmployeeList.get(i).getJob() == MAINTENANCE_WORKER) {
                newAbsEmployeeList.add(newEmployeeList.get(i));
            }
        }

        // switch case
        // return proper employeelist based on Request type
        switch (selected.getRequestType().getrType()) {
            case ITS:
                returnEmployeeList.addAll(newITEmployeeList);
                break;
            case MED:
                returnEmployeeList.addAll(newMedEmployeeList);
                break;
            case ABS:
                returnEmployeeList.addAll(newAbsEmployeeList);
            default:
                System.out.println("Incorrect Job Type: + " + selected.getRequestType().getrType());
        }

        return returnEmployeeList;


    }


    /**
     * Prints out a single request
     *
     * @param request
     * @return
     */
    public String printRequest(Request request) {
        if (request == null) {
            return null;
        }

        return "ID: " + request.getId() +
                " Request Type: " + request.getRequestType().getrType().toString() +
                " Description: " + request.getNotes();
    }

    /**
     * Prints out a single employee
     *
     * @param e
     * @return
     */
    public String printEmployee(Employee e) {
        if (e == null) {
            return null;
        }
        return "ID: " + e.getID() +
                " Job: " + e.getJob().toString();
    }



    //Show Requests based on Job
    private ObservableList<Request> showProperRequest(ObservableList<Request> newRequestList, ArrayList<MedicineRequest> allMedReqList,ArrayList<ITRequest> allITReqList){

        //Add request radio button
        typeVBox.getChildren().removeAll(medicineRadio,ITRadio);


        //Add requests to proper jobs
        switch (Controller.getCurrentJob()) {
            case ADMINISTRATOR:
                newRequestList.addAll(allMedReqList);
                newRequestList.addAll(allITReqList);
                typeVBox.getChildren().addAll(medicineRadio,ITRadio);
                break;
            case DOCTOR:
                newRequestList.addAll(allMedReqList);
                typeVBox.getChildren().addAll(medicineRadio);
                break;
            case NURSE:
                newRequestList.addAll(allMedReqList);
                typeVBox.getChildren().addAll(medicineRadio);
                break;
            case IT:
                newRequestList.addAll(allITReqList);
                typeVBox.getChildren().addAll(ITRadio);
                break;
            case SECURITY_PERSONNEL:
                break;
            case JANITOR:
                break;
            case MAINTENANCE_WORKER:
                break;
            default:
                break;

        }

        return newRequestList;
    }


    /**
     * initialize the list of requests
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Request> requestlist = FXCollections.observableArrayList();
        ObservableList<Employee> EmployeeList = FXCollections.observableArrayList();

        ArrayList<Employee> allEs = (ArrayList) myDBS.getAllEmployees();
        ArrayList<MedicineRequest> medicineReq = (ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests();
        ArrayList<ITRequest> itReq = (ArrayList<ITRequest>) myDBS.getAllITRequests();

        requestlist = showProperRequest(requestlist, medicineReq, itReq);
        EmployeeList.addAll(allEs);

        printList(requestlist);
        printEList(EmployeeList);

        Request selected;
        // if there are extisting requests
        if (requestListView != null) {
            // get the select the first request in the list
            requestListView.getSelectionModel().select(0);
            // assign the selected item to a variable
            selected = (Request) requestListView.getSelectionModel().getSelectedItem();
            // otherwise set to null
        } else {
            selected = null;
        }

    }
}
