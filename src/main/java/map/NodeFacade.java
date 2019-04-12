package map;

public class NodeFacade {
    Node node;
    MapNode mapNode;

    public NodeFacade(MapNode mapNode) {
        this.mapNode = mapNode;
    }

    public NodeFacade(Node node){
        this.node = node;
    }

    public void mapNodeCalculateHeuristic(MapNode destination){
        mapNode.calculateHeuristic(destination);
    }

    public void mapNodeCalculateG(MapNode nextNode){
        mapNode.calculateG(nextNode);
    }

    public void mapNodeCompareTo(MapNode node){
        mapNode.compareTo(node);
    }

    public void nodeValidateType(String nodeType){
        this.node.validateType(nodeType);
    }

    public void nodeValidateID(String ID){
        this.node.validateID(ID);
    }

    public void nodeValidateFloor(String floor){
        this.node.validateFloor(floor);
    }
}
