package controller;


import model.Edge;
import model.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import service.DatabaseService;
import org.mockito.Mock;

import java.util.ArrayList;

public class MapEditControllerTest {

    private MapEditController mec = new MapEditController();
    @Mock private DatabaseService dbs;
    Node n;
    Node n1;
    ArrayList<Edge> edges = new ArrayList<>();


    @Before
    public void setup() {
        DatabaseService dbs = mock(DatabaseService.class);
         n = new Node(1, 1 , "","","","","","");
         n1 = new Node(0, 0 , "","","","","","");

        Edge e = new Edge( "edge", n , n1);

        when(dbs.insertNode(n)).thenReturn(true);
        when(dbs.insertNode(n1)).thenReturn(false);

        edges.add(e);
        when(dbs.insertEdge(edges.get(0))).thenReturn(true);
        when(dbs.insertNode(n1)).thenReturn(false);
        when(dbs.updateNode(n)).thenReturn(true).thenReturn(false);
        when(dbs.deleteNode(n)).thenReturn(true).thenReturn(false);

        MapEditController.dbs = dbs;
    }

    @Test
    @Category(FastTest.class)
    public void insertNodeTest(){
        ArrayList<Edge> empty = new ArrayList<>();
        //edges is not empty
        assertThat(mec.insertNode(n, empty), equalTo(false));
        //success
        assertThat(mec.insertNode(n, edges), equalTo(true));
        //if it already exists
        assertThat(mec.insertNode(n1, edges), equalTo(false));
    }

    @Test
    @Category(FastTest.class)
    public void updateNodeTests(){
        assertThat(mec.updateNode(n), equalTo(true));
        //unsure of what conditions make an edit return false, maybe if nothing changes?
        assertThat(mec.updateNode(n), equalTo(false));
        // if doesnt exist
        assertThat(mec.updateNode(n1), equalTo(false)) ;
    }

    @Test
    @Category(FastTest.class)
    public void deleteNode(){
        // sucessful delete
        assertThat(mec.deleteNode(n), equalTo(true)) ;
        // if node does not exist, fail
        assertThat(mec.deleteNode(n1), equalTo(false)) ;
    }

}
