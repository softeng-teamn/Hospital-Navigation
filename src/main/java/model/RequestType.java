package model;



public class RequestType {
    public enum RType{
        ITS, MED, ABS
    }

    private RType rType;
    private int size;

    public int getSize() {
        return size;
    }

    public RequestType() {
        this.size = RType.values().length;
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
