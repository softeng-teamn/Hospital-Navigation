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
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
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

    @FXML
    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }


    @FXML
    public  void fulfillRequest(){
        Request selected = (Request) requestListView.getSelectionModel().getSelectedItem();
        MedicineRequest medupdate;
        ITRequest ITupdate;

        selected.setCompleted(true);

        RequestType rType = selected.getRequestType();

        switch(rType.getrType()){
            case ITS:
                ITupdate = (ITRequest) selected;
                dbs.updateITRequest(ITupdate);
                break;
            case MED:
                medupdate = (MedicineRequest) selected;
                dbs.updateMedicineRequest(medupdate);
                break;
            case ABS:
                //do nothing
        }

        reloadList();

    }

    @FXML
    public void showAdmin() throws Exception{
        Stage stage = (Stage) adminBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.adminPage);
        StageManager.changeExistingWindow(stage, root, "Admin Page");
    }

    @FXML
    public void radioChanged(ActionEvent event){
        reloadList();

    }

    public void reloadList(){
        ObservableList<Request> newRequestlist = FXCollections.observableArrayList();

        if (allRadio.isSelected()){
            if(allTypeRadio.isSelected()){
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) dbs.getAllMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) dbs.getAllITRequests();
                newRequestlist.addAll(allMedReqList);
                newRequestlist.addAll(allITReqList);
            }else if (medicineRadio.isSelected()){
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) dbs.getAllMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            }else if (ITRadio.isSelected()){
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) dbs.getAllITRequests();
                newRequestlist.addAll(allITReqList);
            }
        }else if (uncRadio.isSelected()){
            if(allTypeRadio.isSelected()){
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) dbs.getAllIncompleteMedicineRequests();
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) dbs.getAllIncompleteITRequests();
                newRequestlist.addAll(allMedReqList);
                newRequestlist.addAll(allITReqList);
            }else if (medicineRadio.isSelected()){
                ArrayList<MedicineRequest> allMedReqList = (ArrayList<MedicineRequest>) dbs.getAllIncompleteMedicineRequests();
                newRequestlist.addAll(allMedReqList);
            }else if (ITRadio.isSelected()){
                ArrayList<ITRequest> allITReqList = (ArrayList<ITRequest>) dbs.getAllIncompleteITRequests();
                newRequestlist.addAll(allITReqList);
            }
        }

        printList(newRequestlist);
    }

    public void printList(ObservableList<Request> newReqList){
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


    public String printRequest(Request request){
        if (request == null){
            return null;
        }

        return "ID: " + request.getId() +
                " Request Type: " + request.getRequestType() +
                " Description: " + request.getNotes();
    }

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Request> requestlist = FXCollections.observableArrayList();

        ArrayList<MedicineRequest> medicineReq = (ArrayList<MedicineRequest>) dbs.getAllMedicineRequests();
        ArrayList<ITRequest> itReq = (ArrayList<ITRequest>) dbs.getAllITRequests();

        requestlist.addAll(medicineReq);
        requestlist.addAll(itReq);

        printList(requestlist);

    }
}
