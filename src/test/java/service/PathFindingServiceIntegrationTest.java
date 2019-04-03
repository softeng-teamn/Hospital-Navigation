package service;

import controller.CSVController;
import controller.Controller;
import model.MapNode;
import model.Node;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class PathFindingServiceIntegrationTest {

    final PathFindingService pathFindingService = new PathFindingService();
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
//        assertThat(pathFindingService.getChildren(testMapNode), containsInAnyOrder(testMapNodeChild1, testMapNodeChild2, testMapNodeChild3));
//    }

}
