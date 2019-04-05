package model.request;

import com.jfoenix.controls.JFXToggleNode;
import model.Node;
import service.DatabaseService;

public class RequestFacade {

    private JFXToggleNode selected;
    private String description;
    private Node requestLocation;
    private Request Type;
    private String byWho;

    public RequestFacade(JFXToggleNode selected, String description, Node requestLocation) {
        this.selected = selected;
        this.description = description;
        this.requestLocation = requestLocation;
    }

    public RequestFacade(Request type, String byWho) {
        Type = type;
        this.byWho = byWho;
    }

    // to make  a new IT Request
    public void makeITRequest(){
        ITRequest newITRequest = new ITRequest(-1, description, requestLocation, false);
        DatabaseService.getDatabaseService().insertITRequest(newITRequest);
    }

    // to make a new medicine Request
    public void makeMedRequest(){
        MedicineRequest newMedicineRequest = new MedicineRequest(-1, description, requestLocation, false);
        DatabaseService.getDatabaseService().insertMedicineRequest(newMedicineRequest);
    }


    // to fulfill an existing IT Request
    public void fillITRequest(){
        Type.setCompleted(true);
        Type.setCompletedBy(byWho);
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)Type);
    }


    // to fulfill an existing medicine Request
    public void fillMedRequest(){
        Type.setCompleted(true);
        Type.setCompletedBy(byWho);
        DatabaseService.getDatabaseService().updateMedicineRequest((MedicineRequest) Type);
    }



}
