package controller;

import model.request.Request;

import java.util.Collection;

public class RequestController extends Controller {

    private Collection<Request> pendingRequests;


    // switches window to home screen
    // "cancel" button
    void showHome() {

    }

    // submits request to database
    // "confirm" button
    void makeRequest(Request type) {

    }

    // removes object from database
    void fufillRequest(String requestID, String byWho) {

    }

    // getter for pendingRequests
    public Collection<Request> getPendingRequests () {
        return pendingRequests  ;
    }

}
