package model.request;

import com.jfoenix.controls.JFXToggleNode;
import model.Node;
import service.DatabaseService;

public class RequestFacade {


    private ITRequest itRequest ;
    private MedicineRequest medRequest ;

    public RequestFacade(ITRequest itRequest, MedicineRequest medRequest) {
        this.itRequest = itRequest;
        this.medRequest = medRequest;
    }

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
        itRequest.makeRequest() ;

    }

    // to make a new medicine Request
    public void makeMedRequest(){
        medRequest.makeRequest() ;
    }

    // to fulfill an existing IT Request
    public void fillITRequest(){
        itRequest.fillRequest() ;

    }

    // to fulfill an existing medicine Request
    public void fillMedRequest(){
        medRequest.fillRequest() ;
    }



}
