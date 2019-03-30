package service;

import model.BuildingMap;
import model.FloorMap;
import model.Node;
import model.Point;

public class MapService {

    private BuildingMap bMap;

    // find path from start to destination
    public Point findPath(Node start, Node dest) {
        String floorNum = start.getBuilding();
        FloorMap fm = bMap.getMap(Integer.parseInt(floorNum));

        Point closed;
        Node open = start;


        FloorMap cameFrom = new FloorMap(Integer.parseInt(floorNum));





        Point startReachable = fm.getReachable(start.getNodeID());




        Point myPath = new Point(1,1,1, "someID", null);
        return myPath;
    }

    //Set of visited nodes
    //

    public BuildingMap getbMap() {
        return bMap;
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
