package model.request;

import model.Node;

public class MedicineRequest extends Request {

    String medicineType;
    double quantity;

    public MedicineRequest(String id, String notes, Node location, boolean completed) {
        super(id, notes, location, completed);
        this.medicineType = "";
        this.quantity = 0;
    }

    public MedicineRequest(String id, String notes, Node location, boolean completed, String medicineType, double quantity) {
        super(id, notes, location, completed);
        this.medicineType = medicineType;
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
