package model.request;

import model.Node;
import model.RequestType;

import java.util.Objects;

public class ToyRequest extends Request {

    String toyName;

    public ToyRequest(int id, String notes, Node location, boolean completed, String toyName) {
        super(id, notes, location, completed);
        this.toyName = toyName;
    }

    public ToyRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.toyName = "";
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), toyName);
    }
}
