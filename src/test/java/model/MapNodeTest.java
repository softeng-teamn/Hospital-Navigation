package model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapNodeTest {

    MapNode mn1;
    MapNode mn2;
    MapNode mn3;

    @Before
    public void createNodes() {
        mn1 = new MapNode(0, 0);
        mn2 = new MapNode(6, 3);
        mn3 = new MapNode(2, 1);
    }

    @Test
    public void checkHeuristic() {

        // testing heuristic
        mn1.calculateHeuristic(mn2);

        assertEquals(6.7, mn1.h, 0.1);
    }

    @Test
    public void checkG() {
        // we found a new path that is faster
        mn2.g = 4;
        mn1.checkBetter(mn2, 5);

        assertEquals(9, mn1.g);
    }





}
