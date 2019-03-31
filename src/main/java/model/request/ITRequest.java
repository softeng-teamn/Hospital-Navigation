package model.request;

import model.Node;
import model.RequestType;

import static model.RequestType.RType.ITS;

public class ITRequest extends  Request {

    String description;

    public ITRequest(String id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.description = "";
        this.requestType = new RequestType(ITS);
    }

    public ITRequest(String id, String notes, Node location, boolean completed, String description) {
        super(id, notes, location, completed);
        this.description = description;
        this.requestType = new RequestType(ITS);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
