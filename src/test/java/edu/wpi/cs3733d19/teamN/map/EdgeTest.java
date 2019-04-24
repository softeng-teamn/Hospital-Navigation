package edu.wpi.cs3733d19.teamN.map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import edu.wpi.cs3733d19.teamN.testclassifications.FastTest;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EdgeTest {

    private Node a = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    private Node b = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    private Node c = new Node (1, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    private Node d = new Node (0, 0, "nodeID", "Floor3", "building", "hallway", "noName", "shortName") ;

    private Edge edgeA = new Edge("edgeID",a, b);
    private Edge edgeB = new Edge("edgeID",a,b);
    private Edge edgeC = new Edge( "edgeID",b, c);

    @Test
    @Category(FastTest.class)
    public void equalsTest(){
        assertEquals(edgeA.equals(edgeB), true);
        assertEquals(edgeA.equals(edgeC), false);
    }

}
