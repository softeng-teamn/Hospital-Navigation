package model.request;

import model.Node;

public class AVServiceRequest extends Request {

   public AVServiceRequest(int id, String notes, Node location, boolean completed){
        super(id,notes,location,completed);
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
