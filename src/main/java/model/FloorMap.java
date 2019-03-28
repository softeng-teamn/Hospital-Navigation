package model;

import model.Node;
import model.Point;
import service.DatabaseService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FloorMap {

    public HashMap<String, Point> getfMap() {
        return fMap;
    }

    public void setfMap(HashMap<String, Point> fMap) {
        this.fMap = fMap;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(int floorNum) {
        this.floorNum = floorNum;
    }

    private HashMap<String, Point> fMap;
    private int floorNum;

    public FloorMap(int floorNum){
        this.floorNum = floorNum;
        fMap = new HashMap<String, Point>();
        // Call DB for edges
        Collection<Edge> edges = DatabaseService.getEdges(1);
        for (Edge e: edges
        ) {
            Node n1 = e.getNode1();
            Node n2 = e.getNode2();

            double w = findWeight(n1.getXcoord(),n2.getXcoord(),n1.getYcoord(),n2.getYcoord());

            Point p1 = new Point(w, n1.getXcoord(), n1.getYcoord(), n1.getNodeID(), null);
            Point p2 = new Point(w, n2.getXcoord(), n2.getYcoord(), n2.getNodeID(), null);

            //if key already exists, append p2 to the end of
            if(fMap.containsKey(n1.getNodeID())){
                Point firstOcc = fMap.get(n1.getNodeID());
                p2.setNext(firstOcc);
            }
            //if key already exists, append p2 to the end of
            if(fMap.containsKey(n2.getNodeID())){
                Point firstOcc = fMap.get(n2.getNodeID());
                p1.setNext(firstOcc);
            }

            fMap.put(n1.getNodeID(), p2);
            fMap.put(n2.getNodeID(), p1);
        }
    }

    public double findWeight(int x1, int x2, int y1, int y2){
        return (Math.abs(x1-x2) + Math.abs(y1 - y2));
    }


}
