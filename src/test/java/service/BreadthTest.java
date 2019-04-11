package service;

import map.pathfinding.BreadthFS;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class BreadthTest {
    //         9 --- 8
    //         |     |
    //   6 --- 5     7
    //   |     |     |
    //   2 --- 1 --- 4
    //         |
    //         3
    // 1 is the start node and 9 is the end node


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

    public void createMap() {
        n1 = new Node("node1",0,2, "CONF");
        mn1 = new MapNode(0,2,n1);
        n2 = new Node("node2",1, 2, "CONF");
        mn2 = new MapNode(1,2,n2);
        n3 = new Node("node3",2, 2, "CONF");
        mn3 = new MapNode(2,2,n3);
        n4 = new Node("node4",3, 2, "CONF");
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
    }

    final BreadthFS mockPF = spy(new BreadthFS());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void mockingGetChildren() throws IOException {

        createMap();
        ArrayList<MapNode> list = new ArrayList<MapNode>();
        list.add(mn5);
        list.add(mn4);
        list.add(mn3);
        list.add(mn2);
        doReturn(list).when(mockPF).getChildren((mn1));
        list = new ArrayList<MapNode>();
        list.add(mn1);
        list.add(mn6);
        doReturn(list).when(mockPF).getChildren((mn2));
        list = new ArrayList<MapNode>();
        list.add(mn1);
        doReturn(list).when(mockPF).getChildren((mn3));
        list = new ArrayList<MapNode>();
        list.add(mn7);
        list.add(mn1);
        doReturn(list).when(mockPF).getChildren((mn4));
        list = new ArrayList<MapNode>();
        list.add(mn9);
        list.add(mn6);
        list.add(mn1);
        doReturn(list).when(mockPF).getChildren((mn5));
        list = new ArrayList<MapNode>();
        list.add(mn5);
        list.add(mn2);
        doReturn(list).when(mockPF).getChildren((mn6));
        list = new ArrayList<MapNode>();
        list.add(mn8);
        list.add(mn4);
        doReturn(list).when(mockPF).getChildren((mn7));
        list = new ArrayList<MapNode>();
        list.add(mn9);
        list.add(mn7);
        doReturn(list).when(mockPF).getChildren((mn8));
        list = new ArrayList<MapNode>();
        list.add(mn8);
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn9));
    }

    @Test
    @Category(FastTest.class)
    public void pathTester() throws IOException {
//        createMap();
        mockingGetChildren();
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n9);
        expected.add(0, n5);
        expected.add(0, n1);
        assertThat(mockPF.findDest(mn1, mn9, false, null), is(expected));
        mockingGetChildren();
        expected = new ArrayList<Node>();
        expected.add(0, n8);
        expected.add(0, n9);
        expected.add(0, n5);
        expected.add(0, n1);
        assertThat(mockPF.findDest(mn1, mn8, false, null), is(expected));
    }

}
