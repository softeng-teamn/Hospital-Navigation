package model.request;

import model.Node;

public class SecurityRequest extends Request {

    public SecurityRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }
}
