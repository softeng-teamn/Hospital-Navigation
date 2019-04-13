package service;

import map.pathfinding.Algorithm;
import map.pathfinding.AlgorithmContext;
import map.pathfinding.Astar;
import map.MapNode;
import map.Node;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import testclassifications.FastTest;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class AstarAdvancedTest {

    // MAP (Diaginal testing)
    //
    //                  10
    //                  |
    //                  9
    //                  |
    //  1   -   2   -   3   -   4
    //          |  \
    //          5     7
    //          |       \
    //          6          8


    // ----------------- WARNING -----------------
    // Running a test will set the parent of the mapNode
    // This must be reset before every test to ensure
    // there are no infinite loops when iterating through parents.
    //
    // To Be Safe:  Initialize the nodes before
    //              every test by running "mockingGetChildren()"

    Node n1;
    MapNode mn1;

    Node n2;
    MapNode mn2;

    Node n3;
    MapNode mn3;

    Node n4;
    MapNode mn4;

    Node n5;
    MapNode mn5;

    Node n6;
    MapNode mn6;

    Node n7;
    MapNode mn7;

    Node n8;
    MapNode mn8;

    Node n9;
    MapNode mn9;

    Node n10;
    MapNode mn10;

    public void createMap() {
        n1 = new Node("node1",0,2, "CONF");
        mn1 = new MapNode(0,2,n1);
        n2 = new Node("node2",1, 2, "CONF");
        mn2 = new MapNode(1,2,n2);
        n3 = new Node("node3",2, 2, "CONF");
        mn3 = new MapNode(2,2,n3);
        n4 = new Node("node4",3, 2, "REST");
        mn4 = new MapNode(3,2,n4);
        n5 = new Node("node5",1, 3, "CONF");
        mn5 = new MapNode(1,3,n5);
        n6 = new Node("node6",1, 4, "CONF");
        mn6 = new MapNode(1,4,n6);
        n7 = new Node("node7",2, 3, "CONF");
        mn7 = new MapNode(2,3,n7);
        n8 = new Node("node8",3, 4, "CONF");
        mn8 = new MapNode(3,4,n8);
        n9 = new Node("node9",2, 1, "CONF");
        mn9 = new MapNode(2,1,n9);
        n10 = new Node("node10",2, 0, "REST");
        mn10 = new MapNode(2,0,n10);
        n1.setFloor("1");
        n2.setFloor("1");
        n3.setFloor("1");
        n4.setFloor("1");
        n5.setFloor("1");
        n6.setFloor("1");
        n7.setFloor("1");
        n8.setFloor("1");
        n9.setFloor("1");
        n10.setFloor("1");
    }

    final AlgorithmContext mockPF = spy(new Astar());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Before
    public void mockingGetChildren() throws IOException {

        createMap();
        ArrayList<MapNode> list = new ArrayList<MapNode>();
        list.add(mn2);
        doReturn(list).when(mockPF).getChildren((mn1));
//        when(mockPF.getChildren(mn1)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn1);
        list.add(mn3);
        list.add(mn5);
        list.add(mn7);
        doReturn(list).when(mockPF).getChildren((mn2));
//        when(mockPF.getChildren(mn2)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn4);
        list.add(mn9);
        doReturn(list).when(mockPF).getChildren((mn3));
//        when(mockPF.getChildren(mn3)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn3);
        doReturn(list).when(mockPF).getChildren((mn4));
//        when(mockPF.getChildren(mn4)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn6);
        doReturn(list).when(mockPF).getChildren((mn5));
//        when(mockPF.getChildren(mn5)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn6));
//        when(mockPF.getChildren(mn6)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn8);
        doReturn(list).when(mockPF).getChildren((mn7));
//        when(mockPF.getChildren(mn7)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn7);
        doReturn(list).when(mockPF).getChildren((mn8));
//        when(mockPF.getChildren(mn8)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn3);
        list.add(mn10);
        doReturn(list).when(mockPF).getChildren((mn9));
//        when(mockPF.getChildren(mn9)).thenReturn(list);
        list = new ArrayList<MapNode>();
        list.add(mn9);
        doReturn(list).when(mockPF).getChildren((mn10));
//        when(mockPF.getChildren(mn10)).thenReturn(list);
    }



    @Test
    @Category(FastTest.class)
    public void testAStar() {
        // a path can be found
        assertThat(mockPF.returnMapNode(mn1, mn6, false, null), is(mn6));
        assertThat(mockPF.returnMapNode(mn10, mn8, false, null), is(mn8));
        assertThat(mockPF.returnMapNode(mn6, mn8, false, null), is(mn8));
        assertThat(mockPF.returnMapNode(mn2, mn10, false, null), is(mn10));
        assertThat(mockPF.returnMapNode(mn1, mn8, false, null), is(mn8));
    }

    @Test
    @Category(FastTest.class)
    public void pathTester() throws IOException {
//        createMap();
        mockingGetChildren();
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n8);
        expected.add(0, n7);
        expected.add(0, n2);
        expected.add(0, n3);
        expected.add(0, n9);
        expected.add(0, n10);
        assertThat(mockPF.findDest(mn10, mn8, false, "astar"), is(expected));
        mockingGetChildren();
        expected = new ArrayList<Node>();
        expected.add(0, n8);
        expected.add(0, n7);
        expected.add(0, n2);
        expected.add(0, n1);
        assertThat(mockPF.findDest(mn1, mn8, false, "astar"), is(expected));
    }

//    @Test
//    @Category(FastTest.class)
//    public void filteredBreadthTest() throws IOException {
//        mockingGetChildren();
//        ArrayList<Node> expected = new ArrayList<Node>();
//        expected.add(0, n4);
//        expected.add(0, n3);
//        expected.add(0, n2);
//        assertThat(mockPF.findDest(mn2, null, false, "REST"), is(expected));
//    }

}
