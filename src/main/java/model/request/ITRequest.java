package model.request;

import model.Node;

public class ITRequest extends  Request {

    String description;

    public ITRequest(String id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.description = "";
    }

    public ITRequest(String id, String notes, Node location, boolean completed, String description) {
        super(id, notes, location, completed);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
