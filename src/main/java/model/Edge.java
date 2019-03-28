package model;

public class Edge {

    private String edgeID;
    private Node node1, node2;

    public Edge(Node node1, Node node2) {
        this.edgeID = node1.getNodeID() + node2.getNodeID();
        this.node1 = node1;
        this.node2 = node2;
    }

    public String getEdgeID() {
        return edgeID;
    }

    public void setEdgeID(String edgeID) {
        this.edgeID = edgeID;
    }

    public Node getNode1() {
        return node1;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public Edge(String edgeID, Node node1, Node node2) {
        this.edgeID = edgeID;
        this.node1 = node1;
        this.node2 = node2;
    }
}
