package model;



public class RequestType {
    public enum RType{
        ITS, MED, ABS;
    }

    RType rType;

    public RequestType() {
    }

    public RequestType(RType rType) {
        this.rType = rType;
    }

    public RType getrType() {
        return rType;
    }

    public void setrType(RType rType) {
        this.rType = rType;
    }
}
