package edu.wpi.cs3733d19.teamN.map;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import edu.wpi.cs3733d19.teamN.testclassifications.FastTest;

import static org.junit.Assert.assertEquals;

public class MapNodeTest {

    MapNode mn1;
    MapNode mn2;
    MapNode mn3;

    @Before
    @Category(FastTest.class)
    public void createNodes() {
        mn1 = new MapNode(0, 0, new Node("n1", 0, 0));
        mn2 = new MapNode(6, 3, new Node("n2", 6, 3));
        mn3 = new MapNode(2, 1, new Node("n3", 2, 1));
    }

    @Test
    @Category(FastTest.class)
    public void checkHeuristic() {

        // testing heuristic
        NodeFacade nf = new NodeFacade(mn1);
        nf.mapNodeCalculateHeuristic(mn2);

        assertEquals(6.7, mn1.h, 0.1);
    }

    @Test
    @Category(FastTest.class)
    public void checkG() {
        // we found a new path that is faster
        mn2.g = 4;
        mn1.setParent(mn2, 5);

        assertEquals(9, mn1.g);
    }

    @Test
    @Category(FastTest.class)
    public void equalTest() {
        MapNode bob = new MapNode(0, 0, new Node("bob", 0, 0));
        MapNode tom = new MapNode(0, 0, new Node("tom", 0, 0));
        assertEquals(true, tom.equals(bob));
    }

    @Test
    @Category(FastTest.class)
    public void badEqualTest() {
        MapNode bob = new MapNode(1, 0, new Node("bob", 1, 0));
        MapNode tom = new MapNode(0, 0, new Node("tom", 0, 0));
        assertEquals(false, tom.equals(bob));
    }

    @Test
    @Category(FastTest.class)
    public void nullEqualTest() {
        MapNode bob = null;
        MapNode tom = new MapNode(0, 0, new Node("tom", 0, 0));
        assertEquals(false, tom.equals(bob));
    }


}
