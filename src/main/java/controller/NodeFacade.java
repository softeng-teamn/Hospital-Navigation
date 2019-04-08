package controller;

import model.MapNode;
import model.Node;

public class NodeFacade {
    Node node;
    MapNode mapNode;

    public NodeFacade(MapNode mapNode) {
        this.mapNode = mapNode;
    }

    public NodeFacade(Node node){
        this.node = node;
    }

    public void MapNodeCalculateHeuristic(MapNode destination){
        mapNode.calculateHeuristic(destination);
    }

    public void MapNodeCalculateG(MapNode nextNode){
        mapNode.calculateG(nextNode);
    }

    public void MapNodeCompareTo(MapNode node){
        mapNode.compareTo(node);
    }

    public void NodeValidateType(String nodeType){
        node.validateType(nodeType);
    }

    public void NodeEquals(Object o){
        node.equals(o);
    }

    public void MapNodeEquals(Object o){
        mapNode.equals(o);
    }

}
