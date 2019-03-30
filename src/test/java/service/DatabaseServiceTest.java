package service;

import model.Edge;
import model.Node;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class DatabaseServiceTest {
    private DatabaseService myDBS;

    @Before
    public void setUp() throws SQLException, MismatchedDatabaseVersionException {
        myDBS = DatabaseService.init("hospital-db-test");
    }

    @After
    public void tearDown() throws IOException {
        myDBS.close();
        FileUtils.deleteDirectory(new File("hospital-db-test"));
    }

    @Test
    @Category(FastTest.class)
    public void insertNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        // make sure that the new node is successfully inserted
        assertThat(myDBS.insertNode(testNode), is(true));
        // make sure that the same node cannot be inserted a second time
        assertThat(myDBS.insertNode(testNode), is(false));
    }

    @Test
    @Category(FastTest.class)
    public void getNode(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);
        Node toGet = myDBS.getNode("ACONF00102");
        assertThat(toGet.getNodeID(),is("ACONF00102"));
        assertThat(toGet.getXcoord(),is(1580));
        assertThat(toGet.getYcoord(),is(2538));
        assertThat(toGet.getFloor(),is("2"));
        assertThat(toGet.getBuilding(),is("BTM"));
        assertThat(toGet.getNodeType(),is("HALL"));
        assertThat(toGet.getShortName(),is("Hall"));
        assertThat(toGet.getLongName(),is("Hall"));
    }

    @Test
    @Category(FastTest.class)
    public void getNodeFail() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);
        assertThat(myDBS.getNode("NOTINFIELD"), is(nullValue()));
    }

    @Test
    @Category(FastTest.class)
    public void updateNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);

        Node toGet = myDBS.getNode("ACONF00102");
        assertThat(toGet.getNodeID(),is("ACONF00102"));
        assertThat(toGet.getXcoord(),is(1580));
        assertThat(toGet.getYcoord(),is(2538));
        assertThat(toGet.getFloor(),is("2"));
        assertThat(toGet.getBuilding(),is("BTM"));
        assertThat(toGet.getNodeType(),is("HALL"));
        assertThat(toGet.getShortName(),is("Hall"));
        assertThat(toGet.getLongName(),is("Hall"));


        testNode = new Node("ACONF00102", 1582, 2540, "3", "BTM", "CONF", "Halla", "Halls");
        myDBS.updateNode(testNode);

        toGet = myDBS.getNode("ACONF00102");
        assertThat(toGet.getNodeID(),is("ACONF00102"));
        assertThat(toGet.getXcoord(),is(1582));
        assertThat(toGet.getYcoord(),is(2540));
        assertThat(toGet.getFloor(),is("3"));
        assertThat(toGet.getBuilding(),is("BTM"));
        assertThat(toGet.getNodeType(),is("CONF"));
        assertThat(toGet.getShortName(),is("Halls"));
        assertThat(toGet.getLongName(),is("Halla"));
    }

    @Test
    @Category(FastTest.class)
    public void deleteNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);
        // make sure it can be got
        assertThat(myDBS.getNode("ACONF00102").getNodeID(), is("ACONF00102"));
        // delete the node from the database successfully
        assertThat(myDBS.deleteNode(testNode),is(true));
        //make sure that it is not in the database
        assertThat((myDBS.getNode("ACONF00102")), is(nullValue()));
        //delete is like update so trying to delete a record that isn't there doesn't cause problems. No case needed for that.
    }

    @Test
    @Category(FastTest.class)
    public void getAllNodes() {
        // insert nodes
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);
        testNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDBS.insertNode(testNode);
        ArrayList<Node> allNodes = myDBS.getAllNodes();
        assertThat(allNodes.size(),is(2));
        assertThat(allNodes.get(0).getNodeID(),is("ACONF00102"));
        assertThat(allNodes.get(1).getNodeID(),is("ACONF00103"));

        testNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDBS.insertNode(testNode);
        allNodes = myDBS.getAllNodes();
        assertThat(allNodes.size(),is(3));
        assertThat(allNodes.get(0).getNodeID(),is("ACONF00102"));
        assertThat(allNodes.get(1).getNodeID(),is("ACONF00103"));
        assertThat(allNodes.get(2).getNodeID(),is("ACONF00104"));
    }

    @Test
    public void getNodes() {

    }

    @Test
    @Category(FastTest.class)
    public void getEdge() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        myDBS.insertNode(testNode);
        myDBS.insertNode(otherNode);
        myDBS.insertEdge(newEdge);
        Edge gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newEdge.getNode1().getNodeID()));
        assertThat(gotEdge.getNode2().getNodeID(), is(newEdge.getNode2().getNodeID()));

    }

    @Test
    @Category(FastTest.class)
    public void insertEdge(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        assertFalse(myDBS.insertEdge(newEdge));
        myDBS.insertNode(testNode);
        assertFalse(myDBS.insertEdge(newEdge));
        myDBS.insertNode(otherNode);
        assertTrue(myDBS.insertEdge(newEdge));
    }

    @Test
    @Category(FastTest.class)
    public void updateEdge(){
        // set up the DB
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node anotherNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        myDBS.insertNode(testNode);
        myDBS.insertNode(otherNode);
        myDBS.insertEdge(newEdge);
        myDBS.insertNode(anotherNode);
        // get the edge and confirm its initial values
        Edge gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newEdge.getNode1().getNodeID()));
        assertThat(gotEdge.getNode2().getNodeID(), is(newEdge.getNode2().getNodeID()));
        Edge newerEdge = new Edge("ACONF00102-ACONF00104", testNode, anotherNode);
        // update the values and confirm that they were changed
        assertTrue(myDBS.updateEdge(newerEdge));
        gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge,is(notNullValue()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newerEdge.getNode1().getNodeID()));



    }

    @Test
    @Category(FastTest.class)
    public void deleteEdge(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        myDBS.insertNode(testNode);
        myDBS.insertNode(otherNode);
        myDBS.insertEdge(newEdge);
        myDBS.insertNode(testNode);
        Edge gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        // delete it
        myDBS.deleteEdge(gotEdge);
        //make sure that it's not there
        assertThat((myDBS.getEdge("ACONF00102-ACONF00103")), is(nullValue()));


    }


    // uh i legit don't know how to test this because everything relies on it and we can't delete
    // the tables yet
    @Test
    public void createTables() {
    }

    @Test
    @Category(FastTest.class)
    public void tableExists() {
        assertTrue(myDBS.tableExists("NODE"));
        assertFalse(myDBS.tableExists("NOTPRESENT"));


    }
}