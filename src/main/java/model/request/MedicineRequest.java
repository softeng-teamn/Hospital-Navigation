package model.request;

import model.Node;
import model.RequestType;

import static model.RequestType.RType.ITS;
import static model.RequestType.RType.MED;

public class MedicineRequest extends Request {

    String medicineType;
    double quantity;

    public MedicineRequest(String id, String notes, Node location, boolean completed, String medicineType) {
        super(id, notes, location, completed);
        this.requestType = new RequestType(MED);
        this.medicineType = medicineType;
        this.quantity = 0;
    }


    public MedicineRequest(String id, String notes, Node location, boolean completed, String medicineType, double quantity) {
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
}
