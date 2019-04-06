package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
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

public class FulfillRequestController extends Controller implements Initializable {

    @FXML
    private JFXButton homeBtn;
    @FXML
    private JFXButton adminBtn;
    @FXML
    private JFXListView requestListView;
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
                DatabaseService.getDatabaseService().updateITRequest(ITupdate);
                break;
            case MED:
                medupdate = (MedicineRequest) selected;
                DatabaseService.getDatabaseService().updateMedicineRequest(medupdate);
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
     * TBD
     *
     * @param event
     */
    @FXML
    public void radioChanged(ActionEvent event) {
        reloadList();

    }

    /**
     * reloads the list of requests
     */
    public void reloadList() {
        ObservableList<Request> newRequestlist = FXCollections.observableArrayList();

        if (allRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) DatabaseService.getDatabaseService().getAllMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) DatabaseService.getDatabaseService().getAllITRequests();

                newRequestlist = showProperRequest(newRequestlist, allMedReqList, allITReqList);

            } else if (medicineRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) DatabaseService.getDatabaseService().getAllMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            } else if (ITRadio.isSelected()) {
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) DatabaseService.getDatabaseService().getAllITRequests();
                newRequestlist.addAll(allITReqList);
            }
        } else if (uncRadio.isSelected()) {
            if (allTypeRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) DatabaseService.getDatabaseService().getAllIncompleteMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) DatabaseService.getDatabaseService().getAllIncompleteITRequests();
                newRequestlist = showProperRequest(newRequestlist, allMedReqList, allITReqList);
            } else if (medicineRadio.isSelected()) {
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) DatabaseService.getDatabaseService().getAllIncompleteMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            } else if (ITRadio.isSelected()) {
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) DatabaseService.getDatabaseService().getAllIncompleteITRequests();
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

    //Show Requests based on Job
    private ObservableList<Request> showProperRequest(ObservableList<Request> newRequestList, ArrayList<MedicineRequest> allMedReqList,ArrayList<ITRequest> allITReqList){

        switch (Controller.getCurrentJob()) {
            case ADMINISTRATOR:
                newRequestList.addAll(allMedReqList);
                newRequestList.addAll(allITReqList);
                break;
            case DOCTOR:
                newRequestList.addAll(allMedReqList);
                break;
            case NURSE:
                newRequestList.addAll(allMedReqList);
                break;
            case IT:
                newRequestList.addAll(allITReqList);
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

        ArrayList<MedicineRequest> medicineReq = (ArrayList<MedicineRequest>) DatabaseService.getDatabaseService().getAllMedicineRequests();
        ArrayList<ITRequest> itReq = (ArrayList<ITRequest>) DatabaseService.getDatabaseService().getAllITRequests();

        requestlist = showProperRequest(requestlist, medicineReq, itReq);

        printList(requestlist);

    }
}
