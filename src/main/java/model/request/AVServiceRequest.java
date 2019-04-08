package model.request;

import model.Node;

import java.util.Objects;

public class AVServiceRequest extends Request {

    public enum AVServiceType{
        Audio,
        Visual,
        Other
    }

    AVServiceType avServiceType;

   public AVServiceRequest(int id, String notes, Node location, boolean completed, AVServiceType avServiceType){
        super(id,notes,location,completed);
        this.avServiceType = avServiceType;
    }

    public AVServiceType getAVServiceType(){return avServiceType;}

   public void setAVServiceType(AVServiceType avServiceType){this.avServiceType = avServiceType;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AVServiceRequest that = (AVServiceRequest)o;
        return avServiceType == that.avServiceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), avServiceType);
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
