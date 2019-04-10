package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import controller.requests.InternalTransportController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Employee;
import model.JobType;
import model.request.*;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private JFXRadioButton avReqRadio; //done
    @FXML
    private JFXRadioButton religiousRadio; //done
    @FXML
    private JFXRadioButton interpretRadio; //done
    @FXML
    private JFXRadioButton maintenanceRadio; //done
    @FXML
    private JFXRadioButton toyRadio; //done
    @FXML
    private JFXRadioButton securityRadio; //done
    @FXML
    private JFXRadioButton sanitationRadio; //done
    @FXML
    private JFXRadioButton patientInfoRadio; //done
    @FXML
    private JFXRadioButton floristRadio; //done
    @FXML
    private JFXRadioButton giftRadio; //done
    @FXML
    private JFXRadioButton internalTransRadio; //done
    @FXML
    private JFXRadioButton externalTransRadio;
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
        selected.fillRequest();
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
        reloadEmployees();
    }

    @FXML
    public void assignEmployee (ActionEvent event) {
        Request selectedTask = (Request) requestListView.getSelectionModel().getSelectedItem();
        Employee selectedEmp = (Employee) employeeListView.getSelectionModel().getSelectedItem();
        selectedTask.setAssignedTo(selectedEmp.getID());

        requestListView.getItems().set(requestListView.getSelectionModel().getSelectedIndex(), selectedTask) ;
        ObservableList<Request> x = FXCollections.observableArrayList();
        for (int i = 0 ; i < requestListView.getItems().size() ; i ++) {
            x.add((Request)requestListView.getItems().get(i));
        }
        printList(x);
    }






    /**
     * reloads the list of requests
     */
    public void reloadList() {

        // return requests
        ObservableList<Request> finalRequestList = FXCollections.observableArrayList();

        /*ObservableList<Request> obsRequestList = FXCollections.observableArrayList();
        //List<AVServiceRequest> av = myDBS.getAllAVServiceRequests();
        List<MedicineRequest> mr =  myDBS.getAllMedicineRequests();
        List<ITRequest> it = myDBS.getAllITRequests();*/
        // total requests
        System.out.println("The current job of this controller is: " + Controller.getCurrentJob().name());
        ObservableList<Request> allRequestlist = FXCollections.observableArrayList();
        allRequestlist.addAll((ArrayList<AVServiceRequest>) myDBS.getAllAVServiceRequests());
        allRequestlist.addAll((ArrayList<ExternalTransportRequest>) myDBS.getAllExtTransRequests());
        allRequestlist.addAll((ArrayList<FloristRequest>) myDBS.getAllFloristRequests());
        allRequestlist.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllCompleteGiftStoreRequests());
        allRequestlist.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllIncompleteGiftStoreRequests());
        allRequestlist.addAll((ArrayList<InternalTransportRequest>) myDBS.getAllInternalTransportRequest());
        allRequestlist.addAll((ArrayList<InterpreterRequest>) myDBS.getAllInterpreterRequests());
        if(Controller.getCurrentJob().name().equals("ADMINISTRATOR") || Controller.getCurrentJob().name().equals("IT")){
            allRequestlist.addAll((ArrayList<ITRequest>) myDBS.getAllITRequests());
        }
        allRequestlist.addAll((ArrayList<MaintenanceRequest>) myDBS.getAllMaintenanceRequests());
        if(Controller.getCurrentJob().name().equals("ADMINISTRATOR") ||
        Controller.getCurrentJob().name().equals("DOCTOR")){
            allRequestlist.addAll((ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests());
        }
        allRequestlist.addAll((ArrayList<PatientInfoRequest>) myDBS.getAllPatientInfoRequests());
        allRequestlist.addAll((ArrayList<ReligiousRequest>) myDBS.getAllReligiousRequests());
        allRequestlist.addAll((ArrayList<SanitationRequest>) myDBS.getAllSanitationRequests());
        allRequestlist.addAll((ArrayList<SecurityRequest>) myDBS.getAllSecurityRequests());
        allRequestlist.addAll((ArrayList<ToyRequest>) myDBS.getAllToyRequests());

        // total incomplete requests
        ObservableList<Request> allIncompleteRequestlist = FXCollections.observableArrayList();
        allIncompleteRequestlist.addAll((ArrayList<AVServiceRequest>) myDBS.getAllIncompleteAVServiceRequests());
        allIncompleteRequestlist.addAll((ArrayList<ExternalTransportRequest>) myDBS.getAllIncompleteExtTransRequests());
        allIncompleteRequestlist.addAll((ArrayList<FloristRequest>) myDBS.getAllFloristRequests());
        allIncompleteRequestlist.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllIncompleteGiftStoreRequests());
        allIncompleteRequestlist.addAll((ArrayList<InternalTransportRequest>) myDBS.getAllIncompleteInternalTransportRequests());
        allIncompleteRequestlist.addAll((ArrayList<InterpreterRequest>) myDBS.getAllIncompleteInterpreterRequests());

        if(Controller.getCurrentJob().name().equals("ADMINISTRATOR") || Controller.getCurrentJob().name().equals("IT")){
            allIncompleteRequestlist.addAll((ArrayList<ITRequest>) myDBS.getAllIncompleteITRequests());
        }
        allIncompleteRequestlist.addAll((ArrayList<MaintenanceRequest>) myDBS.getAllIncompleteMaintenanceRequests());
        if(Controller.getCurrentJob().name().equals("ADMINISTRATOR") ||
                Controller.getCurrentJob().name().equals("DOCTOR")){
            allIncompleteRequestlist.addAll((ArrayList<MedicineRequest>) myDBS.getAllIncompleteMedicineRequests());
        }

        allIncompleteRequestlist.addAll((ArrayList<PatientInfoRequest>) myDBS.getAllIncompletePatientInfoRequests());
        allIncompleteRequestlist.addAll((ArrayList<ReligiousRequest>) myDBS.getAllIncompleteReligiousRequests());
        allIncompleteRequestlist.addAll((ArrayList<SanitationRequest>) myDBS.getAllIncompleteSanitationRequests());
        allIncompleteRequestlist.addAll((ArrayList<SecurityRequest>) myDBS.getAllIncompleteSecurityRequests());
        allIncompleteRequestlist.addAll((ArrayList<ToyRequest>) myDBS.getAllIncompleteToyRequests());


        if (allRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                //finalRequestList = allRequestlist;
                ObservableList<Request> obsRequestList = FXCollections.observableArrayList();
                List<GiftStoreRequest> gift = myDBS.getAllCompleteGiftStoreRequests();
                gift.addAll(myDBS.getAllIncompleteGiftStoreRequests());
                List<AVServiceRequest> av = myDBS.getAllAVServiceRequests();
                List<MedicineRequest> mr =  myDBS.getAllMedicineRequests();
                List<ITRequest> it = myDBS.getAllITRequests();
                List<ExternalTransportRequest> ex = myDBS.getAllExtTransRequests();
                List<FloristRequest> fl = myDBS.getAllFloristRequests();
                List<SecurityRequest> seq = myDBS.getAllSecurityRequests();
                List<SanitationRequest> jan = myDBS.getAllSanitationRequests();
                List<MaintenanceRequest> man = myDBS.getAllMaintenanceRequests();
                List<ReligiousRequest> rel = myDBS.getAllReligiousRequests();
                List<InterpreterRequest> interp = myDBS.getAllInterpreterRequests();
                List<ToyRequest> toy = myDBS.getAllToyRequests();
                List<PatientInfoRequest> pinfo = myDBS.getAllPatientInfoRequests();
                List<InternalTransportRequest> intTrans = myDBS.getAllInternalTransportRequest();
                finalRequestList = showProperRequest(obsRequestList, mr, it, seq, jan, man, rel, gift, av, interp, toy, pinfo, fl, intTrans, ex);
            } else if (medicineRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests());
            } else if (ITRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ITRequest>) myDBS.getAllITRequests());
            } else if (avReqRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<AVServiceRequest>) myDBS.getAllAVServiceRequests());
            } else if (religiousRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ReligiousRequest>) myDBS.getAllReligiousRequests());
            } else if (interpretRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<InterpreterRequest>) myDBS.getAllInterpreterRequests());
            } else if (maintenanceRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<MaintenanceRequest>) myDBS.getAllMaintenanceRequests());
            } else if (toyRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ToyRequest>) myDBS.getAllToyRequests());
            } else if (securityRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<SecurityRequest>) myDBS.getAllSecurityRequests());
            } else if (sanitationRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<SanitationRequest>) myDBS.getAllSanitationRequests());
            } else if (patientInfoRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<PatientInfoRequest>) myDBS.getAllPatientInfoRequests());
            } else if (floristRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<FloristRequest>) myDBS.getAllFloristRequests());
            } else if (giftRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllCompleteGiftStoreRequests());
                finalRequestList.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllIncompleteGiftStoreRequests());
            } else if (internalTransRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<InternalTransportRequest>) myDBS.getAllInternalTransportRequest());
            } else if (externalTransRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ExternalTransportRequest>) myDBS.getAllExtTransRequests());
            }
        } else if (uncRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                ObservableList<Request> obsRequestList = FXCollections.observableArrayList();
                List<GiftStoreRequest> gift = myDBS.getAllCompleteGiftStoreRequests();
                gift.addAll(myDBS.getAllIncompleteGiftStoreRequests());
                List<AVServiceRequest> av = myDBS.getAllAVServiceRequests();
                List<MedicineRequest> mr =  myDBS.getAllMedicineRequests();
                List<ITRequest> it = myDBS.getAllITRequests();
                List<ExternalTransportRequest> ex = myDBS.getAllExtTransRequests();
                List<FloristRequest> fl = myDBS.getAllFloristRequests();
                List<SecurityRequest> seq = myDBS.getAllSecurityRequests();
                List<SanitationRequest> jan = myDBS.getAllSanitationRequests();
                List<MaintenanceRequest> man = myDBS.getAllMaintenanceRequests();
                List<ReligiousRequest> rel = myDBS.getAllReligiousRequests();
                List<InterpreterRequest> interp = myDBS.getAllInterpreterRequests();
                List<ToyRequest> toy = myDBS.getAllToyRequests();
                List<PatientInfoRequest> pinfo = myDBS.getAllPatientInfoRequests();
                List<InternalTransportRequest> intTrans = myDBS.getAllInternalTransportRequest();
                finalRequestList = showProperRequest(obsRequestList, mr, it, seq, jan, man, rel, gift, av, interp, toy, pinfo, fl, intTrans, ex);
               //finalRequestList = allIncompleteRequestlist;
            } else if (medicineRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<MedicineRequest>) myDBS.getAllIncompleteMedicineRequests());
            } else if (ITRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ITRequest>) myDBS.getAllIncompleteITRequests());
            } else if (avReqRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<AVServiceRequest>) myDBS.getAllIncompleteAVServiceRequests());
            } else if (religiousRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ReligiousRequest>) myDBS.getAllIncompleteReligiousRequests());
            } else if (interpretRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<InterpreterRequest>) myDBS.getAllIncompleteInterpreterRequests());
            } else if (maintenanceRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<MaintenanceRequest>) myDBS.getAllIncompleteMaintenanceRequests());
            } else if (toyRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ToyRequest>) myDBS.getAllIncompleteToyRequests());
            } else if (securityRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<SecurityRequest>) myDBS.getAllIncompleteSecurityRequests());
            } else if (sanitationRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<SanitationRequest>) myDBS.getAllIncompleteSanitationRequests());
            } else if (patientInfoRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<PatientInfoRequest>) myDBS.getAllIncompletePatientInfoRequests());
            } else if (floristRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<FloristRequest>) myDBS.getAllFloristRequests());
            } else if (giftRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllIncompleteGiftStoreRequests());
            } else if (internalTransRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<InternalTransportRequest>) myDBS.getAllIncompleteInternalTransportRequests());
            } else if (externalTransRadio.isSelected()) {
                finalRequestList.addAll((ArrayList<ExternalTransportRequest>) myDBS.getAllIncompleteExtTransRequests());
            }
        }

        printList(finalRequestList);
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
        requestListView.setEditable(true);
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
        reloadEmployees();
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

        if (selected != null) {
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
        }
        printEList(returnList);
    }


    /**
     * returns the proper list of employee based on the selected job
     *
     * @return list of correct employees
     */
    public ObservableList<Employee> employeeForSelectedJob() {

        ObservableList<Employee> returnEmployeeList = FXCollections.observableArrayList();

        Request selected = (Request) requestListView.getSelectionModel().getSelectedItem();
        if (selected != null) {

            // get all Employees from database
            // ArrayList<Employee> newEmployeeList = (ArrayList) myDBS.getAllEmployees();

            returnEmployeeList = selected.returnCorrectEmployee();
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
                " Request Type: " + request.getClass().getSimpleName() +
                " Description: " + request.getNotes() +
                " Assigned To: " + request.getAssignedTo();
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
                " Job: " + e.getJob().toString() +
                 " Username: " + e.getUsername() ;
    }


    //Show Requests based on Job
    private ObservableList<Request> showProperRequest(ObservableList<Request> newRequestList, List<MedicineRequest> allMedReqList,
                                                      List<ITRequest> allITReqList, List<SecurityRequest> allSeqReqList,
                                                      List<SanitationRequest> allJanReqList, List<MaintenanceRequest> allMainReqList,
                                                      List<ReligiousRequest> allRelReqList, List<GiftStoreRequest> allGiftReqList,
                                                      List<AVServiceRequest> allAVReqList, List<InterpreterRequest> allIntReqList,
                                                      List<ToyRequest> allToyReqList, List<PatientInfoRequest> allPInfReqList,
                                                      List<FloristRequest> allFloReqList, List<InternalTransportRequest> allIntTransReqList,
                                                      List<ExternalTransportRequest> allExtTransReqList){

        //Add request radio button
        typeVBox.getChildren().removeAll(medicineRadio,ITRadio);


        //Add requests to proper jobs
        switch (Controller.getCurrentJob()) {
            case ADMINISTRATOR:
                newRequestList.addAll(allMedReqList);
                newRequestList.addAll(allITReqList);
                newRequestList.addAll(allGiftReqList);
                newRequestList.addAll(allJanReqList);
                newRequestList.addAll(allMainReqList);
                newRequestList.addAll(allRelReqList);
                newRequestList.addAll(allSeqReqList);
                newRequestList.addAll(allAVReqList);
                newRequestList.addAll(allExtTransReqList);
                newRequestList.addAll(allFloReqList);
                newRequestList.addAll(allIntReqList);
                newRequestList.addAll(allIntTransReqList);
                newRequestList.addAll(allPInfReqList);
                newRequestList.addAll(allToyReqList);
                typeVBox.getChildren().add(medicineRadio);
                typeVBox.getChildren().add(ITRadio);
                /*typeVBox.getChildren().add(securityRadio);
                typeVBox.getChildren().add(sanitationRadio);
                typeVBox.getChildren().addAll(maintenanceRadio);
                typeVBox.getChildren().addAll(religiousRadio);
                typeVBox.getChildren().addAll(giftRadio);
                typeVBox.getChildren().addAll(avReqRadio);
                typeVBox.getChildren().addAll(internalTransRadio);
                typeVBox.getChildren().addAll(interpretRadio);
                typeVBox.getChildren().addAll(toyRadio);
                typeVBox.getChildren().addAll(patientInfoRadio);
                typeVBox.getChildren().addAll(floristRadio);
                typeVBox.getChildren().addAll(externalTransRadio);*/
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
                newRequestList.addAll(allSeqReqList);
                typeVBox.getChildren().addAll(securityRadio);
                break;
            case JANITOR:
                newRequestList.addAll(allJanReqList);
                typeVBox.getChildren().addAll(sanitationRadio);
                break;
            case MAINTENANCE_WORKER:
                newRequestList.addAll(allMainReqList);
                typeVBox.getChildren().addAll(maintenanceRadio);
                break;
            case RELIGIOUS_OFFICIAL:
                newRequestList.addAll(allRelReqList);
                typeVBox.getChildren().addAll(religiousRadio);
                break;
            case GIFT_SERVICES:
                newRequestList.addAll(allGiftReqList);
                typeVBox.getChildren().addAll(giftRadio);
                break;
            case AV:
                newRequestList.addAll(allAVReqList);
                typeVBox.getChildren().addAll(avReqRadio);
                break;
            case INTERPRETER:
                newRequestList.addAll(allIntReqList);
                typeVBox.getChildren().addAll(interpretRadio);
                break;
            case TOY:
                newRequestList.addAll(allToyReqList);
                typeVBox.getChildren().addAll(toyRadio);
                break;
            case PATIENT_INFO:
                newRequestList.addAll(allPInfReqList);
                typeVBox.getChildren().addAll(patientInfoRadio);
                break;
            case FLORIST:
                newRequestList.addAll(allFloReqList);
                typeVBox.getChildren().addAll(floristRadio);
                break;
            case INTERNAL_TRANSPORT:
                newRequestList.addAll(allIntTransReqList);
                typeVBox.getChildren().addAll(internalTransRadio);
                break;
            case EXTERNAL_TRANSPORT:
                newRequestList.addAll(allExtTransReqList);
                typeVBox.getChildren().addAll(externalTransRadio);
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
       ObservableList<Request> obsRequestList = FXCollections.observableArrayList();
        List<GiftStoreRequest> gift = myDBS.getAllCompleteGiftStoreRequests();
        gift.addAll(myDBS.getAllIncompleteGiftStoreRequests());
        List<AVServiceRequest> av = myDBS.getAllAVServiceRequests();
        List<MedicineRequest> mr =  myDBS.getAllMedicineRequests();
        List<ITRequest> it = myDBS.getAllITRequests();
        List<ExternalTransportRequest> ex = myDBS.getAllExtTransRequests();
        List<FloristRequest> fl = myDBS.getAllFloristRequests();
        List<SecurityRequest> seq = myDBS.getAllSecurityRequests();
        List<SanitationRequest> jan = myDBS.getAllSanitationRequests();
        List<MaintenanceRequest> man = myDBS.getAllMaintenanceRequests();
        List<ReligiousRequest> rel = myDBS.getAllReligiousRequests();
        List<InterpreterRequest> interp = myDBS.getAllInterpreterRequests();
        List<ToyRequest> toy = myDBS.getAllToyRequests();
        List<PatientInfoRequest> pinfo = myDBS.getAllPatientInfoRequests();
        List<InternalTransportRequest> intTrans = myDBS.getAllInternalTransportRequest();

        ObservableList<Request> requestList = showProperRequest(obsRequestList, mr, it, seq, jan, man, rel, gift, av, interp, toy, pinfo, fl, intTrans, ex);
        ObservableList<Employee> EmployeeList = FXCollections.observableArrayList();

        ArrayList<Employee> allEs = (ArrayList) myDBS.getAllEmployees();

        // total requests
        ObservableList<Request> allRequestlist = FXCollections.observableArrayList();
        allRequestlist.addAll((ArrayList<AVServiceRequest>) myDBS.getAllAVServiceRequests());
        allRequestlist.addAll((ArrayList<ExternalTransportRequest>) myDBS.getAllExtTransRequests());
        allRequestlist.addAll((ArrayList<FloristRequest>) myDBS.getAllFloristRequests());
        allRequestlist.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllCompleteGiftStoreRequests());
        allRequestlist.addAll((ArrayList<GiftStoreRequest>) myDBS.getAllIncompleteGiftStoreRequests());
        allRequestlist.addAll((ArrayList<InternalTransportRequest>) myDBS.getAllInternalTransportRequest());
        allRequestlist.addAll((ArrayList<InterpreterRequest>) myDBS.getAllInterpreterRequests());
        allRequestlist.addAll((ArrayList<ITRequest>) myDBS.getAllITRequests());
        allRequestlist.addAll((ArrayList<MaintenanceRequest>) myDBS.getAllMaintenanceRequests());
        allRequestlist.addAll((ArrayList<MedicineRequest>) myDBS.getAllMedicineRequests());
        allRequestlist.addAll((ArrayList<PatientInfoRequest>) myDBS.getAllPatientInfoRequests());
        allRequestlist.addAll((ArrayList<ReligiousRequest>) myDBS.getAllReligiousRequests());
        allRequestlist.addAll((ArrayList<SanitationRequest>) myDBS.getAllSanitationRequests());
        allRequestlist.addAll((ArrayList<SecurityRequest>) myDBS.getAllSecurityRequests());
        allRequestlist.addAll((ArrayList<ToyRequest>) myDBS.getAllToyRequests());


        //requestlist = showProperRequest(requestlist, medicineReq, itReq, avReq, exTransReq, floristReq, gsReqC, gsReqI, internalTReq, interpReq, mainReq, patientReq, religReq, sanitReq, secReq, toyReq);
        EmployeeList.addAll(allEs);

        printList(requestList);
        //printEList(EmployeeList);

        Request selected;
        // if there are extisting requests
        if (requestListView != null) {
            // get the select the first request in the list
            requestListView.getSelectionModel().select(0);
            // otherwise set to null
        } else {
            selected = null;
        }

    }
}
