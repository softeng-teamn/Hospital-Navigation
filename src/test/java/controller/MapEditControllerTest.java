package controller;


import model.Edge;
import model.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import service.DatabaseService;
import org.mockito.Mock;

import java.util.ArrayList;

public class MapEditControllerTest {

    private MapEditController mec = new MapEditController();

    @Before
    public void setup() {

    }


/*
    @Test
    @Category(FastTest.class)
    public void insertNodeTest(){
        Node testNode1 = new Node("ACONF00102", 1510, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node testNode2 = new Node("ACONF00105", 1580, 2538, "3", "BTM", "HALL", "Hall", "Hall");
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Edge testEdge1 = new Edge("ACONF00102-ACONF00103", testNode1,testNode2);
        Edge testEdge2 = new Edge("ACONF00102-ACONF00104", testNode1,testNode3);

        ArrayList<Edge> empty = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(testEdge1);
        edges.add(testEdge2);
        //edges is not empty
        assertThat(mec.insertNode(testNode1, empty), equalTo(false));
        //success
        assertThat(mec.insertNode(testNode2, edges), equalTo(true));
        //if it already exists
        assertThat(mec.insertNode(testNode2, edges), equalTo(false));

        mec.deleteNode(testNode1);
        mec.deleteNode(testNode2);
    }
    */



    @Test
    @Category(FastTest.class)
    public void updateNodeTests(){
        Node testNode1 = new Node("ACONF00102", 1510, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node testNode2 = new Node("ACONF00105", 1580, 2538, "3", "BTM", "HALL", "Hall", "Hall");
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Edge testEdge1 = new Edge("ACONF00102-ACONF00103", testNode1,testNode2);
        Edge testEdge2 = new Edge("ACONF00102-ACONF00104", testNode1,testNode3);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(testEdge1);
        edges.add(testEdge2);

        mec.insertNode(testNode1, edges);
        testNode1.setFloor("3");
        assertThat(mec.updateNode(testNode1), equalTo(true));
        //if nothing changes return true
        assertThat(mec.updateNode(testNode1), equalTo(true));
        // if doesnt exist
        assertThat(mec.updateNode(testNode2), equalTo(true)) ;
    }

    @Test
    @Category(FastTest.class)
    public void deleteNode(){
        Node testNode1 = new Node("ACONF00102", 1510, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node testNode2 = new Node("ACONF00105", 1580, 2538, "3", "BTM", "HALL", "Hall", "Hall");
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Edge testEdge1 = new Edge("ACONF00102-ACONF00103", testNode1,testNode2);
        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(testEdge1);


        mec.insertNode(testNode1, edges);
        // sucessful delete
        assertThat(mec.deleteNode(testNode1), equalTo(true)) ;
        // if node does not exist, still return true
        assertThat(mec.deleteNode(testNode3), equalTo(true)) ;
    }


}
