package model.request;

import model.Node;
import model.RequestType;

import static model.RequestType.RType.ITS;
import static model.RequestType.RType.MED;

import java.util.Objects;

public class MedicineRequest extends Request {

    String medicineType;
    double quantity;

    public MedicineRequest(int id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.requestType = new RequestType(MED);
        this.medicineType = "";
        this.quantity = 0;
    }

    public MedicineRequest(int id, String notes, Node location, boolean completed, String medicineType, double quantity) {
        super(id, notes, location, completed);
        this.requestType = new RequestType(MED);
        this.quantity = quantity;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MedicineRequest that = (MedicineRequest) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                Objects.equals(medicineType, that.medicineType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), medicineType, quantity);
    }
}
