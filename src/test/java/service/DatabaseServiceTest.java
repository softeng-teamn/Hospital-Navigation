package service;

import model.Edge;
import model.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class DatabaseServiceTest {
    DatabaseService myDB;

    @Before
    public void setUp(){

        try {
            myDB = DatabaseService.init("testerDB");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        myDB.wipeTables();
        myDB.close();
    }


    @Test
    @Category(FastTest.class)
    public void insertNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        // make sure that the new node is successfully inserted
        assertThat(myDB.insertNode(testNode), is(true));
        // make sure that the same node cannot be inserted a second time
        assertThat(myDB.insertNode(testNode), is(false));
    }

    @Test
    @Category(FastTest.class)
    public void getNode(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDB.insertNode(testNode);
        Node toGet = myDB.getNode("ACONF00102");
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
        myDB.insertNode(testNode);
        assertThat(myDB.getNode("NOTINFIELD"), is(nullValue()));
    }

    @Test
    @Category(FastTest.class)
    public void updateNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDB.insertNode(testNode);

        Node toGet = myDB.getNode("ACONF00102");
        assertThat(toGet.getNodeID(),is("ACONF00102"));
        assertThat(toGet.getXcoord(),is(1580));
        assertThat(toGet.getYcoord(),is(2538));
        assertThat(toGet.getFloor(),is("2"));
        assertThat(toGet.getBuilding(),is("BTM"));
        assertThat(toGet.getNodeType(),is("HALL"));
        assertThat(toGet.getShortName(),is("Hall"));
        assertThat(toGet.getLongName(),is("Hall"));


        testNode = new Node("ACONF00102", 1582, 2540, "3", "BTM", "CONF", "Halla", "Halls");
        myDB.updateNode(testNode);

        toGet = myDB.getNode("ACONF00102");
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
        myDB.insertNode(testNode);
        // make sure it can be got
        assertThat(myDB.getNode("ACONF00102").getNodeID(), is("ACONF00102"));
        // delete the node from the database successfully
        assertThat(myDB.deleteNode(testNode),is(true));
        //make sure that it is not in the database
        assertThat((myDB.getNode("ACONF00102")), is(nullValue()));
        //delete is like update so trying to delete a record that isn't there doesn't cause problems. No case needed for that.
    }

    @Test
    @Category(FastTest.class)
    public void getAllNodes() {
        // insert nodes
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDB.insertNode(testNode);
        testNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDB.insertNode(testNode);
        ArrayList<Node> allNodes = myDB.getAllNodes();
        assertThat(allNodes.size(),is(2));
        assertThat(allNodes.get(0).getNodeID(),is("ACONF00102"));
        assertThat(allNodes.get(1).getNodeID(),is("ACONF00103"));

        testNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDB.insertNode(testNode);
        allNodes = myDB.getAllNodes();
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
        myDB.insertNode(testNode);
        myDB.insertNode(otherNode);
        myDB.insertEdge(newEdge);
        Edge gotEdge = myDB.getEdge("ACONF00102-ACONF00103");
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
        assertFalse(myDB.insertEdge(newEdge));
        myDB.insertNode(testNode);
        assertFalse(myDB.insertEdge(newEdge));
        myDB.insertNode(otherNode);
        assertTrue(myDB.insertEdge(newEdge));

    }

    @Test
    @Category(FastTest.class)
    public void updateEdge(){
        // set up the DB
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node anotherNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        myDB.insertNode(testNode);
        myDB.insertNode(otherNode);
        myDB.insertEdge(newEdge);
        myDB.insertNode(anotherNode);
        // get the edge and confirm its initial values
        Edge gotEdge = myDB.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newEdge.getNode1().getNodeID()));
        assertThat(gotEdge.getNode2().getNodeID(), is(newEdge.getNode2().getNodeID()));
        Edge newerEdge = new Edge("ACONF00102-ACONF00104", testNode, anotherNode);
        // update the values and confirm that they were changed
        assertTrue(myDB.updateEdge(newerEdge));
        gotEdge = myDB.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge,is(notNullValue()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newerEdge.getNode1().getNodeID()));



    }

    @Test
    public void deleteEdge(){

    }


    // uh i legit don't know how to test this because everything relies on it and we can't delete
    // the tables yet
    @Test
    public void createTables() {
    }

    @Test
    @Category(FastTest.class)
    public void tableExists() {
        assertTrue(myDB.tableExists("NODE"));
        assertFalse(myDB.tableExists("NOTPRESENT"));


    }
}