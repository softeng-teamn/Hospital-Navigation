package model.request;

import model.Node;

import java.util.Objects;

public class InternalTransportRequest extends Request {

    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Streatcher
    }

    private TransportType transport;

    public InternalTransportRequest(int id, String notes, Node location, boolean completed, TransportType transportType) {
        super(id, notes, location, completed);
        this.transport = transportType;
    }

    public InternalTransportRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.transport = TransportType.Wheelchair;
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }

    public TransportType getTransport() {
        return transport;
    }

    @Override
    public String toString() {
        return "InternalTransportRequest{" +
                "transport=" + transport +
                '}';
    }

    public void setTransport(TransportType transport) {
        this.transport = transport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InternalTransportRequest that = (InternalTransportRequest) o;
        return transport == that.transport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transport);
    }
}
