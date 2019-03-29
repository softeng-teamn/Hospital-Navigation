package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.junit.Assert.*;

public class NodeTest {

    //private int xcoord, ycoord;
    //private String nodeID, floor, building, nodeType, longName, shortName;

    Node a = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    Node b = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    Node c = new Node (1, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;
    Node d = new Node (0, 0, "nodeID", "Floor3", "building", "hallway", "noName", "shortName") ;


    @Before
    public void setUp() throws Exception {
    }


    // not sure if this fcn is a different form of getter/setter, delete if so
    // test for validateID
    @Test
    @Category(FastTest.class)
    public void validateIDTest () {
        // same IDs
        assertTrue(a.validateID("nodeID")) ;
        // different IDs
        assertFalse(a.validateID("notTheRightNodeID")) ;
    }

    // not sure if this fcn is a different form of getter/setter, delete if so
    // test for validateFloorTest
    @Test
    @Category(FastTest.class)
    public void validateFloorTest () {
        // same
        assertTrue(a.validateFloor("Floor1")) ;
        // different
        assertFalse(a.validateFloor("Floor6")) ;
    }

    // test for validateType
    @Test
    @Category(FastTest.class)
    public void validateTypeTest () {
        // same
        assertTrue(a.validateType("hallway")) ;
        // different
        assertFalse(a.validateType("elevator")) ;

    }

    // test for equals
    @Test
    @Category(FastTest.class)
    public void equalsTest () {
        // same
        assertTrue(a.equals(b));
        // different by q field
        assertFalse(a.equals(c));
        // different by multiple fields
        assertFalse(a.equals(d));

    }


    @After
    public void tearDown() throws Exception {
    }

}
