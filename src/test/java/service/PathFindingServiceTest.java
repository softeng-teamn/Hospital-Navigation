package service;

import controller.MapController;
import model.Node;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import testclassifications.FastTest;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathFindingServiceTest {

    Node n1 = new Node("n1", 0, 0, "f", "f", "f", "f", "f");
    Node n2 = new Node("n2", 1, 0, "f", "f", "f", "f", "f");
    Node n3 = new Node("n3", 1, 1, "f", "f", "f", "f", "f");
    Node n4 = new Node("n4", 2, 1, "f", "f", "f", "f", "f");
    Node n5 = new Node("n5", 3, 1, "f", "f", "f", "f", "f");
    Node n6 = new Node("n6", 4, 0, "f", "f", "f", "f", "f");

    @Before
    public void init() {

    }

    @Mock
    MapController mockMapController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void mockingConnectedTo() {
        ArrayList<Node> neighbors = new ArrayList<>();
//        neighbors.add();
        MapController mockMapController = mock(MapController.class);
//        when(mockMapController.getNodesConnectedTo(new Node(1,1)).thenReturn());
    }

    @Test
    @Category(FastTest.class)
    public void testingGetChildren() {

    }

}
