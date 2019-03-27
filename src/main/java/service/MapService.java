package service;

import model.BuildingMap;
import model.Node;
import model.Point;

public class MapService {

    private BuildingMap bMap;

    // find path from start to destination
    public Point findPath(Node start, Node dest) {
        Point myPath = new Point();
        return myPath;
    }

    // add a node to the map
    public boolean addNode(Node n) {
        return true;
    }

    // edit an existing node inside the map
    public boolean editNode(Node n) {
        return true;
    }

    // delete a node inside the map
    public boolean deleteNode(Node n) {
        return true;
    }

}
