package model.request;

import com.jfoenix.controls.JFXToggleNode;
import model.Node;
import service.DatabaseService;

public class RequestFacade {


    private Request request;


    public RequestFacade(Request request) {
        this.request = request;
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
    public void makeRequest(){
        request.makeRequest() ;

    }

    // to fulfill an existing medicine Request
    public void fillRequest(){
        request.fillRequest() ;
    }



}
