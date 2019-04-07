package model.request;

import com.sun.xml.internal.bind.v2.TODO;
import model.Node;
import service.DatabaseService;

import javax.xml.crypto.Data;
import java.util.Objects;

public class InternalTransportRequest extends Request {

    public enum TransportType {
        Wheelchair,
        MotorScooter,
        Stretcher
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
        DatabaseService.getDatabaseService().insertInternalTransportRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateInternalTransportRequest(this);
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
