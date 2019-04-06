package model.request;

import com.jfoenix.controls.JFXToggleNode;
import model.Node;
import model.RequestType;
import service.DatabaseService;

import static model.RequestType.RType.ITS;

import java.util.Objects;

public class ITRequest extends Request {

    String description;

    public ITRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.description = "";
        //this.requestType = new Request(ITS);
    }

    public ITRequest(int id, String notes, Node location, boolean completed, String description) {
        super(id, notes, location, completed);
        this.description = description;
        //this.requestType = new RequestType(ITS);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ITRequest itRequest = (ITRequest) o;
        return Objects.equals(description, itRequest.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }


    @Override
    public void makeRequest () {
        ITRequest newITRequest = new ITRequest(-1, description, this.getLocation(), false);
        DatabaseService.getDatabaseService().insertITRequest(newITRequest);
    }

    @Override
    public void fillRequest () {
        this.setCompleted(true);
        this.setCompletedBy(this.getCompletedBy());
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)this);

    }

}



