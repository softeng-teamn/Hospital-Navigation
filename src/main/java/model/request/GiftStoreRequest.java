package model.request;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Node;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Objects;

import static model.JobType.ADMINISTRATOR;
import static model.JobType.GIFT_SERVICES;

public class GiftStoreRequest extends Request {

    public enum GiftType {BALLOONS, TEDDY_BEAR, GIFT_BASKET}
    GiftType gType ;
    String patientName ;


    public GiftStoreRequest(int id, String notes, Node location, boolean completed, GiftType gType, String patientName) {
        super(id, notes, location, completed);
        this.gType = gType ;
        this.patientName = patientName ;
    }

    public GiftType getgType() {
        return gType;
    }

    public void setgType(GiftType gType) {
        this.gType = gType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftStoreRequest)) return false;
        if (!super.equals(o)) return false;
        GiftStoreRequest that = (GiftStoreRequest) o;
        return getgType() == that.getgType() &&
                Objects.equals(getPatientName(), that.getPatientName());
    }



    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getgType(), getPatientName());
    }


    @Override
    public String toString() {
        return "GiftStoreRequest{" +
                "gType=" + gType +
                ", patientName='" + patientName + '\'' +
                '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertGiftStoreRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateGiftStoreRequest(this);
    }

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Override
    public ObservableList<Employee> returnCorrectEmployee () {
        ObservableList<Employee> rightEmployee = FXCollections.observableArrayList();
        ObservableList<Employee> allEmployee = (ObservableList<Employee>) myDBS.getAllEmployees() ;

        for (int i = 0; i < allEmployee.size(); i++) {
            if (allEmployee.get(i).getJob() == GIFT_SERVICES || allEmployee.get(i).getJob() == ADMINISTRATOR) {
                rightEmployee.add(allEmployee.get(i)) ;
            }
        }
        return rightEmployee ;
    }

}
