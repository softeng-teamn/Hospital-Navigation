package model.request;

import model.Node;

import java.util.Date;
import java.util.Objects;

public class ExternalTransportRequest extends Request {

    public enum TransportationType{
        BUS,
        TAXI,
        CAR
    }

    private Date date;
    private TransportationType transportationType;

    public ExternalTransportRequest(int id, String notes, Node location, boolean completed, Date date, TransportationType transportationType) {
        super(id, notes, location, completed);
        this.date = date;
        this.transportationType = transportationType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TransportationType getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType transportationType) {
        this.transportationType = transportationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExternalTransportRequest that = (ExternalTransportRequest) o;
        return Objects.equals(date, that.date) &&
                transportationType == that.transportationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date, transportationType);
    }

    @Override
    public String toString() {
        return "ExternalTransportRequest{" +
                "date=" + date +
                ", transportationType=" + transportationType +
                '}';
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }
}
