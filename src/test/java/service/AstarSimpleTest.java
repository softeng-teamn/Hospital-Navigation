package service;

import service.CSVService;
import controller.MapController;
import model.MapNode;
import model.Node;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import testclassifications.FastTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AstarSimpleTest {

    final Node n1 = new Node("n1", 0, 0, "1", "f", "f", "f", "f");
    final MapNode mn1 = new MapNode(0,0, n1);
    final Node n2 = new Node("n2", 1, 0, "1", "f", "f", "f", "f");
    final MapNode mn2 = new MapNode(1,0,n2);
    final Node n3 = new Node("n3", 1, 1, "1", "f", "f", "f", "f");
    final MapNode mn3 = new MapNode(1,1, n3);
    final Node n4 = new Node("n4", 2, 1, "1", "f", "f", "f", "f");
    final MapNode mn4 = new MapNode(2, 1, n4);
    final Node n5 = new Node("n5", 3, 1, "1", "f", "f", "f", "f");
    final MapNode mn5 = new MapNode(3, 1, n5);
    final Node n6 = new Node("n6", 4, 0, "1", "f", "f", "f", "f");
    final MapNode mn6 = new MapNode(4, 0, n6);
    final Astar mockPF = spy(new Astar());
    @Mock
    MapController mockMapController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

//    @Test
//    @Category(FastTest.class)
//    public void sample() {
//
//    }

    // generates the mock scenario
    //
    //  1 - 2 - - - 6
    //      |       |
    //      3 - 4 - 5
    //
    @Before
    public void mockingGetChildren() {
        ArrayList<MapNode> list = new ArrayList<MapNode>();
        list.add(mn2);
        doReturn(list).when(mockPF).getChildren((mn1));
//        when(mockPF.getChildren(mn1)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn1);
        list.add(mn6);
        list.add(mn3);
        doReturn(list).when(mockPF).getChildren((mn2));
        //when(mockPF.getChildren(mn2)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn4);
        doReturn(list).when(mockPF).getChildren((mn3));
        //when(mockPF.getChildren(mn3)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn3);
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn4));
        //when(mockPF.getChildren(mn4)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn4);
        list.add(mn6);
        doReturn(list).when(mockPF).getChildren((mn5));
        //when(mockPF.getChildren(mn5)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn6));
        //when(mockPF.getChildren(mn6)).thenReturn(list);
    }

    // make sure I built the scenario correctly
    @Test
    @Category(FastTest.class)
    public void testMocking() throws IOException {
        ArrayList<MapNode> expected = new ArrayList<MapNode>();
        expected.add(mn1);
        expected.add(mn6);
        expected.add(mn3);
        assertThat(mockPF.getChildren(mn2), is(expected));
        expected = new ArrayList<MapNode>();
        expected.add(mn2);
        expected.add(mn5);
        assertThat(mockPF.getChildren(mn6), is(expected));
        expected = new ArrayList<MapNode>();
        expected.add(mn2);
        assertThat(mockPF.getChildren(mn1), is(expected));
    }

    @Test
    @Category(FastTest.class)
    public void testAStar() {
        // a path can be found
        assertThat(mockPF.aStar(mn1, mn6, false, null), is(mn6));
    }

    @Test
    @Category(FastTest.class)
    public void testPathBackTracking() {
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n6);
        expected.add(0, n2);
        expected.add(0, n1);
        assertThat(mockPF.findDest(mn1, mn6, false, "astar"), is(expected));
    }

    @Test
    @Category(FastTest.class)
    public void testPathBackTracking2() {
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n6);
        expected.add(0, n2);
        expected.add(0, n3);
        assertThat(mockPF.findDest(mn3, mn6, false, "astar"), is(expected));
    }

    @Test
    @Category(FastTest.class)
    public void testPathBackTracking3() {
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n1);
        expected.add(0, n2);
        expected.add(0, n3);
        expected.add(0, n4);
        assertThat(mockPF.findDest(mn4, mn1, false, "astar"), is(expected));
    }

}
