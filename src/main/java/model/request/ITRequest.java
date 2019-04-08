package model.request;

import model.Node;
import service.DatabaseService;
import java.util.Objects;

public class ITRequest extends Request {

    String description;

    public ITRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.description = "";
    }

    public ITRequest(int id, String notes, Node location, boolean completed, String description) {
        super(id, notes, location, completed);
        this.description = description;
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


    // overides abstract Request method - called in Request Facade
    @Override
    public void makeRequest () {
        ITRequest newITRequest = new ITRequest(-1, description, this.getLocation(), false);
        DatabaseService.getDatabaseService().insertITRequest(newITRequest);
    }

    // overides abstract Request method - called in Request Facade
    @Override
    public void fillRequest () {
        this.setCompleted(true);
        this.setCompletedBy(this.getCompletedBy());
        DatabaseService.getDatabaseService().updateITRequest((ITRequest)this);

    }

}



