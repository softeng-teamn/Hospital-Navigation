package service;

import model.MapNode;
import model.Node;
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

public class PathFindingServiceAccessibilityTest {
    // Map accessibility test
    //    1  -  2(stair)
    //    |     |
    //    |     3(end)
    //    |     |
    //    4  -  5
    //   elev

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

    public void createMap() {
        n1 = new Node("node1",0,0, "CONF");
        mn1 = new MapNode(0,0,n1);
        n2 = new Node("node2",1, 0, "STAI");
        mn2 = new MapNode(1,0,n2);
        n3 = new Node("node3",1, 1, "CONF");
        mn3 = new MapNode(1,1,n3);
        n4 = new Node("node4",0, 1, "ELEV");
        mn4 = new MapNode(0,1,n4);
        n5 = new Node("node5",1, 2, "CONF");
        mn5 = new MapNode(1,2,n5);
    }

    final PathFindingService mockPF = spy(new PathFindingService());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void mockingGetChildren() throws IOException {
        createMap();
        ArrayList<MapNode> list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn4);
        doReturn(list).when(mockPF).getChildren((mn1));
        list = new ArrayList<MapNode>();
        list.add(mn1);
        list.add(mn3);
        doReturn(list).when(mockPF).getChildren((mn2));
        list = new ArrayList<MapNode>();
        list.add(mn2);
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn3));
        list = new ArrayList<MapNode>();
        list.add(mn1);
        list.add(mn5);
        doReturn(list).when(mockPF).getChildren((mn4));
        list = new ArrayList<MapNode>();
        list.add(mn3);
        list.add(mn4);
        doReturn(list).when(mockPF).getChildren((mn5));
    }

    @Test
    @Category(FastTest.class)
    public void pathTester() throws IOException {
//        createMap();
        mockingGetChildren();
        ArrayList<Node> expected = new ArrayList<Node>();
        expected.add(0, n3);
        expected.add(0, n2);
        expected.add(0, n1);
        assertThat(mockPF.genPath(mn1, mn3, false, "astar"), is(expected));
        mockingGetChildren();
        expected = new ArrayList<Node>();
        expected.add(0, n3);
        expected.add(0, n5);
        expected.add(0, n4);
        expected.add(0, n1);
        assertThat(mockPF.genPath(mn1, mn3, true, "astar"), is(expected));
    }
}
