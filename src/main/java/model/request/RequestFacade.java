package model.request;

import com.jfoenix.controls.JFXToggleNode;
import model.Node;
import service.DatabaseService;

public class RequestFacade {

    private JFXToggleNode selected;
    private String description;
    private Node requestLocation;
    private Request requestType;
    private String byWho;

    public RequestFacade(JFXToggleNode selected, String description, Node requestLocation) {
        this.selected = selected;
        this.description = description;
        this.requestLocation = requestLocation;
    }

    public RequestFacade(Request type, String byWho) {
        requestType = type;
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
        
        requestType.setCompleted(true);
        requestType.setCompletedBy(byWho);
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)requestType);
        
        
    }

    // to fulfill an existing medicine Request
    public void fillMedRequest(){
        requestType.setCompleted(true);
        requestType.setCompletedBy(byWho);
        DatabaseService.getDatabaseService().updateMedicineRequest((MedicineRequest) requestType);
    }



}
