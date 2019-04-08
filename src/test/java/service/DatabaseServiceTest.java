package service;

import model.*;
import model.request.*;
import org.apache.commons.io.FileUtils;
import org.apache.derby.iapi.db.Database;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseServiceTest {
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @Before
    public void setUp() {
        myDBS.wipeTables();
    }

    @After
    public void tearDown() {
        myDBS.wipeTables();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
    }

    @Test
    @Category(FastTest.class)
    public void insertNode() {
        final Function callback = mock(Function.class);
        myDBS.registerNodeCallback(callback);
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        // make sure that the new node is successfully inserted
        assertThat(myDBS.insertNode(testNode), is(true));
        verify(callback, times(1)).apply(null);

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
        final Function callback = mock(Function.class);
        myDBS.registerNodeCallback(callback);

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
        verify(callback, times(2)).apply(null); // Cumulative

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
        final Function callback = mock(Function.class);
        myDBS.registerNodeCallback(callback);

        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode);
        // make sure it can be got
        assertThat(myDBS.getNode("ACONF00102").getNodeID(), is("ACONF00102"));
        // delete the node from the database successfully
        assertThat(myDBS.deleteNode(testNode),is(true));
        verify(callback, times(2)).apply(null);

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
    @Category(FastTest.class)
    public void insertAllNodes() {
        final Function callback = mock(Function.class);
        myDBS.registerNodeCallback(callback);

        assertThat(myDBS.getAllNodes().size(), is(0));
        ArrayList<Node> nodes = new ArrayList<>();

        for (int i = 0; i < 10001; i ++) {
            nodes.add(new Node("" + i, i, i, "2", "BTM", "HALL", "Hall", "Hall"));
        }

        assertTrue(myDBS.insertAllNodes(nodes));

        verify(callback, times(11)).apply(null);

        assertThat(myDBS.getAllNodes().size(), is(10001));
    }

    @Test
    @Category(FastTest.class)
    public void getNumNodeTypeByFloor() {
        Node testNode1 = new Node("XCONF00101", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node testNode2 = new Node("XCONF00102", 1648, 2968, "2", "BTM", "STAI", "BTM Conference Center", "BTM Conference");
        Node testNode3 = new Node("XCONF00103", 1648, 2968, "2", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node testNode4 = new Node("XCONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");

        Node testNode5 = new Node("XCONF00105", 1648, 2968, "1", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node testNode6 = new Node("XCONF00106", 1648, 2968, "1", "BTM", "DEPT", "BTM Conference Center", "BTM Conference");
        Node testNode7 = new Node("XCONF00107", 1580, 2538, "1", "BTM", "LABS", "Hall", "Hall");
        Node testNode8 = new Node("XCONF00108", 1648, 2968, "1", "BTM", "LABS", "BTM Conference Center", "BTM Conference");
        Node testNode9 = new Node("XCONF00109", 1648, 2968, "1", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        assertTrue(myDBS.insertNode(testNode1));
        assertTrue(myDBS.insertNode(testNode2));
        assertTrue(myDBS.insertNode(testNode3));
        assertTrue(myDBS.insertNode(testNode4));
        assertTrue(myDBS.insertNode(testNode5));
        assertTrue(myDBS.insertNode(testNode6));
        assertTrue(myDBS.insertNode(testNode7));
        assertTrue(myDBS.insertNode(testNode8));
        assertTrue(myDBS.insertNode(testNode9));

        assertEquals(2, myDBS.getNumNodeTypeByFloor("HALL","2"));
        assertEquals(1, myDBS.getNumNodeTypeByFloor("STAI","2"));
        assertEquals(0, myDBS.getNumNodeTypeByFloor("HALL","1"));
        assertEquals(2, myDBS.getNumNodeTypeByFloor("LABS","1"));
        assertEquals(1, myDBS.getNumNodeTypeByFloor("DEPT","1"));
    }

    @Test
    @Category(FastTest.class)
    public void getNodesByFloor() {
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDBS.insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        myDBS.insertNode(testNode3);
        ArrayList<Node> getByFloor = myDBS.getNodesByFloor("3");
        assertThat(getByFloor.size(), is(2));
        assertEquals(getByFloor.get(0),testNode2);
        assertEquals(getByFloor.get(1),testNode3);
    }

    @Test
    @Category(FastTest.class)
    public void getNodesFilteredByType() {
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "STAI", "Hall", "Hall");
        myDBS.insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "CONF", "Hall", "Hall");
        myDBS.insertNode(testNode3);
        Node testNode4 = new Node("ACONF00105", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode4);
        Node testNode5 = new Node("ACONF00106", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode5);

        Node[] nodes0 = {testNode2, testNode3};
        assertThat(myDBS.getNodesFilteredByType("HALL"), containsInAnyOrder(nodes0));

        Node[] nodes1 = {testNode1, testNode2, testNode4, testNode5};
        assertThat(myDBS.getNodesFilteredByType("CONF"), containsInAnyOrder(nodes1));

        Node[] nodes2 = {testNode3};
        assertThat(myDBS.getNodesFilteredByType("HALL", "STAI"), containsInAnyOrder(nodes2));
    }

    @Test
    @Category(FastTest.class)
    public void getNodesConnectedTo(){
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode3);
        Node testNode4 = new Node("ACONF00105", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode4);
        Node testNode5 = new Node("ACONF00106", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        myDBS.insertNode(testNode5);
        Edge testEdge1 = new Edge("ACONF00102-ACONF00103", testNode1,testNode2);
        myDBS.insertEdge(testEdge1);
        Edge testEdge2 = new Edge("ACONF00102-ACONF00104", testNode1,testNode3);
        myDBS.insertEdge(testEdge2);
        Edge testEdge3 = new Edge("ACONF00105-ACONF00102", testNode4,testNode1);
        myDBS.insertEdge(testEdge3);
        Edge testEdge4 = new Edge("ACONF00105-ACONF00106", testNode4,testNode5);
        myDBS.insertEdge(testEdge4);
        Edge testEdge5 = new Edge("ACONF00106-ACONF00102", testNode5,testNode1);
        myDBS.insertEdge(testEdge5);
        ArrayList<Node> connectedNodes = myDBS.getNodesConnectedTo(testNode1);
        assertThat(connectedNodes.get(0).getNodeID(), is(testNode2.getNodeID()));
        assertThat(connectedNodes.get(1).getNodeID(), is(testNode3.getNodeID()));
        assertThat(connectedNodes.get(2).getNodeID(), is(testNode4.getNodeID()));

        assertTrue(connectedNodes.get(0).equals(testNode2));
        assertTrue(connectedNodes.get(1).equals(testNode3));
        assertTrue(connectedNodes.get(2).equals(testNode4));
        assertTrue(connectedNodes.get(3).equals(testNode5));
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
        final Function callback = mock(Function.class);
        myDBS.registerEdgeCallback(callback);

        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        assertFalse(myDBS.insertEdge(newEdge));
        verify(callback, times(0)).apply(null);

        myDBS.insertNode(testNode);
        assertFalse(myDBS.insertEdge(newEdge));
        verify(callback, times(0)).apply(null);

        myDBS.insertNode(otherNode);
        assertTrue(myDBS.insertEdge(newEdge));
        verify(callback, times(1)).apply(null);
    }

    @Test
    @Category(FastTest.class)
    public void updateEdge(){
        final Function callback = mock(Function.class);
        myDBS.registerEdgeCallback(callback);
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
        Edge newerEdge = new Edge("ACONF00102-ACONF00103", testNode, anotherNode);
        // update the values and confirm that they were changed
        assertTrue(myDBS.updateEdge(newerEdge));
        gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge,is(notNullValue()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newerEdge.getNode1().getNodeID()));

        verify(callback, times(2)).apply(null);
    }

    @Test
    @Category(FastTest.class)
    public void deleteEdge(){
        final Function callback = mock(Function.class);
        myDBS.registerEdgeCallback(callback);
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        myDBS.insertNode(testNode);
        myDBS.insertNode(otherNode);
        myDBS.insertEdge(newEdge);
        Edge gotEdge = myDBS.getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        // delete it
        myDBS.deleteEdge(gotEdge);
        verify(callback, times(2)).apply(null);

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

    @Test
    @Category(FastTest.class)
    public void getAllEdges() {
        Node n1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node n2 = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node n3 = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge e1 = new Edge("ACONF00102-ACONF00103", n1, n2);
        Edge e2 = new Edge("ACONF00102-ACONF00104", n1, n3);
        Edge e3 = new Edge("ACONF00103-ACONF00104", n2, n3);
        assertTrue(myDBS.insertNode(n1));
        assertTrue(myDBS.insertNode(n2));
        assertTrue(myDBS.insertNode(n3));

        assertTrue(myDBS.insertEdge(e1));

        assertThat(myDBS.getAllEdges(), Matchers.contains(e1));

        assertTrue(myDBS.insertEdge(e2));
        assertTrue(myDBS.insertEdge(e3));

        assertThat(myDBS.getAllEdges(), Matchers.contains(e1, e2, e3));
    }

    @Test
    @Category(FastTest.class)
    // Test both inserting and getting a reservation
    public void insertAndGetReservation() {
        // Assume an empty DB (ensured by setUp())

        Reservation value, expected;

        // First verify that these reservations are null
        value = myDBS.getReservation(1);
        assertThat(value, is(nullValue()));
        Employee testEmployee = new Employee(23,"JJohnson",JobType.DOCTOR,false,"douglas");

        // Create a reservation
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation1 = new Reservation(1, 0, 23, "Event 0", "None", reservationStart, reservationEnd);

        // successful insert because of constraints
        assertFalse(myDBS.insertReservation(reservation1)); // No matching employee yet
        assertTrue(myDBS.insertEmployee(testEmployee));
        assertTrue(myDBS.insertReservation(reservation1));

        // Verify successful get
        expected = reservation1;
        value = myDBS.getReservation(1); // Expect 1 because of failed insert

        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllReservations() {
        long now = (new Date()).getTime();
        List<Reservation> reservationList;

        // No reservations should exist yet
        reservationList = myDBS.getAllReservations();
        assertThat(reservationList.size(), is(0));

        Employee testEmployee = new Employee(23,"CatPlanet",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee));

        // Create some reservations
        GregorianCalendar res1Start = new GregorianCalendar();
        GregorianCalendar res1End = new GregorianCalendar();
        GregorianCalendar res2Start = new GregorianCalendar();
        GregorianCalendar res2End = new GregorianCalendar();
        GregorianCalendar res3Start = new GregorianCalendar();
        GregorianCalendar res3End = new GregorianCalendar();
        res1Start.setTime(new Date(now - 5000));
        res1End.setTime(new Date(now + 100));
        res2Start.setTime(new Date(now - 420000));
        res2End.setTime(new Date(now + 110000));
        res3Start.setTime(new Date(now));
        res3End.setTime(new Date(now + 1000));
        Reservation res0 = new Reservation(0, 1, 23, "Event 0", "ABCD", res1Start, res1End);
        Reservation res1 = new Reservation(1, 0, 23, "Event 1", "XYZ", res2Start, res2End);
        Reservation res2 = new Reservation(2, 2, 23, "Event 2", "LMNO", res3Start, res3End);

        // Insert two
        assertTrue(myDBS.insertReservation(res0));
        assertTrue(myDBS.insertReservation(res1));

        // Check that there are two and only two, and that they are the right two
        reservationList = myDBS.getAllReservations();
        assertThat(reservationList.size(), is(2));
        assertEquals(res0, reservationList.get(0));
        assertEquals(res1, reservationList.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertReservation(res2));

        reservationList = myDBS.getAllReservations();
        assertThat(reservationList.size(), is(3));
        assertEquals(res0, reservationList.get(0));
        assertEquals(res1, reservationList.get(1));
        assertEquals(res2, reservationList.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateReservation() {
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation = new Reservation(0, 0, 23, "Event 0", "None", reservationStart, reservationEnd);


        Employee testEmployee = new Employee(23,"CatPlanet", JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee));

        assertTrue(myDBS.insertReservation(reservation));
        assertEquals(reservation, myDBS.getReservation(0));

        reservation.setPrivacyLevel(1);
        reservation.setLocationID("ABCD");
        reservationEnd.add(Calendar.MINUTE, 30);
        reservation.setEndTime(reservationEnd);

        assertTrue(myDBS.updateReservation(reservation));
        assertEquals(reservation, myDBS.getReservation(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteReservation() {
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation = new Reservation(0, 0, 23, "Event 0", "None", reservationStart, reservationEnd);

        Employee testEmployee = new Employee(23,"CatPlanet",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee));

        assertTrue(myDBS.insertReservation(reservation));
        assertEquals(reservation, myDBS.getReservation(0));

        assertTrue(myDBS.deleteReservation(reservation));
        assertNull(myDBS.getReservation(0));
    }

    @Test
    @Category(FastTest.class)
    public void getReservationsBySpaceId() {
        long now = (new Date()).getTime();

        List<Reservation> reservationList;

        // No reservations should exist yet
        reservationList = myDBS.getAllReservations();
        assertThat(reservationList.size(), is(0));

        // Create some reservations
        GregorianCalendar res1Start = new GregorianCalendar();
        GregorianCalendar res1End = new GregorianCalendar();
        GregorianCalendar res2Start = new GregorianCalendar();
        GregorianCalendar res2End = new GregorianCalendar();
        GregorianCalendar res3Start = new GregorianCalendar();
        GregorianCalendar res3End = new GregorianCalendar();
        res1Start.setTime(new Date(now - 5000));
        res1End.setTime(new Date(now + 100));
        res2Start.setTime(new Date(now - 420000));
        res2End.setTime(new Date(now + 110000));
        res3Start.setTime(new Date(now));
        res3End.setTime(new Date(now + 1000));
        Reservation res0 = new Reservation(0, 1, 23, "Event 0", "ABCD", res1Start, res1End);
        Reservation res1 = new Reservation(1, 0, 43, "Event 1", "XYZ", res2Start, res2End);
        Reservation res2 = new Reservation(2, 2, 12, "Event 2", "ABCD", res3Start, res3End);


        Employee testEmployee1 = new Employee(23,"CatPlanet",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee1));

        Employee testEmployee2 = new Employee(43,"CatPlanet1",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee2));

        Employee testEmployee3 = new Employee(12,"CatPlanet2",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee3));

        // Insert two
        assertTrue(myDBS.insertReservation(res0));
        assertTrue(myDBS.insertReservation(res1));

        // Check that only the res with the ABCD location is retrieved
        reservationList = myDBS.getReservationsBySpaceId("ABCD");
        assertThat(reservationList.size(), is(1));
        assertEquals(res0, reservationList.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertReservation(res2));

        reservationList = myDBS.getReservationsBySpaceId("ABCD");
        assertThat(reservationList.size(), is(2));
        assertEquals(res0, reservationList.get(0));
        assertEquals(res2, reservationList.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void getReservationBySpaceIdBetween() {
        long now = 100000000000l;

        List<Reservation> reservationList;

        // No reservations should exist yet
        reservationList = myDBS.getAllReservations();
        assertThat(reservationList.size(), is(0));


        // Create some reservations
        GregorianCalendar res1Start = new GregorianCalendar();
        GregorianCalendar res1End = new GregorianCalendar();
        GregorianCalendar res2Start = new GregorianCalendar();
        GregorianCalendar res2End = new GregorianCalendar();
        res1Start.setTime(new Date(now - 5000));
        res1End.setTime(new Date(now + 100));
        res2Start.setTime(new Date(now - 420000));
        res2End.setTime(new Date(now + 110000));
        Reservation res0 = new Reservation(0, 1, 23, "Event 0", "ABCD", res1Start, res1End);
        Reservation res1 = new Reservation(1, 0, 43, "Event 1", "ABCD", res2Start, res2End);
        Reservation res2 = new Reservation(2, 0, 43, "Event 1", "LMNO", res2Start, res2End);


        Employee testEmployee1 = new Employee(23,"CatPlanet",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee1));

        Employee testEmployee2 = new Employee(43,"CatPlanet1",JobType.DOCTOR,false,"douglas");
        assertTrue(myDBS.insertEmployee(testEmployee2));

        // Insert two
        assertTrue(myDBS.insertReservation(res0));
        assertTrue(myDBS.insertReservation(res1));
        assertTrue(myDBS.insertReservation(res2));

        GregorianCalendar gapStart = new GregorianCalendar();
        GregorianCalendar gapEnd = new GregorianCalendar();
        gapStart.setTime(new Date(now - 6000));
        gapEnd.setTime(new Date(now + 200));

        // Check that only one is retrieved (small time block)
        reservationList = myDBS.getReservationsBySpaceIdBetween("ABCD", gapStart, gapEnd);
        assertThat(reservationList.size(), is(1));
        assertEquals(res0, reservationList.get(0));

        gapStart.setTime(new Date(now - 1000000));
        gapEnd.setTime(new Date(now + 1100000));

        // Check that both are retrieved (large time block)
        reservationList = myDBS.getReservationsBySpaceIdBetween("ABCD", gapStart, gapEnd);
        assertThat(reservationList.size(), is(2));
        assertEquals(res0, reservationList.get(0));
        assertEquals(res1, reservationList.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void insertAndGetEmployee() {
        // Assume an empty DB (ensured by setUp())

        Employee value, expected;

        // First verify that the Employee is null
        value = myDBS.getEmployee(0);
        assertThat(value, is(nullValue()));

        // Create an employee
        Employee employee = new Employee(0, "mrdoctor", JobType.DOCTOR, false, "douglas");

        // Verify successful insertion
        boolean insertRes = myDBS.insertEmployee(employee);
        assertTrue(insertRes);

        // Verify successful get
        expected = employee;
        value = myDBS.getEmployee(employee.getID());
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllEmployees() {
        Employee value, expected;

        // First verify that the Employees are null
        value = myDBS.getEmployee(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getEmployee(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getEmployee(2);
        assertThat(value, is(nullValue()));

        // Create an employee
        Employee employee1 = new Employee(0, "douglas", JobType.DOCTOR, false, "douglas");
        Employee employee2 = new Employee(1, "tferrara", JobType.NURSE, false, "tyler");
        Employee employee3 = new Employee(2, "josh", JobType.ADMINISTRATOR, true, "joshua");
        Employee tylerImpersonator = new Employee(3, "tferrara", JobType.NURSE, true, "tyler");

        // Verify successful insertion
        boolean insertRes = myDBS.insertEmployee(employee1);
        assertTrue(insertRes);
        insertRes = myDBS.insertEmployee(employee2);
        assertTrue(insertRes);
        assertFalse(myDBS.insertEmployee(tylerImpersonator));

        // Check that there are two and only two, and that they are the right two
        List<Employee> employeeList = myDBS.getAllEmployees();
        assertThat(employeeList.size(), is(2));
        assertEquals(employee1, employeeList.get(0));
        assertEquals(employee2, employeeList.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertEmployee(employee3));

        employeeList = myDBS.getAllEmployees();
        assertThat(employeeList.size(), is(3));
        assertEquals(employee1, employeeList.get(0));
        assertEquals(employee2, employeeList.get(1));
        assertEquals(employee3, employeeList.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateEmployee() {
        Employee employee = new Employee(0, "doc", JobType.DOCTOR, false, "123456");

        assertTrue(myDBS.insertEmployee(employee));
        assertEquals(employee, myDBS.getEmployee(0));

        employee.setAdmin(true);
        employee.setJob(JobType.ADMINISTRATOR);

        assertTrue(myDBS.updateEmployee(employee));
        assertEquals(employee, myDBS.getEmployee(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteEmployee() {
        Employee employee = new Employee(0, "doc", JobType.DOCTOR, false, "password");

        assertTrue(myDBS.insertEmployee(employee));
        assertEquals(employee, myDBS.getEmployee(0));

        assertTrue(myDBS.deleteEmployee(employee));
        assertNull(myDBS.getEmployee(0));
    }

    @Test
    @Category(FastTest.class)
    public void insertAndGetReservableSpace()  {
        // Assume an empty DB (ensured by setUp())

        ReservableSpace value, expected;

        // First verify that the ReservableSpace is null
        value = myDBS.getReservableSpace("ABCD");
        assertThat(value, is(nullValue()));


        // Create a ReservableSpace
        GregorianCalendar openTime = new GregorianCalendar();
        openTime.set(Calendar.HOUR, 7);
        openTime.set(Calendar.MINUTE, 0);
        GregorianCalendar closeTime = new GregorianCalendar();
        closeTime.set(Calendar.HOUR, 17);
        closeTime.set(Calendar.MINUTE, 30);
        ReservableSpace space = new ReservableSpace("ABCD", "Space 1", "CONF", "LMNO10011", openTime, closeTime);

        // Verify successful insertion
        boolean insertRes = myDBS.insertReservableSpace(space);
        assertTrue(insertRes);

        // Verify successful get
        expected = space;
        value = myDBS.getReservableSpace(space.getSpaceID());
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllReservableSpaces() {
        // Assume an empty DB (ensured by setUp())
        ReservableSpace value, expected;

        // First verify that the ReservableSpace is null
        value = myDBS.getReservableSpace("ABCD");
        assertThat(value, is(nullValue()));
        value = myDBS.getReservableSpace("XYZ");
        assertThat(value, is(nullValue()));
        value = myDBS.getReservableSpace("LMNO");
        assertThat(value, is(nullValue()));


        // Create a ReservableSpace
        GregorianCalendar openTime = new GregorianCalendar();
        openTime.set(Calendar.HOUR, 7);
        openTime.set(Calendar.MINUTE, 0);
        GregorianCalendar closeTime = new GregorianCalendar();
        closeTime.set(Calendar.HOUR, 17);
        closeTime.set(Calendar.MINUTE, 30);

        ReservableSpace space1 = new ReservableSpace("ABCD", "Space 1", "CONF", "ABCD10011", openTime, closeTime);
        ReservableSpace space2 = new ReservableSpace("XYZ", "Space 2", "WKRS", "XYZ10011", openTime, closeTime);
        ReservableSpace space3 = new ReservableSpace("LMNO", "Space 3", "CONF", "LMNO10011", openTime, closeTime);

        // Verify successful insertion
        boolean insertRes = myDBS.insertReservableSpace(space1);
        assertTrue(insertRes);
        insertRes = myDBS.insertReservableSpace(space2);
        assertTrue(insertRes);

        // Check that there are two and only two, and that they are the right two
        List<ReservableSpace> allReservableSpaces = myDBS.getAllReservableSpaces();
        assertThat(allReservableSpaces.size(), is(2));
        assertEquals(space1, allReservableSpaces.get(0));
        assertEquals(space2, allReservableSpaces.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertReservableSpace(space3));

        allReservableSpaces = myDBS.getAllReservableSpaces();
        assertThat(allReservableSpaces.size(), is(3));
        assertEquals(space1, allReservableSpaces.get(0));
        assertEquals(space2, allReservableSpaces.get(1));
        assertEquals(space3, allReservableSpaces.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void getBookedReservableSpacesBetween() {
        // Create employee
        Employee emp = new Employee(1234, "JOe", JobType.DOCTOR, false, "pass");
        myDBS.insertEmployee(emp);

        // Create a ReservableSpace
        GregorianCalendar openTime = new GregorianCalendar();
        openTime.set(Calendar.HOUR, 7);
        openTime.set(Calendar.MINUTE, 0);
        GregorianCalendar closeTime = new GregorianCalendar();
        closeTime.set(Calendar.HOUR, 23);
        closeTime.set(Calendar.MINUTE, 00);

        ReservableSpace space1 = new ReservableSpace("ABCD", "Space 1", "CONF", "ABCD10011", openTime, closeTime);
        ReservableSpace space2 = new ReservableSpace("XYZ", "Space 2", "WKRS", "XYZ10011", openTime, closeTime);
        ReservableSpace space3 = new ReservableSpace("LMNO", "Space 3", "CONF", "LMNO10011", openTime, closeTime);

        assertTrue(myDBS.insertReservableSpace(space1));
        assertTrue(myDBS.insertReservableSpace(space2));
        assertTrue(myDBS.insertReservableSpace(space3));

        // Query times
        GregorianCalendar betweenStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(9, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar betweenEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(11, 0)).atZone(ZoneId.of("America/New_York"))));

        ArrayList<ReservableSpace> value;

        // First verify that the query returns null
        value = (ArrayList) myDBS.getBookedReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(0));

        // Create a Reservation
        GregorianCalendar resStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(9, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar resEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(10, 0)).atZone(ZoneId.of("America/New_York"))));

        Reservation res1 = new Reservation(-1, 0, 1234, "Test", "ABCD", resStart, resEnd);
        Reservation res2 = new Reservation(-1, 0, 1234, "Test", "XYZ", resStart, resEnd);

        // Insert reservation
        assertTrue(myDBS.insertReservation(res1));

        // Now the query should return only Space1
        value = (ArrayList) myDBS.getBookedReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(1));
        assertThat(value.get(0), is(space1));

        // Insert reservation
        assertTrue(myDBS.insertReservation(res2));

        // Now the query should return space1 and space2
        value = (ArrayList) myDBS.getBookedReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(2));
        assertThat(value.get(0), is(space1));
        assertThat(value.get(1), is(space2));

        // Create a Reservation
        GregorianCalendar fakeStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(19, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar fakeEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(20, 0)).atZone(ZoneId.of("America/New_York"))));

        Reservation res3 = new Reservation(-1, 0, 1234, "Test", "ABCD", fakeStart, fakeEnd);

        // Insert reservation
        assertTrue(myDBS.insertReservation(res3));

        // Now the query should return space1 and space2
        value = (ArrayList) myDBS.getBookedReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(2));
        assertThat(value.get(0), is(space1));
        assertThat(value.get(1), is(space2));
    }

    @Test
    @Category(FastTest.class)
    public void getAvailableReservableSpacesBetween() {
        // Create employee
        Employee emp = new Employee(1234, "JOe", JobType.DOCTOR, false, "pass");
        myDBS.insertEmployee(emp);

        // Create a ReservableSpace
        GregorianCalendar openTime = new GregorianCalendar();
        GregorianCalendar closeTime = new GregorianCalendar();

        ReservableSpace space1 = new ReservableSpace("ABCD", "Space 1", "CONF", "ABCD10011", openTime, closeTime);
        ReservableSpace space2 = new ReservableSpace("XYZ", "Space 2", "WKRS", "XYZ10011", openTime, closeTime);
        ReservableSpace space3 = new ReservableSpace("LMNO", "Space 3", "CONF", "LMNO10011", openTime, closeTime);
        ReservableSpace space4 = new ReservableSpace("0001", "Space 3", "CONF", "LMNO10011", openTime, closeTime);
        ReservableSpace space5 = new ReservableSpace("0002", "Space 3", "CONF", "LMNO10011", openTime, closeTime);
        ReservableSpace space6 = new ReservableSpace("0003", "Space 3", "CONF", "LMNO10011", openTime, closeTime);

        assertTrue(myDBS.insertReservableSpace(space1));
        assertTrue(myDBS.insertReservableSpace(space2));
        assertTrue(myDBS.insertReservableSpace(space3));
        assertTrue(myDBS.insertReservableSpace(space4));
        assertTrue(myDBS.insertReservableSpace(space5));
        assertTrue(myDBS.insertReservableSpace(space6));

        // Query times
        GregorianCalendar betweenStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(9, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar betweenEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(12, 0)).atZone(ZoneId.of("America/New_York"))));

        ArrayList<ReservableSpace> value;

        // First verify that the query returns null
        value = (ArrayList) myDBS.getAvailableReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(6));
        assertThat(value.get(0), is(space1));
        assertThat(value.get(1), is(space2));
        assertThat(value.get(2), is(space3));

        // Create a Reservation
        GregorianCalendar resStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(9, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar resEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(10, 0)).atZone(ZoneId.of("America/New_York"))));
        Reservation res1 = new Reservation(-1, 0, 1234, "Test", "ABCD", resStart, resEnd);

        resStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(8, 0)).atZone(ZoneId.of("America/New_York"))));
        resEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(11, 30)).atZone(ZoneId.of("America/New_York"))));
        Reservation res2 = new Reservation(-1, 0, 1234, "Test", "XYZ", resStart, resEnd);

        resStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(11, 30)).atZone(ZoneId.of("America/New_York"))));
        resEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(18, 00)).atZone(ZoneId.of("America/New_York"))));
        Reservation res3 = new Reservation(-1, 0, 1234, "Test", "0001", resStart, resEnd);

        resStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(7, 0)).atZone(ZoneId.of("America/New_York"))));
        resEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(13, 00)).atZone(ZoneId.of("America/New_York"))));
        Reservation res4 = new Reservation(-1, 0, 1234, "Test", "0002", resStart, resEnd);

        // Insert reservation
        assertTrue(myDBS.insertReservation(res1));

        // Now the query should return only Space1
        value = (ArrayList) myDBS.getAvailableReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(5));
        assertThat(value.get(0), is(space2));
        assertThat(value.get(4), is(space6));

        // Insert reservation
        assertTrue(myDBS.insertReservation(res2));

        // Now the query should return space1 and space2
        value = (ArrayList) myDBS.getAvailableReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(4));
        assertThat(value.get(0), is(space3));

        // Create a Reservation
        GregorianCalendar fakeStart = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(19, 0)).atZone(ZoneId.of("America/New_York"))));
        GregorianCalendar fakeEnd = GregorianCalendar.from(ZonedDateTime.from(LocalDate.now().atTime(LocalTime.of(20, 0)).atZone(ZoneId.of("America/New_York"))));

        Reservation res5 = new Reservation(-1, 0, 1234, "Test", "ABCD", fakeStart, fakeEnd);

        // Insert reservation
        assertTrue(myDBS.insertReservation(res5));

        // Now the query should return space1 and space2
        value = (ArrayList) myDBS.getAvailableReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(4));
        assertThat(value.get(0), is(space3));

        // Insert reservation
        assertTrue(myDBS.insertReservation(res3));
        assertTrue(myDBS.insertReservation(res4));

        // Now the query should return space1 and space2
        value = (ArrayList) myDBS.getAvailableReservableSpacesBetween(betweenStart, betweenEnd);
        assertThat(value, hasSize(2));
        assertThat(value.get(0), is(space3));
        assertThat(value.get(1), is(space6));
    }

    @Test
    @Category(FastTest.class)
    public void updateReservableSpace() {
        GregorianCalendar openTime = new GregorianCalendar();
        openTime.set(Calendar.HOUR, 7);
        openTime.set(Calendar.MINUTE, 0);
        GregorianCalendar closeTime = new GregorianCalendar();
        closeTime.set(Calendar.HOUR, 17);
        closeTime.set(Calendar.MINUTE, 30);

        ReservableSpace space = new ReservableSpace("ABCD", "Space 1", "CONF", "ABCD10011", openTime, closeTime);

        assertTrue(myDBS.insertReservableSpace(space));
        assertEquals(space, myDBS.getReservableSpace("ABCD"));

        space.setSpaceName("Named Room");
        openTime.add(Calendar.MINUTE, -30);
        space.setTimeOpen(openTime);

        assertTrue(myDBS.updateReservableSpace(space));
        assertEquals(space, myDBS.getReservableSpace("ABCD"));
    }

    @Test
    @Category(FastTest.class)
    public void deleteReservableSpace() {
        GregorianCalendar openTime = new GregorianCalendar();
        openTime.set(Calendar.HOUR, 7);
        openTime.set(Calendar.MINUTE, 0);
        GregorianCalendar closeTime = new GregorianCalendar();
        closeTime.set(Calendar.HOUR, 17);
        closeTime.set(Calendar.MINUTE, 30);
    }

    @Test
    @Category(FastTest.class)
    public void insertAndGetITRequest() {
        // Assume an empty DB (ensured by setUp())

        ITRequest value, expected;

        // First verify that the request is null
        value = myDBS.getITRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req = new ITRequest(0, "No notes", node, false, "New mouse required");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertITRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getITRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllITRequests() {
        // Assume an empty DB (ensured by setUp())

        ITRequest value;

        // First verify that these requests are null
        value = myDBS.getITRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getITRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getITRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req1 = new ITRequest(0, "No notes", node, false, "New mouse required");
        ITRequest req2 = new ITRequest(1, "Priority", node, true, "No internet");
        ITRequest req3 = new ITRequest(2, "Notes go here", node, false, "Help me");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertITRequest(req1));
        assertTrue(myDBS.insertITRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<ITRequest> allITRequests = myDBS.getAllITRequests();
        assertThat(allITRequests.size(), is(2));
        assertEquals(req1, allITRequests.get(0));
        assertEquals(req2, allITRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertITRequest(req3));

        req3.setId(2);

        allITRequests = myDBS.getAllITRequests();
        assertThat(allITRequests.size(), is(3));
        assertEquals(req1, allITRequests.get(0));
        assertEquals(req2, allITRequests.get(1));
        assertEquals(req3, allITRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateITRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req = new ITRequest(0, "No notes", node, false, "New mouse required");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertITRequest(req));
        assertEquals(req, myDBS.getITRequest(0));

        req.setDescription("Two new mouses needed");
        req.setCompleted(true);

        assertTrue(myDBS.updateITRequest(req));
        assertEquals(req, myDBS.getITRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteITRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req = new ITRequest(0, "No notes", node, false, "New mouse required");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertITRequest(req));
        assertEquals(req, myDBS.getITRequest(0));

        assertTrue(myDBS.deleteITRequest(req));
        assertNull(myDBS.getITRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteITRequests() {
        // Assume an empty DB (ensured by setUp())

        ITRequest value;

        // First verify that these requests are null
        value = myDBS.getITRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getITRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getITRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req1 = new ITRequest(0, "No notes", node, false, "New mouse required");
        ITRequest req2 = new ITRequest(1, "Priority", node, true, "No internet");
        ITRequest req3 = new ITRequest(2, "Notes go here", node, false, "Help me");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertITRequest(req1));
        assertTrue(myDBS.insertITRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<ITRequest> allITRequests = myDBS.getAllIncompleteITRequests();
        assertThat(allITRequests.size(), is(1));
        assertEquals(req1, allITRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertITRequest(req3));

        allITRequests = myDBS.getAllIncompleteITRequests();
        assertThat(allITRequests.size(), is(2));
        assertEquals(req1, allITRequests.get(0));
        assertEquals(req3, allITRequests.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void insertAndGetMedicineRequest() {
        // Assume an empty DB (ensured by setUp())

        MedicineRequest value, expected;

        // First verify that the request is null
        value = myDBS.getMedicineRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req = new MedicineRequest(0, "Quickly please", node, false, "Ibuprofen", 2.5);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertMedicineRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getMedicineRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllMedicineRequests() {
        // Assume an empty DB (ensured by setUp())

        MedicineRequest value;

        // First verify that these requests are null
        value = myDBS.getMedicineRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getMedicineRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getMedicineRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req1 = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);
        MedicineRequest req2 = new MedicineRequest(1, "Priority", node, true, "Asprin", 10);
        MedicineRequest req3 = new MedicineRequest(2, "Notes go here", node, false, "Some other medicine", 1);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMedicineRequest(req1));
        assertTrue(myDBS.insertMedicineRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MedicineRequest> allMedicineRequests = myDBS.getAllMedicineRequests();
        assertThat(allMedicineRequests.size(), is(2));
        assertEquals(req1, allMedicineRequests.get(0));
        assertEquals(req2, allMedicineRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertMedicineRequest(req3));

        allMedicineRequests = myDBS.getAllMedicineRequests();
        assertThat(allMedicineRequests.size(), is(3));
        assertEquals(req1, allMedicineRequests.get(0));
        assertEquals(req2, allMedicineRequests.get(1));
        assertEquals(req3, allMedicineRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateMedicineRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMedicineRequest(req));
        assertEquals(req, myDBS.getMedicineRequest(0));

        req.setNotes("Capsules");
        req.setQuantity(5.333);

        assertTrue(myDBS.updateMedicineRequest(req));
        assertEquals(req, myDBS.getMedicineRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteMedicineRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMedicineRequest(req));
        assertEquals(req, myDBS.getMedicineRequest(0));

        assertTrue(myDBS.deleteMedicineRequest(req));
        assertNull(myDBS.getMedicineRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteMedicineRequests() {
        // Assume an empty DB (ensured by setUp())

        MedicineRequest value;

        // First verify that these requests are null
        value = myDBS.getMedicineRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getMedicineRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getMedicineRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req1 = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);
        MedicineRequest req2 = new MedicineRequest(1, "Priority", node, true, "Asprin", 10);
        MedicineRequest req3 = new MedicineRequest(2, "Notes go here", node, false, "Some other medicine", 1);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMedicineRequest(req1));
        assertTrue(myDBS.insertMedicineRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MedicineRequest> allMedicineRequests = myDBS.getAllIncompleteMedicineRequests();
        assertThat(allMedicineRequests.size(), is(1));
        assertEquals(req1, allMedicineRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertMedicineRequest(req3));

        allMedicineRequests = myDBS.getAllIncompleteMedicineRequests();
        assertThat(allMedicineRequests.size(), is(2));
        assertEquals(req1, allMedicineRequests.get(0));
        assertEquals(req3, allMedicineRequests.get(1));
    }





    ///////////////////////// REQUEST 1 TESTS //////////////////////////////////////////////////////////////////////////

    @Test
    @Category(FastTest.class)
    public void insertAndGetFloristRequest() {
        // Assume an empty DB (ensured by setUp())

        FloristRequest value, expected;

        // First verify that the request is null
        value = myDBS.getFloristRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        FloristRequest req = new FloristRequest(0, "Yellow", node, false, "Get Well Bouquet", 1);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertFloristRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getFloristRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllFloristRequests() {
        // Assume an empty DB (ensured by setUp())

        FloristRequest value;

        // First verify that these requests are null
        value = myDBS.getFloristRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getFloristRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getFloristRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        FloristRequest req1 = new FloristRequest(0, "No notes", node, false, "Get Well", 6);
        FloristRequest req2 = new FloristRequest(1, "Priority", node, true, "Red Rose", 10);
        FloristRequest req3 = new FloristRequest(2, "Notes go here", node, false, "Douglas", 1);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertFloristRequest(req1));
        assertTrue(myDBS.insertFloristRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<FloristRequest> allFloristRequests = myDBS.getAllFloristRequests();
        assertThat(allFloristRequests.size(), is(2));
        assertEquals(req1, allFloristRequests.get(0));
        assertEquals(req2, allFloristRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertFloristRequest(req3));

        allFloristRequests = myDBS.getAllFloristRequests();
        assertThat(allFloristRequests.size(), is(3));
        assertEquals(req1, allFloristRequests.get(0));
        assertEquals(req2, allFloristRequests.get(1));
        assertEquals(req3, allFloristRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateFloristRequests() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        FloristRequest req = new FloristRequest(0, "No notes", node, false, "Red Rose", 4);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertFloristRequest(req));
        assertEquals(req, myDBS.getFloristRequest(0));

        req.setNotes("Rapid Order");
        req.setQuantity(7);

        assertTrue(myDBS.updateFloristRequest(req));
        assertEquals(req, myDBS.getFloristRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteFloristRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        FloristRequest req = new FloristRequest(0, "No notes", node, false, "Red Rose", 6);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertFloristRequest(req));
        assertEquals(req, myDBS.getFloristRequest(0));

        assertTrue(myDBS.deleteFloristRequest(req));
        assertNull(myDBS.getFloristRequest(0));
    }







    //////////////////////// END REQUEST 1 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 2 TESTS //////////////////////////////////////////////////////////////////////////
    @Test
    @Category(FastTest.class)
    public void insertAndGetSecurityRequest() {
        // Assume an empty DB (ensured by setUp())

        SecurityRequest value, expected;

        // First verify that the request is null
        value = myDBS.getSecurityRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SecurityRequest req = new SecurityRequest(0, "No notes", node, false, SecurityRequest.Urgency.VERY);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertSecurityRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getSecurityRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllSecurityRequests() {
        // Assume an empty DB (ensured by setUp())

        SecurityRequest value;

        // First verify that these requests are null
        value = myDBS.getSecurityRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getSecurityRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getSecurityRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SecurityRequest req1 = new SecurityRequest(0, "No notes", node, false, SecurityRequest.Urgency.VERY);
        SecurityRequest req2 = new SecurityRequest(1, "Priority", node, true, SecurityRequest.Urgency.NOT);
        SecurityRequest req3 = new SecurityRequest(2, "Notes go here", node, false, SecurityRequest.Urgency.SOMEWHAT);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSecurityRequest(req1));
        assertTrue(myDBS.insertSecurityRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<SecurityRequest> allSecurityRequests = myDBS.getAllSecurityRequests();
        assertThat(allSecurityRequests.size(), is(2));
        assertEquals(req1, allSecurityRequests.get(0));
        assertEquals(req2, allSecurityRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertSecurityRequest(req3));

        req3.setId(2);

        allSecurityRequests = myDBS.getAllSecurityRequests();
        assertThat(allSecurityRequests.size(), is(3));
        assertEquals(req1, allSecurityRequests.get(0));
        assertEquals(req2, allSecurityRequests.get(1));
        assertEquals(req3, allSecurityRequests.get(2));
    }


    @Test
    @Category(FastTest.class)
    public void updateSecurityRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SecurityRequest req = new SecurityRequest(0, "No notes", node, false, SecurityRequest.Urgency.VERY);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSecurityRequest(req));
        assertEquals(req, myDBS.getSecurityRequest(0));

        req.setUrgency(SecurityRequest.Urgency.NOT);
        req.setCompleted(true);

        assertTrue(myDBS.updateSecurityRequest(req));
        assertEquals(req, myDBS.getSecurityRequest(0));
    }


    @Test
    @Category(FastTest.class)
    public void deleteSecurityRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SecurityRequest req = new SecurityRequest(0, "No notes", node, false, SecurityRequest.Urgency.VERY);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSecurityRequest(req));
        assertEquals(req, myDBS.getSecurityRequest(0));

        assertTrue(myDBS.deleteSecurityRequest(req));
        assertNull(myDBS.getSecurityRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteSecurityRequests() {
        // Assume an empty DB (ensured by setUp())

        SecurityRequest value;

        // First verify that these requests are null
        value = myDBS.getSecurityRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getSecurityRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getSecurityRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SecurityRequest req1 = new SecurityRequest(0, "No notes", node, false, SecurityRequest.Urgency.VERY);
        SecurityRequest req2 = new SecurityRequest(1, "Priority", node, true, SecurityRequest.Urgency.NOT);
        SecurityRequest req3 = new SecurityRequest(2, "Notes go here", node, false, SecurityRequest.Urgency.SOMEWHAT);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSecurityRequest(req1));
        assertTrue(myDBS.insertSecurityRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<SecurityRequest> allServiceRequests = myDBS.getAllIncompleteSecurityRequests();
        assertThat(allServiceRequests.size(), is(1));
        assertEquals(req1, allServiceRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertSecurityRequest(req3));

        allServiceRequests = myDBS.getAllIncompleteSecurityRequests();
        assertThat(allServiceRequests.size(), is(2));
        assertEquals(req1, allServiceRequests.get(0));
        assertEquals(req3, allServiceRequests.get(1));
    }

    //////////////////////// END REQUEST 2 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 3 TESTS //////////////////////////////////////////////////////////////////////////

    @Test
    @Category(FastTest.class)
    public void insertAndGetSanitationRequest() {
        // Assume an empty DB (ensured by setUp())

        SanitationRequest value, expected;

        // First verify that the request is null
        value = myDBS.getSanitationRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SanitationRequest req = new SanitationRequest(0, "No notes", node, false, "High", "Liquid");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertSanitationRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getSanitationRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllSanitationRequests() {
        // Assume an empty DB (ensured by setUp())

        SanitationRequest value;

        // First verify that these requests are null
        value = myDBS.getSanitationRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getSanitationRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getSanitationRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SanitationRequest req1 = new SanitationRequest(0, "No notes", node, false, "High", "Solid");
        SanitationRequest req2 = new SanitationRequest(1, "Priority", node, true, "Medium", "Mixture");
        SanitationRequest req3 = new SanitationRequest(2, "Notes go here", node, false, "Low", "Liquid");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSanitationRequest(req1));
        assertTrue(myDBS.insertSanitationRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<SanitationRequest> allSanitationRequests = myDBS.getAllSanitationRequests();
        assertThat(allSanitationRequests.size(), is(2));
        assertEquals(req1, allSanitationRequests.get(0));
        assertEquals(req2, allSanitationRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertSanitationRequest(req3));

        req3.setId(2);

        allSanitationRequests = myDBS.getAllSanitationRequests();
        assertThat(allSanitationRequests.size(), is(3));
        assertEquals(req1, allSanitationRequests.get(0));
        assertEquals(req2, allSanitationRequests.get(1));
        assertEquals(req3, allSanitationRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateSanitationRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SanitationRequest req = new SanitationRequest(0, "No notes", node, false, "High", "Other");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSanitationRequest(req));
        assertEquals(req, myDBS.getSanitationRequest(0));

        req.setMaterialState("Liquid");
        req.setCompleted(true);

        assertTrue(myDBS.updateSanitationRequest(req));
        assertEquals(req, myDBS.getSanitationRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteSanitationRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SanitationRequest req = new SanitationRequest(0, "No notes", node, false, "Low", "Solid");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSanitationRequest(req));
        assertEquals(req, myDBS.getSanitationRequest(0));

        assertTrue(myDBS.deleteSanitationRequest(req));
        assertNull(myDBS.getSanitationRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteSanitationRequests() {
        // Assume an empty DB (ensured by setUp())

        SanitationRequest value;

        // First verify that these requests are null
        value = myDBS.getSanitationRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getSanitationRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getSanitationRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        SanitationRequest req1 = new SanitationRequest(0, "No notes", node, false, "High", "Liquid");
        SanitationRequest req2 = new SanitationRequest(1, "Priority", node, true, "Low", "Liquid");
        SanitationRequest req3 = new SanitationRequest(2, "Notes go here", node, false, "Medium", "Liquid");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertSanitationRequest(req1));
        assertTrue(myDBS.insertSanitationRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<SanitationRequest> allIncompleteSanitationRequests = myDBS.getAllIncompleteSanitationRequests();
        assertThat(allIncompleteSanitationRequests.size(), is(1));
        assertEquals(req1, allIncompleteSanitationRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertSanitationRequest(req3));

        allIncompleteSanitationRequests = myDBS.getAllIncompleteSanitationRequests();
        assertThat(allIncompleteSanitationRequests.size(), is(2));
        assertEquals(req1, allIncompleteSanitationRequests.get(0));
        assertEquals(req3, allIncompleteSanitationRequests.get(1));
    }





    //////////////////////// END REQUEST 3 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 4 TESTS //////////////////////////////////////////////////////////////////////////


    @Test
    @Category(FastTest.class)
    public void insertAndGetGiftStoreRequest() {
        // Assume an empty DB (ensured by setUp())

        GiftStoreRequest value, expected;

        // First verify that the request is null
        value = myDBS.getGiftStoreRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        GiftStoreRequest req = new GiftStoreRequest(0, "Get well soon!", node, false, GiftStoreRequest.GiftType.BALLOONS,  "Mary");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertGiftStoreRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getGiftStoreRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteGiftStoreRequests() {
        // Assume an empty DB (ensured by setUp())

        GiftStoreRequest value;

        // First verify that these requests are null
        value = myDBS.getGiftStoreRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getGiftStoreRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getGiftStoreRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        GiftStoreRequest req1 = new GiftStoreRequest(0, "No notes", node, false, GiftStoreRequest.GiftType.GIFT_BASKET,  "Tommy");
        GiftStoreRequest req2 = new GiftStoreRequest(1, "Get well soon!", node, false, GiftStoreRequest.GiftType.TEDDY_BEAR,"Ben");
        GiftStoreRequest req3 = new GiftStoreRequest(2, "feel better", node, false, GiftStoreRequest.GiftType.BALLOONS, "Tommy");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertGiftStoreRequest(req1));
        assertTrue(myDBS.insertGiftStoreRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<GiftStoreRequest> allGiftStoreRequests = myDBS.getAllIncompleteGiftStoreRequests();
        assertThat(allGiftStoreRequests.size(), is(2));
        assertEquals(req1, allGiftStoreRequests.get(0));
        assertEquals(req2, allGiftStoreRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertGiftStoreRequest(req3));

        req3.setId(2);

        allGiftStoreRequests = myDBS.getAllIncompleteGiftStoreRequests();
        assertThat(allGiftStoreRequests.size(), is(3));
        assertEquals(req1, allGiftStoreRequests.get(0));
        assertEquals(req2, allGiftStoreRequests.get(1));
        assertEquals(req3, allGiftStoreRequests.get(2));
    }



    @Test
    @Category(FastTest.class)
    public void getAllCompleteGiftStoreRequests() {
        // Assume an empty DB (ensured by setUp())

        GiftStoreRequest value;

        // First verify that these requests are null
        value = myDBS.getGiftStoreRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getGiftStoreRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getGiftStoreRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        GiftStoreRequest req1 = new GiftStoreRequest(0, "No notes", node, true, GiftStoreRequest.GiftType.GIFT_BASKET,  "Tommy");
        GiftStoreRequest req2 = new GiftStoreRequest(1, "Get well soon!", node, true, GiftStoreRequest.GiftType.TEDDY_BEAR,"Ben");
        GiftStoreRequest req3 = new GiftStoreRequest(2, "feel better", node, true, GiftStoreRequest.GiftType.BALLOONS, "Tommy");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertGiftStoreRequest(req1));
        assertTrue(myDBS.insertGiftStoreRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<GiftStoreRequest> allGiftStoreRequests = myDBS.getAllCompleteGiftStoreRequests();
        assertThat(allGiftStoreRequests.size(), is(2));
        assertEquals(req1, allGiftStoreRequests.get(0));
        assertEquals(req2, allGiftStoreRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertGiftStoreRequest(req3));

        req3.setId(2);

        allGiftStoreRequests = myDBS.getAllCompleteGiftStoreRequests();
        assertThat(allGiftStoreRequests.size(), is(3));
        assertEquals(req1, allGiftStoreRequests.get(0));
        assertEquals(req2, allGiftStoreRequests.get(1));
        assertEquals(req3, allGiftStoreRequests.get(2));
    }







    @Test
    @Category(FastTest.class)
    public void updateGiftStoreRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        GiftStoreRequest req = new GiftStoreRequest(0, "No notes", node, false, GiftStoreRequest.GiftType.TEDDY_BEAR, "Ron");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertGiftStoreRequest(req));
        assertEquals(req, myDBS.getGiftStoreRequest(0));

        req.setPatientName("SOMETHING THAT CHANGED ");
        req.setCompleted(true);

        assertTrue(myDBS.updateGiftStoreRequest(req));
        assertEquals(req, myDBS.getGiftStoreRequest(0));
    }


    @Test
    @Category(FastTest.class)
    public void deleteGiftStoreRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        GiftStoreRequest req = new GiftStoreRequest(0, "No notes", node, false, GiftStoreRequest.GiftType.BALLOONS, "me");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertGiftStoreRequest(req));
        assertEquals(req, myDBS.getGiftStoreRequest(0));

        assertTrue(myDBS.deleteGiftStoreRequest(req));
        assertNull(myDBS.getGiftStoreRequest(0));
    }




    //////////////////////// END REQUEST 4 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 5 TESTS //////////////////////////////////////////////////////////////////////////







    //////////////////////// END REQUEST 5 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 6 TESTS //////////////////////////////////////////////////////////////////////////

    @Test
    @Category(FastTest.class)
    public void insertAndGetInterpreterRequest() {
        // Assume an empty DB (ensured by setUp())

        InterpreterRequest value, expected;

        // First verify that the request is null
        value = myDBS.getInterpreterRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InterpreterRequest req = new InterpreterRequest(0, "Quickly please", node, false, InterpreterRequest.Language.ENGLISH);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertInterpreterRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getInterpreterRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllInterpreterRequests() {
        // Assume an empty DB (ensured by setUp())

        InterpreterRequest value;

        // First verify that these requests are null
        value = myDBS.getInterpreterRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getInterpreterRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getInterpreterRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InterpreterRequest req1 = new InterpreterRequest(0, "No notes", node, false, InterpreterRequest.Language.ENGLISH);
        InterpreterRequest req2 = new InterpreterRequest(1, "Priority", node, true, InterpreterRequest.Language.FRENCH);
        InterpreterRequest req3 = new InterpreterRequest(2, "Notes go here", node, false, InterpreterRequest.Language.MANDARIN);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInterpreterRequest(req1));
        assertTrue(myDBS.insertInterpreterRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<InterpreterRequest> allInterpreterRequests = myDBS.getAllInterpreterRequests();
        assertThat(allInterpreterRequests.size(), is(2));
        assertEquals(req1, allInterpreterRequests.get(0));
        assertEquals(req2, allInterpreterRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertInterpreterRequest(req3));

        allInterpreterRequests = myDBS.getAllInterpreterRequests();
        assertThat(allInterpreterRequests.size(), is(3));
        assertEquals(req1, allInterpreterRequests.get(0));
        assertEquals(req2, allInterpreterRequests.get(1));
        assertEquals(req3, allInterpreterRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateInterpreterRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InterpreterRequest req = new InterpreterRequest(0, "No notes", node, false, InterpreterRequest.Language.ENGLISH);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInterpreterRequest(req));
        assertEquals(req, myDBS.getInterpreterRequest(0));

        req.setNotes("Capsules");
        req.setLanguage(InterpreterRequest.Language.FRENCH);

        assertTrue(myDBS.updateInterpreterRequest(req));
        assertEquals(req, myDBS.getInterpreterRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteInterpreterRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InterpreterRequest req = new InterpreterRequest(0, "No notes", node, false, InterpreterRequest.Language.ENGLISH);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInterpreterRequest(req));
        assertEquals(req, myDBS.getInterpreterRequest(0));

        assertTrue(myDBS.deleteInterpreterRequest(req));
        assertNull(myDBS.getInterpreterRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteInterpreterRequests() {
        // Assume an empty DB (ensured by setUp())

        InterpreterRequest value;

        // First verify that these requests are null
        value = myDBS.getInterpreterRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getInterpreterRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getInterpreterRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InterpreterRequest req1 = new InterpreterRequest(0, "No notes", node, false, InterpreterRequest.Language.ENGLISH);
        InterpreterRequest req2 = new InterpreterRequest(1, "Priority", node, true, InterpreterRequest.Language.FRENCH);
        InterpreterRequest req3 = new InterpreterRequest(2, "Notes go here", node, false, InterpreterRequest.Language.MANDARIN);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInterpreterRequest(req1));
        assertTrue(myDBS.insertInterpreterRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<InterpreterRequest> allInterpreterRequests = myDBS.getAllIncompleteInterpreterRequests();
        assertThat(allInterpreterRequests.size(), is(1));
        assertEquals(req1, allInterpreterRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertInterpreterRequest(req3));

        allInterpreterRequests = myDBS.getAllIncompleteInterpreterRequests();
        assertThat(allInterpreterRequests.size(), is(2));
        assertEquals(req1, allInterpreterRequests.get(0));
        assertEquals(req3, allInterpreterRequests.get(1));
    }





    //////////////////////// END REQUEST 6 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 7 TESTS //////////////////////////////////////////////////////////////////////////
    @Test
    @Category(FastTest.class)
    public void insertAndGetPatientInfoRequest() {
        // Assume an empty DB (ensured by setUp())

        PatientInfoRequest value, expected;

        // First verify that the request is null
        value = myDBS.getPatientInfoRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req = new PatientInfoRequest(0, "No notes", node, false);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertPatientInfoRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getPatientInfoRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllPatientInfoRequests() {
        // Assume an empty DB (ensured by setUp())

        PatientInfoRequest value;

        // First verify that these requests are null
        value = myDBS.getPatientInfoRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req1 = new PatientInfoRequest(0, "No notes", node, false);
        PatientInfoRequest req2 = new PatientInfoRequest(1, "Priority", node, true);
        PatientInfoRequest req3 = new PatientInfoRequest(2, "Notes go here", node, false);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertPatientInfoRequest(req1));
        assertTrue(myDBS.insertPatientInfoRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<PatientInfoRequest> allPatientInfoRequests = myDBS.getAllPatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(2));
        assertEquals(req1, allPatientInfoRequests.get(0));
        assertEquals(req2, allPatientInfoRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertPatientInfoRequest(req3));

        req3.setId(2);

        allPatientInfoRequests = myDBS.getAllPatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(3));
        assertEquals(req1, allPatientInfoRequests.get(0));
        assertEquals(req2, allPatientInfoRequests.get(1));
        assertEquals(req3, allPatientInfoRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updatePatientInfoRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req = new PatientInfoRequest(0, "No notes", node, false);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertPatientInfoRequest(req));
        assertEquals(req, myDBS.getPatientInfoRequest(0));

        req.setDescription("Two new mouses needed");
        req.setCompleted(true);

        assertTrue(myDBS.updatePatientInfoRequest(req));
        assertEquals(req, myDBS.getPatientInfoRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deletePatientInfoRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req = new PatientInfoRequest(0, "No notes", node, false);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertPatientInfoRequest(req));
        assertEquals(req, myDBS.getPatientInfoRequest(0));

        assertTrue(myDBS.deletePatientInfoRequest(req));
        assertNull(myDBS.getPatientInfoRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompletePatientInfoRequests() {
        // Assume an empty DB (ensured by setUp())

        PatientInfoRequest value;

        // First verify that these requests are null
        value = myDBS.getPatientInfoRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req1 = new PatientInfoRequest(0, "No notes", node, false);
        PatientInfoRequest req2 = new PatientInfoRequest(1, "Priority", node, true);
        PatientInfoRequest req3 = new PatientInfoRequest(2, "Notes go here", node, false);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertPatientInfoRequest(req1));
        assertTrue(myDBS.insertPatientInfoRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<PatientInfoRequest> allPatientInfoRequests = myDBS.getAllIncompletePatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(1));
        assertEquals(req1, allPatientInfoRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertPatientInfoRequest(req3));

        allPatientInfoRequests = myDBS.getAllIncompletePatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(2));
        assertEquals(req1, allPatientInfoRequests.get(0));
        assertEquals(req3, allPatientInfoRequests.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void getAllCompletePatientInfoRequests() {
        // Assume an empty DB (ensured by setUp())

        PatientInfoRequest value;

        // First verify that these requests are null
        value = myDBS.getPatientInfoRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getPatientInfoRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        PatientInfoRequest req1 = new PatientInfoRequest(0, "No notes", node, true);
        PatientInfoRequest req2 = new PatientInfoRequest(1, "Priority", node, false);
        PatientInfoRequest req3 = new PatientInfoRequest(2, "Notes go here", node, true);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertPatientInfoRequest(req1));
        assertTrue(myDBS.insertPatientInfoRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<PatientInfoRequest> allPatientInfoRequests = myDBS.getAllCompletePatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(1));
        assertEquals(req1, allPatientInfoRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertPatientInfoRequest(req3));

        allPatientInfoRequests = myDBS.getAllCompletePatientInfoRequests();
        assertThat(allPatientInfoRequests.size(), is(2));
        assertEquals(req1, allPatientInfoRequests.get(0));
        assertEquals(req3, allPatientInfoRequests.get(1));
    }

    //////////////////////// END REQUEST 7 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 8 TESTS //////////////////////////////////////////////////////////////////////////


    @Test
    @Category(FastTest.class)
    public void insertAndGetInternalTransportRequest() {
        // Assume an empty DB (ensured by setUp())

        InternalTransportRequest value, expected;

        // First verify that the request is null
        value = myDBS.getInternalTransportRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InternalTransportRequest req = new InternalTransportRequest(0, "No notes", node, false, InternalTransportRequest.TransportType.Stretcher);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertInternalTransportRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getInternalTransportRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllInternalTransportRequest() {
        // Assume an empty DB (ensured by setUp())

        InternalTransportRequest value;

        // First verify that these requests are null
        value = myDBS.getInternalTransportRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getInternalTransportRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getInternalTransportRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InternalTransportRequest req1 = new InternalTransportRequest(0, "No notes", node, false, InternalTransportRequest.TransportType.Wheelchair);
        InternalTransportRequest req2 = new InternalTransportRequest(1, "Priority", node, true, InternalTransportRequest.TransportType.Stretcher);
        InternalTransportRequest req3 = new InternalTransportRequest(2, "Notes go here", node, false, InternalTransportRequest.TransportType.MotorScooter);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInternalTransportRequest(req1));
        assertTrue(myDBS.insertInternalTransportRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<InternalTransportRequest> allInternalTransportRequests = myDBS.getAllInternalTransportRequest();
        assertThat(allInternalTransportRequests.size(), is(2));
        assertEquals(req1, allInternalTransportRequests.get(0));
        assertEquals(req2, allInternalTransportRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertInternalTransportRequest(req3));

        req3.setId(2);

        allInternalTransportRequests = myDBS.getAllInternalTransportRequest();
        assertThat(allInternalTransportRequests.size(), is(3));
        assertEquals(req1, allInternalTransportRequests.get(0));
        assertEquals(req2, allInternalTransportRequests.get(1));
        assertEquals(req3, allInternalTransportRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateInternalTransportRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InternalTransportRequest req = new InternalTransportRequest(0, "No notes", node, false, InternalTransportRequest.TransportType.MotorScooter);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInternalTransportRequest(req));
        assertEquals(req, myDBS.getInternalTransportRequest(0));

        req.setTransport(InternalTransportRequest.TransportType.Wheelchair);
        req.setCompleted(true);

        assertTrue(myDBS.updateInternalTransportRequest(req));
        assertEquals(req, myDBS.getInternalTransportRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteInternalTransportRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InternalTransportRequest req = new InternalTransportRequest(0, "No notes", node, false);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInternalTransportRequest(req));
        assertEquals(req, myDBS.getInternalTransportRequest(0));

        assertTrue(myDBS.deleteInternalTransportRequest(req));
        assertNull(myDBS.getInternalTransportRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllInternalTransportRequests() {
        // Assume an empty DB (ensured by setUp())

        InternalTransportRequest value;

        // First verify that these requests are null
        value = myDBS.getInternalTransportRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getInternalTransportRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getInternalTransportRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        InternalTransportRequest req1 = new InternalTransportRequest(0, "No notes", node, false);
        InternalTransportRequest req2 = new InternalTransportRequest(1, "Priority", node, true);
        InternalTransportRequest req3 = new InternalTransportRequest(2, "Notes go here", node, false);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertInternalTransportRequest(req1));
        assertTrue(myDBS.insertInternalTransportRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<InternalTransportRequest> allInternalTransportRequests = myDBS.getAllIncompleteInternalTransportRequests();
        assertThat(allInternalTransportRequests.size(), is(1));
        assertEquals(req1, allInternalTransportRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertInternalTransportRequest(req3));

        allInternalTransportRequests = myDBS.getAllIncompleteInternalTransportRequests();
        assertThat(allInternalTransportRequests.size(), is(2));
        assertEquals(req1, allInternalTransportRequests.get(0));
        assertEquals(req3, allInternalTransportRequests.get(1));
    }


    //////////////////////// END REQUEST 8 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 9 TESTS //////////////////////////////////////////////////////////////////////////

    @Test
    @Category(FastTest.class)
    public void insertAndGetExtTransRequest() {
        // Assume an empty DB (ensured by setUp())

        ExternalTransportRequest value, expected;

        // First verify that the request is null
        value = myDBS.getExtTransRequest(0);
        assertThat(value, is(nullValue()));

        Date d = new Date();
        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node node2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ExternalTransportRequest req = new ExternalTransportRequest(0, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");


        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertNode(node2));
        boolean insertRes = myDBS.insertExtTransRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getExtTransRequest(0);
        assertEquals(expected, value);
    }


    @Test
    @Category(FastTest.class)
    public void getAllExtTransRequests() {
        // Assume an empty DB (ensured by setUp())

        ExternalTransportRequest value;


        Date d = new Date();
        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node node2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ExternalTransportRequest req1 = new ExternalTransportRequest(0, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");
        ExternalTransportRequest req2 = new ExternalTransportRequest(1, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");
        ExternalTransportRequest req3 = new ExternalTransportRequest(2, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");


        // First verify that these requests are null
        value = myDBS.getExtTransRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getExtTransRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getExtTransRequest(2);
        assertThat(value, is(nullValue()));

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertExtTransRequest(req1));
        assertTrue(myDBS.insertExtTransRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<ExternalTransportRequest> allExtTransRequests = myDBS.getAllExtTransRequests();
        assertThat(allExtTransRequests.size(), is(2));
        assertEquals(req1, allExtTransRequests.get(0));
        assertEquals(req2, allExtTransRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertExtTransRequest(req3));

        req3.setId(2);

        allExtTransRequests = myDBS.getAllExtTransRequests();
        assertThat(allExtTransRequests.size(), is(3));
        assertEquals(req1, allExtTransRequests.get(0));
        assertEquals(req2, allExtTransRequests.get(1));
        assertEquals(req3, allExtTransRequests.get(2));
    }


    @Test
    @Category(FastTest.class)
    public void updateExtTransRequest() {
        ExternalTransportRequest value;


        Date d = new Date();
        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node node2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ExternalTransportRequest req = new ExternalTransportRequest(0, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertNode(node2));
        boolean insertRes = myDBS.insertExtTransRequest(req);
        assertTrue(insertRes);

        req.setDescription("Two new mouses needed");
        req.setCompleted(true);

        assertTrue(myDBS.updateExtTransRequest(req));
        assertEquals(req, myDBS.getExtTransRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteExtTransRequest() {
        ExternalTransportRequest value;


        Date d = new Date();
        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node node2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ExternalTransportRequest req = new ExternalTransportRequest(0, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertNode(node2));
        boolean insertRes = myDBS.insertExtTransRequest(req);
        assertTrue(insertRes);
        assertEquals(req, myDBS.getExtTransRequest(0));

        assertTrue(myDBS.deleteExtTransRequest(req));
        assertNull(myDBS.getITRequest(0));
    }


    @Test
    @Category(FastTest.class)
    public void getAllIncompleteExtTransRequests() {
        // Assume an empty DB (ensured by setUp())

        ExternalTransportRequest value;


        Date d = new Date();
        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node node2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ExternalTransportRequest req1 = new ExternalTransportRequest(0, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");
        ExternalTransportRequest req2 = new ExternalTransportRequest(1, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");
        ExternalTransportRequest req3 = new ExternalTransportRequest(2, "No notes", node, false, d, ExternalTransportRequest.TransportationType.BUS, "");


        // First verify that these requests are null
        value = myDBS.getExtTransRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getExtTransRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getExtTransRequest(2);
        assertThat(value, is(nullValue()));

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertNode(node2));
        assertTrue(myDBS.insertExtTransRequest(req1));
        assertTrue(myDBS.insertExtTransRequest(req2));


        // Check that there are two and only two, and that they are the right two
        List<ExternalTransportRequest> allExtTransRequests = myDBS.getAllIncompleteExtTransRequests();
        assertThat(allExtTransRequests.size(), is(2));
        assertEquals(req1, allExtTransRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertExtTransRequest(req3));

        allExtTransRequests = myDBS.getAllIncompleteExtTransRequests();
        assertThat(allExtTransRequests.size(), is(3));
        assertEquals(req1, allExtTransRequests.get(0));
        assertEquals(req2, allExtTransRequests.get(1));
    }





    //////////////////////// END REQUEST 9 TESTS ///////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 10 TESTS /////////////////////////////////////////////////////////////////////////







    //////////////////////// END REQUEST 10 TESTS //////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 11 TESTS /////////////////////////////////////////////////////////////////////////

    @Test
    @Category(FastTest.class)
    public void insertAndGetMaintenanceRequest() {
        // Assume an empty DB (ensured by setUp())

        MaintenanceRequest value, expected;

        // First verify that the request is null
        value = myDBS.getMaintenanceRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MaintenanceRequest req = new MaintenanceRequest(0, "Quickly please", node, false, MaintenanceRequest.MaintenanceType.Plumbing);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertMaintenanceRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getMaintenanceRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllMaintenanceRequest() {
        // Assume an empty DB (ensured by setUp())

        MaintenanceRequest value;

        // First verify that these requests are null
        value = myDBS.getMaintenanceRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getMaintenanceRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getMaintenanceRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MaintenanceRequest req1 = new MaintenanceRequest(0, "No notes", node, false, MaintenanceRequest.MaintenanceType.Electrical);
        MaintenanceRequest req2 = new MaintenanceRequest(1, "Priority", node, true, MaintenanceRequest.MaintenanceType.Plumbing);
        MaintenanceRequest req3 = new MaintenanceRequest(2, "Notes go here", node, false, MaintenanceRequest.MaintenanceType.Other);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMaintenanceRequest(req1));
        assertTrue(myDBS.insertMaintenanceRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MaintenanceRequest> allMaintenanceRequests = myDBS.getAllMaintenanceRequests();
        assertThat(allMaintenanceRequests.size(), is(2));
        assertEquals(req1, allMaintenanceRequests.get(0));
        assertEquals(req2, allMaintenanceRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertMaintenanceRequest(req3));

        allMaintenanceRequests = myDBS.getAllMaintenanceRequests();
        assertThat(allMaintenanceRequests.size(), is(3));
        assertEquals(req1, allMaintenanceRequests.get(0));
        assertEquals(req2, allMaintenanceRequests.get(1));
        assertEquals(req3, allMaintenanceRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteMaintenanceRequests() {
        // Assume an empty DB (ensured by setUp())

        MaintenanceRequest value;

        // First verify that these requests are null
        value = myDBS.getMaintenanceRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getMaintenanceRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getMaintenanceRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MaintenanceRequest req1 = new MaintenanceRequest(0, "No notes", node, false, MaintenanceRequest.MaintenanceType.Electrical);
        MaintenanceRequest req2 = new MaintenanceRequest(1, "Priority", node, true, MaintenanceRequest.MaintenanceType.Plumbing);
        MaintenanceRequest req3 = new MaintenanceRequest(2, "Notes go here", node, false, MaintenanceRequest.MaintenanceType.Other);

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMaintenanceRequest(req1));
        assertTrue(myDBS.insertMaintenanceRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MaintenanceRequest> allMaintenanceRequests = myDBS.getAllIncompleteMaintenanceRequests();
        assertThat(allMaintenanceRequests.size(), is(1));
        assertEquals(req1, allMaintenanceRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertMaintenanceRequest(req3));

        allMaintenanceRequests = myDBS.getAllIncompleteMaintenanceRequests();
        assertThat(allMaintenanceRequests.size(), is(2));
        assertEquals(req1, allMaintenanceRequests.get(0));
        assertEquals(req3, allMaintenanceRequests.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void updateMaintenanceRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MaintenanceRequest req = new MaintenanceRequest(0, "No notes", node, false, MaintenanceRequest.MaintenanceType.Electrical);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMaintenanceRequest(req));
        assertEquals(req, myDBS.getMaintenanceRequest(0));

        req.setNotes("Capsules");
        req.setMaintenanceType(MaintenanceRequest.MaintenanceType.Plumbing);

        assertTrue(myDBS.updateMaintenanceRequest(req));
        assertEquals(req, myDBS.getMaintenanceRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteMaintenanceRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MaintenanceRequest req = new MaintenanceRequest(0, "No notes", node, false, MaintenanceRequest.MaintenanceType.Electrical);

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertMaintenanceRequest(req));
        assertEquals(req, myDBS.getMaintenanceRequest(0));

        assertTrue(myDBS.deleteMaintenanceRequest(req));
        assertNull(myDBS.getMaintenanceRequest(0));
    }

    //////////////////////// END REQUEST 11 TESTS //////////////////////////////////////////////////////////////////////
    ///////////////////////// REQUEST 12 TESTS /////////////////////////////////////////////////////////////////////////
    @Test
    @Category(FastTest.class)
    public void insertAndGetToyRequest() {
        // Assume an empty DB (ensured by setUp())

        ToyRequest value, expected;

        // First verify that the request is null
        value = myDBS.getToyRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req = new ToyRequest(0, "No notes", node, false, "Xylophone");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        boolean insertRes = myDBS.insertToyRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = myDBS.getToyRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllToyRequests() {
        // Assume an empty DB (ensured by setUp())

        ToyRequest value;

        // First verify that these requests are null
        value = myDBS.getToyRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req1 = new ToyRequest(0, "No notes", node, false, "Xylophone");
        ToyRequest req2 = new ToyRequest(1, "Priority", node, true, "Buzz Lightyear");
        ToyRequest req3 = new ToyRequest(2, "Notes go here", node, false, "Doll");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertToyRequest(req1));
        assertTrue(myDBS.insertToyRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<ToyRequest> allToyRequests = myDBS.getAllToyRequests();
        assertThat(allToyRequests.size(), is(2));
        assertEquals(req1, allToyRequests.get(0));
        assertEquals(req2, allToyRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertToyRequest(req3));

        req3.setId(2);

        allToyRequests = myDBS.getAllToyRequests();
        assertThat(allToyRequests.size(), is(3));
        assertEquals(req1, allToyRequests.get(0));
        assertEquals(req2, allToyRequests.get(1));
        assertEquals(req3, allToyRequests.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateToyRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req = new ToyRequest(0, "No notes", node, false, "Xylophone");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertToyRequest(req));
        assertEquals(req, myDBS.getToyRequest(0));

        req.setToyName("Mr. Potato Head");
        req.setCompleted(true);

        assertTrue(myDBS.updateToyRequest(req));
        assertEquals(req, myDBS.getToyRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteToyRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req = new ToyRequest(0, "No notes", node, false, "Xylophone");

        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertToyRequest(req));
        assertEquals(req, myDBS.getToyRequest(0));

        assertTrue(myDBS.deleteToyRequest(req));
        assertNull(myDBS.getToyRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteToyRequests() {
        // Assume an empty DB (ensured by setUp())

        ToyRequest value;

        // First verify that these requests are null
        value = myDBS.getToyRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req1 = new ToyRequest(0, "No notes", node, false, "Xylophone");
        ToyRequest req2 = new ToyRequest(1, "Priority", node, true, "Mr. Potato Head");
        ToyRequest req3 = new ToyRequest(2, "Notes go here", node, false, "Woody");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertToyRequest(req1));
        assertTrue(myDBS.insertToyRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<ToyRequest> allToyRequests = myDBS.getAllIncompleteToyRequests();
        assertThat(allToyRequests.size(), is(1));
        assertEquals(req1, allToyRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertToyRequest(req3));

        allToyRequests = myDBS.getAllIncompleteToyRequests();
        assertThat(allToyRequests.size(), is(2));
        assertEquals(req1, allToyRequests.get(0));
        assertEquals(req3, allToyRequests.get(1));
    }

    @Test
    @Category(FastTest.class)
    public void getAllCompleteToyRequests() {
        // Assume an empty DB (ensured by setUp())
        ToyRequest value;

        // First verify that these requests are null
        value = myDBS.getToyRequest(0);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(1);
        assertThat(value, is(nullValue()));
        value = myDBS.getToyRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ToyRequest req1 = new ToyRequest(0, "No notes", node, false, "Xylophone");
        ToyRequest req2 = new ToyRequest(1, "Priority", node, true, "Mr. Potato Head");
        ToyRequest req3 = new ToyRequest(2, "Notes go here", node, false, "Woody");

        // Verify successful insertion
        assertTrue(myDBS.insertNode(node));
        assertTrue(myDBS.insertToyRequest(req1));
        assertTrue(myDBS.insertToyRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<ToyRequest> allToyRequests = myDBS.getAllCompleteToyRequests();
        assertThat(allToyRequests.size(), is(1));
        assertEquals(req2, allToyRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(myDBS.insertToyRequest(req3));

        allToyRequests = myDBS.getAllCompleteToyRequests();
        assertThat(allToyRequests.size(), is(1));
        assertEquals(req2, allToyRequests.get(0));
    }
    //////////////////////// END REQUEST 12 TESTS //////////////////////////////////////////////////////////////////////
}
