package model.request;

import model.Node;
import service.DatabaseService;

import java.util.Objects;

public class ReligiousRequest extends Request{
    public enum Religion {
        CHRISTIAN,
        JEWISH,
        CATHOLIC,
        ISLAM
    }

    ReligiousRequest.Religion religion;

    public ReligiousRequest(int id, String notes, Node location, boolean completed, ReligiousRequest.Religion religion) {
        super(id, notes, location, completed);
        this.religion = religion;
    }


    public ReligiousRequest.Religion getReligion() {
        return religion;
    }

    public void setReligion(ReligiousRequest.Religion religion) {
        this.religion = religion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReligiousRequest that = (ReligiousRequest) o;
        return religion == that.religion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), religion);
    }

    @Override
    public String toString() {
        return "ReligiousRequest{" +
                "religion=" + religion +
                '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertReligiousRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateReligiousRequest(this);
    }
}
