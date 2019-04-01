package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import service.ResourceLoader;
import service.StageManager;

import java.util.ArrayList;
import java.util.Collection;

public class RequestController extends Controller {

    @FXML
    private JFXButton homeBtn;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;


    @FXML
    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    // submits request to database
    // "confirm" button
    void makeRequest(Request type) {
        RequestType rType = type.getRequestType();
        switch(rType.getrType()){
            case ITS:
                ITRequest ITType = (ITRequest) type;
                if(dbs.getITRequest(ITType.getId())==null) {
                    dbs.insertITRequest(ITType);
                }
                break;
            case MED:
                MedicineRequest medReq = (MedicineRequest) type;
                if(dbs.getMedicineRequest(medReq.getId())==null) {
                    dbs.insertMedicineRequest(medReq);
                }
                break;
            case ABS:
                //dont make a request if its not a real type
        }
    }

    // removes object from database
    void fufillRequest(Request type, String byWho) {
        RequestType rType = type.getRequestType();
        switch(rType.getrType()){
            case ITS:
                ITRequest ITReq = (ITRequest) type;
                ITReq.setCompleted(true);
                ITReq.setCompletedBy(byWho);
                dbs.updateITRequest(ITReq);
                break;
            case MED:
                MedicineRequest MedReq = (MedicineRequest) type;
                MedReq.setCompleted(true);
                MedReq.setCompletedBy(byWho);
                dbs.updateMedicineRequest(MedReq);
                break;
            case ABS:
                //do nothing
        }
    }

    // getter for pendingRequests
    public Collection<Request> getPendingRequests () {
        ArrayList<Request> requests = new ArrayList<>();
        requests.addAll(this.dbs.getAllIncompleteITRequests());
        requests.addAll(this.dbs.getAllIncompleteMedicineRequests());
        return requests;
    }

}
