package controller;

import model.MapNode;
import model.Node;

public class NodeFacade {
    Node node;
    MapNode mapNode;

    public NodeFacade(Node node, MapNode mapNode) {
        this.node = node;
        this.mapNode = mapNode;
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

    public void NodeHashCode(){
        node.hashCode();
    }

    public void MapNodeHashCode(){
        mapNode.hashCode();
    }

}
