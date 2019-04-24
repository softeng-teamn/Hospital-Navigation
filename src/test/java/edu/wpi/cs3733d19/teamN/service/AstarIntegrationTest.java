package edu.wpi.cs3733d19.teamN.service;

import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.map.pathfinding.Astar;
import edu.wpi.cs3733d19.teamN.map.MapNode;
import edu.wpi.cs3733d19.teamN.map.Node;
import org.junit.Before;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

public class AstarIntegrationTest {

    final Astar astar = new Astar();
    final Node testNode = new Node("AHALL00202", 1590,2604,"2","BTM","HALL","Hall","Hall");
    final MapNode testMapNode = new MapNode(testNode.getXcoord(), testNode.getYcoord(), testNode);
    final Node testNodeChild1 = new Node("AHALL00302", 1590,2745,"2","BTM","HALL","Hall","Hall");
    final Node testNodeChild2 = new Node("ASTAI00102", 1650,2602,"2","BTM","STAI","Stairs Floor 2","Stairs Floor 2");
    final Node testNodeChild3 = new Node("AHALL00102", 1591,2560,"2","BTM","HALL","Hall","Hall");
    final MapNode testMapNodeChild1 = new MapNode(testNodeChild1.getXcoord(), testNodeChild1.getYcoord(), testNodeChild1);
    final MapNode testMapNodeChild2 = new MapNode(testNodeChild2.getXcoord(), testNodeChild2.getYcoord(), testNodeChild2);
    final MapNode testMapNodeChild3 = new MapNode(testNodeChild3.getXcoord(), testNodeChild3.getYcoord(), testNodeChild3);

    private DatabaseService myDBS;

    @Before
    public void setUp() throws IOException {

    }

//    @Test
//    @Category(FastTest.class)
//    public void testGetChildren(){
//        assertThat(astar.getChildren(testMapNode), containsInAnyOrder(testMapNodeChild1, testMapNodeChild2, testMapNodeChild3));
//    }

}
