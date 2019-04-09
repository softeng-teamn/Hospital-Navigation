package model.request;

import model.Node;
import service.DatabaseService;

import java.util.Objects;

public class SecurityRequest extends Request {

    public enum Urgency {
        NOT, SOMEWHAT, VERY;
    }

    private Urgency urgency;

    public SecurityRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.urgency = Urgency.NOT;
    }

    public SecurityRequest(int id, String notes, Node location, boolean completed, Urgency urgency) {
        super(id, notes, location, completed);
        this.urgency = urgency;
    }

    @Override
    public void makeRequest() {
        SecurityRequest newSecurityRequest = new SecurityRequest(-1, this.getNotes(), this.getLocation(), false);
        DatabaseService.getDatabaseService().insertSecurityRequest(newSecurityRequest);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        this.setCompletedBy(this.getCompletedBy());
        DatabaseService.getDatabaseService().updateSecurityRequest((SecurityRequest) this);
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityRequest that = (SecurityRequest) o;
        return urgency == that.urgency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), urgency);
    }

    @Override
    public String toString() {
        return "SecurityRequest: " +
                "urgency=" + urgency;
    }
}
