package model;

public class ElevatorFloor {
    private String floor;
    private int eta;

    public ElevatorFloor(String floor, int eta) {
        this.floor = floor;
        this.eta = eta;
    }

    public String getFloor() {
        return floor;
    }

    public int getEta() {
        return eta;
    }
}
