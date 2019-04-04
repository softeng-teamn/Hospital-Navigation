package service;

import model.*;
import model.request.ITRequest;
import model.request.MedicineRequest;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseServiceTest {
    @Before
    public void setUp() {
        DatabaseService.getDatabaseService(true, false);
    }

    @AfterClass
    public static void tearDown() {
        DatabaseService.wipeOutFiles();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        FileUtils.deleteDirectory(new File("hospital-db-test"));
    }

    @Test
    @Category(FastTest.class)
    public void insertNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        // make sure that the new node is successfully inserted
        assertThat(DatabaseService.getDatabaseService().insertNode(testNode), is(true));
        // make sure that the same node cannot be inserted a second time
        assertThat(DatabaseService.getDatabaseService().insertNode(testNode), is(false));
    }

    @Test
    @Category(FastTest.class)
    public void getNode(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode);
        Node toGet = DatabaseService.getDatabaseService().getNode("ACONF00102");
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
        DatabaseService.getDatabaseService().insertNode(testNode);
        assertThat(DatabaseService.getDatabaseService().getNode("NOTINFIELD"), is(nullValue()));
    }

    @Test
    @Category(FastTest.class)
    public void updateNode() {
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode);

        Node toGet = DatabaseService.getDatabaseService().getNode("ACONF00102");
        assertThat(toGet.getNodeID(),is("ACONF00102"));
        assertThat(toGet.getXcoord(),is(1580));
        assertThat(toGet.getYcoord(),is(2538));
        assertThat(toGet.getFloor(),is("2"));
        assertThat(toGet.getBuilding(),is("BTM"));
        assertThat(toGet.getNodeType(),is("HALL"));
        assertThat(toGet.getShortName(),is("Hall"));
        assertThat(toGet.getLongName(),is("Hall"));


        testNode = new Node("ACONF00102", 1582, 2540, "3", "BTM", "CONF", "Halla", "Halls");
        DatabaseService.getDatabaseService().updateNode(testNode);

        toGet = DatabaseService.getDatabaseService().getNode("ACONF00102");
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
        DatabaseService.getDatabaseService().insertNode(testNode);
        // make sure it can be got
        assertThat(DatabaseService.getDatabaseService().getNode("ACONF00102").getNodeID(), is("ACONF00102"));
        // delete the node from the database successfully
        assertThat(DatabaseService.getDatabaseService().deleteNode(testNode),is(true));
        //make sure that it is not in the database
        assertThat((DatabaseService.getDatabaseService().getNode("ACONF00102")), is(nullValue()));
        //delete is like update so trying to delete a record that isn't there doesn't cause problems. No case needed for that.
    }

    @Test
    @Category(FastTest.class)
    public void getAllNodes() {
        // insert nodes
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode);
        testNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        DatabaseService.getDatabaseService().insertNode(testNode);
        ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();
        assertThat(allNodes.size(),is(2));
        assertThat(allNodes.get(0).getNodeID(),is("ACONF00102"));
        assertThat(allNodes.get(1).getNodeID(),is("ACONF00103"));

        testNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        DatabaseService.getDatabaseService().insertNode(testNode);
        allNodes = DatabaseService.getDatabaseService().getAllNodes();
        assertThat(allNodes.size(),is(3));
        assertThat(allNodes.get(0).getNodeID(),is("ACONF00102"));
        assertThat(allNodes.get(1).getNodeID(),is("ACONF00103"));
        assertThat(allNodes.get(2).getNodeID(),is("ACONF00104"));
    }

    @Test
    @Category(FastTest.class)
    public void insertAllNodes() {
        final Function callback = mock(Function.class);
        DatabaseService.getDatabaseService().registerNodeCallback(callback);

        assertThat(DatabaseService.getDatabaseService().getAllNodes().size(), is(0));
        ArrayList<Node> nodes = new ArrayList<>();

        for (int i = 0; i < 10001; i ++) {
            nodes.add(new Node("" + i, i, i, "2", "BTM", "HALL", "Hall", "Hall"));
        }

        assertTrue(DatabaseService.getDatabaseService().insertAllNodes(nodes));

        verify(callback, times(11)).apply(null);

        assertThat(DatabaseService.getDatabaseService().getAllNodes().size(), is(10001));
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
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode1));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode2));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode3));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode4));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode5));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode6));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode7));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode8));
        assertTrue(DatabaseService.getDatabaseService().insertNode(testNode9));

        assertEquals(2, DatabaseService.getDatabaseService().getNumNodeTypeByFloor("HALL","2"));
        assertEquals(1, DatabaseService.getDatabaseService().getNumNodeTypeByFloor("STAI","2"));
        assertEquals(0, DatabaseService.getDatabaseService().getNumNodeTypeByFloor("HALL","1"));
        assertEquals(2, DatabaseService.getDatabaseService().getNumNodeTypeByFloor("LABS","1"));
        assertEquals(1, DatabaseService.getDatabaseService().getNumNodeTypeByFloor("DEPT","1"));
    }

    @Test
    @Category(FastTest.class)
    public void getNodesByFloor() {
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        DatabaseService.getDatabaseService().insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        DatabaseService.getDatabaseService().insertNode(testNode3);
        ArrayList<Node> getByFloor = DatabaseService.getDatabaseService().getNodesByFloor("3");
        assertThat(getByFloor.size(), is(2));
        assertEquals(getByFloor.get(0),testNode2);
        assertEquals(getByFloor.get(1),testNode3);
    }

    @Test
    @Category(FastTest.class)
    public void getNodesFilteredByType() {
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "STAI", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "CONF", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode3);
        Node testNode4 = new Node("ACONF00105", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode4);
        Node testNode5 = new Node("ACONF00106", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode5);

        Node[] nodes0 = {testNode2, testNode3};
        assertThat(DatabaseService.getDatabaseService().getNodesFilteredByType("HALL"), containsInAnyOrder(nodes0));

        Node[] nodes1 = {testNode1, testNode2, testNode4, testNode5};
        assertThat(DatabaseService.getDatabaseService().getNodesFilteredByType("CONF"), containsInAnyOrder(nodes1));

        Node[] nodes2 = {testNode3};
        assertThat(DatabaseService.getDatabaseService().getNodesFilteredByType("HALL", "STAI"), containsInAnyOrder(nodes2));
    }

    @Test
    @Category(FastTest.class)
    public void getNodesConnectedTo(){
        Node testNode1 = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode1);
        Node testNode2 = new Node("ACONF00103", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode2);
        Node testNode3 = new Node("ACONF00104", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode3);
        Node testNode4 = new Node("ACONF00105", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode4);
        Node testNode5 = new Node("ACONF00106", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        DatabaseService.getDatabaseService().insertNode(testNode5);
        Edge testEdge1 = new Edge("ACONF00102-ACONF00103", testNode1,testNode2);
        DatabaseService.getDatabaseService().insertEdge(testEdge1);
        Edge testEdge2 = new Edge("ACONF00102-ACONF00104", testNode1,testNode3);
        DatabaseService.getDatabaseService().insertEdge(testEdge2);
        Edge testEdge3 = new Edge("ACONF00105-ACONF00102", testNode4,testNode1);
        DatabaseService.getDatabaseService().insertEdge(testEdge3);
        Edge testEdge4 = new Edge("ACONF00105-ACONF00106", testNode4,testNode5);
        DatabaseService.getDatabaseService().insertEdge(testEdge4);
        Edge testEdge5 = new Edge("ACONF00106-ACONF00102", testNode5,testNode1);
        DatabaseService.getDatabaseService().insertEdge(testEdge5);
        ArrayList<Node> connectedNodes = DatabaseService.getDatabaseService().getNodesConnectedTo(testNode1);
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
        DatabaseService.getDatabaseService().insertNode(testNode);
        DatabaseService.getDatabaseService().insertNode(otherNode);
        DatabaseService.getDatabaseService().insertEdge(newEdge);
        Edge gotEdge = DatabaseService.getDatabaseService().getEdge("ACONF00102-ACONF00103");
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
        assertFalse(DatabaseService.getDatabaseService().insertEdge(newEdge));
        DatabaseService.getDatabaseService().insertNode(testNode);
        assertFalse(DatabaseService.getDatabaseService().insertEdge(newEdge));
        DatabaseService.getDatabaseService().insertNode(otherNode);
        assertTrue(DatabaseService.getDatabaseService().insertEdge(newEdge));
    }

    @Test
    @Category(FastTest.class)
    public void updateEdge(){
        // set up the DB
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Node anotherNode = new Node("ACONF00104", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        DatabaseService.getDatabaseService().insertNode(testNode);
        DatabaseService.getDatabaseService().insertNode(otherNode);
        DatabaseService.getDatabaseService().insertEdge(newEdge);
        DatabaseService.getDatabaseService().insertNode(anotherNode);
        // get the edge and confirm its initial values
        Edge gotEdge = DatabaseService.getDatabaseService().getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newEdge.getNode1().getNodeID()));
        assertThat(gotEdge.getNode2().getNodeID(), is(newEdge.getNode2().getNodeID()));
        Edge newerEdge = new Edge("ACONF00102-ACONF00103", testNode, anotherNode);
        // update the values and confirm that they were changed
        assertTrue(DatabaseService.getDatabaseService().updateEdge(newerEdge));
        gotEdge = DatabaseService.getDatabaseService().getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge,is(notNullValue()));
        assertThat(gotEdge.getNode1().getNodeID(), is(newerEdge.getNode1().getNodeID()));



    }

    @Test
    @Category(FastTest.class)
    public void deleteEdge(){
        Node testNode = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        Node otherNode = new Node("ACONF00103", 1648, 2968, "3", "BTM", "CONF", "BTM Conference Center", "BTM Conference");
        Edge newEdge = new Edge("ACONF00102-ACONF00103", testNode, otherNode);
        DatabaseService.getDatabaseService().insertNode(testNode);
        DatabaseService.getDatabaseService().insertNode(otherNode);
        DatabaseService.getDatabaseService().insertEdge(newEdge);
        Edge gotEdge = DatabaseService.getDatabaseService().getEdge("ACONF00102-ACONF00103");
        assertThat(gotEdge.getEdgeID(), is(newEdge.getEdgeID()));
        // delete it
        DatabaseService.getDatabaseService().deleteEdge(gotEdge);
        //make sure that it's not there
        assertThat((DatabaseService.getDatabaseService().getEdge("ACONF00102-ACONF00103")), is(nullValue()));


    }


    // uh i legit don't know how to test this because everything relies on it and we can't delete
    // the tables yet
    @Test
    public void createTables() {
    }

    @Test
    @Category(FastTest.class)
    public void tableExists() {
        assertTrue(DatabaseService.getDatabaseService().tableExists("NODE"));
        assertFalse(DatabaseService.getDatabaseService().tableExists("NOTPRESENT"));


    }

    @Test
    public void getAllEdges() {
    }

    @Test
    @Category(FastTest.class)
    // Test both inserting and getting a reservation
    public void insertAndGetReservation() {
        // Assume an empty DB (ensured by setUp())

        Reservation value, expected;

        // First verify that these reservations are null
        value = DatabaseService.getDatabaseService().getReservation(1);
        assertThat(value, is(nullValue()));

        // Create a reservation
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation1 = new Reservation(0, 0, 23, "Event 0", "None", reservationStart, reservationEnd);

        // successful insert because of constraints
        boolean insertRes = DatabaseService.getDatabaseService().insertReservation(reservation1);
        assertTrue(insertRes);

        // Verify successful get
        expected = reservation1;
        value = DatabaseService.getDatabaseService().getReservation(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllReservations() {
        long now = (new Date()).getTime();
        List<Reservation> reservationList;

        // No reservations should exist yet
        reservationList = DatabaseService.getDatabaseService().getAllReservations();
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
        Reservation res2 = new Reservation(2, 2, 12, "Event 2", "LMNO", res3Start, res3End);

        // Insert two
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res0));
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res1));

        // Check that there are two and only two, and that they are the right two
        reservationList = DatabaseService.getDatabaseService().getAllReservations();
        assertThat(reservationList.size(), is(2));
        assertEquals(res0, reservationList.get(0));
        assertEquals(res1, reservationList.get(1));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res2));

        reservationList = DatabaseService.getDatabaseService().getAllReservations();
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
        Reservation reservation = new Reservation(0, 0, 0, "Event 0", "None", reservationStart, reservationEnd);

        assertTrue(DatabaseService.getDatabaseService().insertReservation(reservation));
        assertEquals(reservation, DatabaseService.getDatabaseService().getReservation(0));

        reservation.setPrivacyLevel(1);
        reservation.setLocationID("ABCD");
        reservationEnd.add(Calendar.MINUTE, 30);
        reservation.setEndTime(reservationEnd);

        assertTrue(DatabaseService.getDatabaseService().updateReservation(reservation));
        assertEquals(reservation, DatabaseService.getDatabaseService().getReservation(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteReservation() {
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation = new Reservation(0, 0, 0, "Event 0", "None", reservationStart, reservationEnd);

        assertTrue(DatabaseService.getDatabaseService().insertReservation(reservation));
        assertEquals(reservation, DatabaseService.getDatabaseService().getReservation(0));

        assertTrue(DatabaseService.getDatabaseService().deleteReservation(reservation));
        assertNull(DatabaseService.getDatabaseService().getReservation(0));
    }

    @Test
    @Category(FastTest.class)
    public void getReservationsBySpaceId() {
        long now = (new Date()).getTime();

        List<Reservation> reservationList;

        // No reservations should exist yet
        reservationList = DatabaseService.getDatabaseService().getAllReservations();
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

        // Insert two
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res0));
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res1));

        // Check that only the res with the ABCD location is retrieved
        reservationList = DatabaseService.getDatabaseService().getReservationsBySpaceId("ABCD");
        assertThat(reservationList.size(), is(1));
        assertEquals(res0, reservationList.get(0));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res2));

        reservationList = DatabaseService.getDatabaseService().getReservationsBySpaceId("ABCD");
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
        reservationList = DatabaseService.getDatabaseService().getAllReservations();
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

        // Insert two
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res0));
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res1));
        assertTrue(DatabaseService.getDatabaseService().insertReservation(res2));

        GregorianCalendar gapStart = new GregorianCalendar();
        GregorianCalendar gapEnd = new GregorianCalendar();
        gapStart.setTime(new Date(now - 6000));
        gapEnd.setTime(new Date(now + 200));

        // Check that only one is retrieved (small time block)
        reservationList = DatabaseService.getDatabaseService().getReservationsBySpaceIdBetween("ABCD", gapStart, gapEnd);
        assertThat(reservationList.size(), is(1));
        assertEquals(res0, reservationList.get(0));

        gapStart.setTime(new Date(now - 1000000));
        gapEnd.setTime(new Date(now + 1100000));

        // Check that both are retrieved (large time block)
        reservationList = DatabaseService.getDatabaseService().getReservationsBySpaceIdBetween("ABCD", gapStart, gapEnd);
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
        value = DatabaseService.getDatabaseService().getEmployee(0);
        assertThat(value, is(nullValue()));

        // Create an employee
        Employee employee = new Employee(0, "Doctor", false, "douglas");

        // Verify successful insertion
        boolean insertRes = DatabaseService.getDatabaseService().insertEmployee(employee);
        assertTrue(insertRes);

        // Verify successful get
        expected = employee;
        value = DatabaseService.getDatabaseService().getEmployee(employee.getID());
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllEmployees() {
        Employee value, expected;

        // First verify that the Employees are null
        value = DatabaseService.getDatabaseService().getEmployee(0);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getEmployee(1);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getEmployee(2);
        assertThat(value, is(nullValue()));

        // Create an employee
        Employee employee1 = new Employee(0, "Doctor", false, "douglas");
        Employee employee2 = new Employee(1, "Nurse", false, "tyler");
        Employee employee3 = new Employee(2, "Admin", true, "joshua");

        // Verify successful insertion
        boolean insertRes = DatabaseService.getDatabaseService().insertEmployee(employee1);
        assertTrue(insertRes);
        insertRes = DatabaseService.getDatabaseService().insertEmployee(employee2);
        assertTrue(insertRes);

        // Check that there are two and only two, and that they are the right two
        List<Employee> employeeList = DatabaseService.getDatabaseService().getAllEmployees();
        assertThat(employeeList.size(), is(2));
        assertEquals(employee1, employeeList.get(0));
        assertEquals(employee2, employeeList.get(1));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertEmployee(employee3));

        employeeList = DatabaseService.getDatabaseService().getAllEmployees();
        assertThat(employeeList.size(), is(3));
        assertEquals(employee1, employeeList.get(0));
        assertEquals(employee2, employeeList.get(1));
        assertEquals(employee3, employeeList.get(2));
    }

    @Test
    @Category(FastTest.class)
    public void updateEmployee() {
        Employee employee = new Employee(0, "Doctor", false, "123456");

        assertTrue(DatabaseService.getDatabaseService().insertEmployee(employee));
        assertEquals(employee, DatabaseService.getDatabaseService().getEmployee(0));

        employee.setAdmin(true);
        employee.setJob("Department head");

        assertTrue(DatabaseService.getDatabaseService().updateEmployee(employee));
        assertEquals(employee, DatabaseService.getDatabaseService().getEmployee(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteEmployee() {
        Employee employee = new Employee(0, "Doctor", false, "password");

        assertTrue(DatabaseService.getDatabaseService().insertEmployee(employee));
        assertEquals(employee, DatabaseService.getDatabaseService().getEmployee(0));

        assertTrue(DatabaseService.getDatabaseService().deleteEmployee(employee));
        assertNull(DatabaseService.getDatabaseService().getEmployee(0));
    }

    @Test
    @Category(FastTest.class)
    public void insertAndGetReservableSpace()  {
        // Assume an empty DB (ensured by setUp())

        ReservableSpace value, expected;

        // First verify that the ReservableSpace is null
        value = DatabaseService.getDatabaseService().getReservableSpace("ABCD");
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
        boolean insertRes = DatabaseService.getDatabaseService().insertReservableSpace(space);
        assertTrue(insertRes);

        // Verify successful get
        expected = space;
        value = DatabaseService.getDatabaseService().getReservableSpace(space.getSpaceID());
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllReservableSpaces() {
        // Assume an empty DB (ensured by setUp())

        ReservableSpace value, expected;

        // First verify that the ReservableSpace is null
        value = DatabaseService.getDatabaseService().getReservableSpace("ABCD");
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getReservableSpace("XYZ");
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getReservableSpace("LMNO");
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
        boolean insertRes = DatabaseService.getDatabaseService().insertReservableSpace(space1);
        assertTrue(insertRes);
        insertRes = DatabaseService.getDatabaseService().insertReservableSpace(space2);
        assertTrue(insertRes);

        // Check that there are two and only two, and that they are the right two
        List<ReservableSpace> allReservableSpaces = DatabaseService.getDatabaseService().getAllReservableSpaces();
        assertThat(allReservableSpaces.size(), is(2));
        assertEquals(space1, allReservableSpaces.get(0));
        assertEquals(space2, allReservableSpaces.get(1));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertReservableSpace(space3));

        allReservableSpaces = DatabaseService.getDatabaseService().getAllReservableSpaces();
        assertThat(allReservableSpaces.size(), is(3));
        assertEquals(space1, allReservableSpaces.get(0));
        assertEquals(space2, allReservableSpaces.get(1));
        assertEquals(space3, allReservableSpaces.get(2));
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

        assertTrue(DatabaseService.getDatabaseService().insertReservableSpace(space));
        assertEquals(space, DatabaseService.getDatabaseService().getReservableSpace("ABCD"));

        space.setSpaceName("Named Room");
        openTime.add(Calendar.MINUTE, -30);
        space.setTimeOpen(openTime);

        assertTrue(DatabaseService.getDatabaseService().updateReservableSpace(space));
        assertEquals(space, DatabaseService.getDatabaseService().getReservableSpace("ABCD"));
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
        value = DatabaseService.getDatabaseService().getITRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req = new ITRequest(0, "No notes", node, false, "New mouse required");

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        boolean insertRes = DatabaseService.getDatabaseService().insertITRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = DatabaseService.getDatabaseService().getITRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllITRequests() {
        // Assume an empty DB (ensured by setUp())

        ITRequest value;

        // First verify that these requests are null
        value = DatabaseService.getDatabaseService().getITRequest(0);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getITRequest(1);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getITRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req1 = new ITRequest(0, "No notes", node, false, "New mouse required");
        ITRequest req2 = new ITRequest(1, "Priority", node, true, "No internet");
        ITRequest req3 = new ITRequest(2, "Notes go here", node, false, "Help me");

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req1));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req2));

        req1.setId(0);
        req2.setId(1);

        // Check that there are two and only two, and that they are the right two
        List<ITRequest> allITRequests = DatabaseService.getDatabaseService().getAllITRequests();
        assertThat(allITRequests.size(), is(2));
        assertEquals(req1, allITRequests.get(0));
        assertEquals(req2, allITRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req3));

        req3.setId(2);

        allITRequests = DatabaseService.getDatabaseService().getAllITRequests();
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

        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getITRequest(0));

        req.setDescription("Two new mouses needed");
        req.setCompleted(true);

        assertTrue(DatabaseService.getDatabaseService().updateITRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getITRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteITRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req = new ITRequest(0, "No notes", node, false, "New mouse required");

        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getITRequest(0));

        assertTrue(DatabaseService.getDatabaseService().deleteITRequest(req));
        assertNull(DatabaseService.getDatabaseService().getITRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteITRequests() {
        // Assume an empty DB (ensured by setUp())

        ITRequest value;

        // First verify that these requests are null
        value = DatabaseService.getDatabaseService().getITRequest(0);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getITRequest(1);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getITRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        ITRequest req1 = new ITRequest(0, "No notes", node, false, "New mouse required");
        ITRequest req2 = new ITRequest(1, "Priority", node, true, "No internet");
        ITRequest req3 = new ITRequest(2, "Notes go here", node, false, "Help me");

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req1));
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<ITRequest> allITRequests = DatabaseService.getDatabaseService().getAllIncompleteITRequests();
        assertThat(allITRequests.size(), is(1));
        assertEquals(req1, allITRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertITRequest(req3));

        allITRequests = DatabaseService.getDatabaseService().getAllIncompleteITRequests();
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
        value = DatabaseService.getDatabaseService().getMedicineRequest(0);
        assertThat(value, is(nullValue()));

        // Create a request
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req = new MedicineRequest(0, "Quickly please", node, false, "Ibuprofen", 2.5);

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        boolean insertRes = DatabaseService.getDatabaseService().insertMedicineRequest(req);
        assertTrue(insertRes);

        // Verify successful get
        expected = req;
        value = DatabaseService.getDatabaseService().getMedicineRequest(0);
        assertEquals(expected, value);
    }

    @Test
    @Category(FastTest.class)
    public void getAllMedicineRequests() {
        // Assume an empty DB (ensured by setUp())

        MedicineRequest value;

        // First verify that these requests are null
        value = DatabaseService.getDatabaseService().getMedicineRequest(0);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getMedicineRequest(1);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getMedicineRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req1 = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);
        MedicineRequest req2 = new MedicineRequest(1, "Priority", node, true, "Asprin", 10);
        MedicineRequest req3 = new MedicineRequest(2, "Notes go here", node, false, "Some other medicine", 1);

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req1));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MedicineRequest> allMedicineRequests = DatabaseService.getDatabaseService().getAllMedicineRequests();
        assertThat(allMedicineRequests.size(), is(2));
        assertEquals(req1, allMedicineRequests.get(0));
        assertEquals(req2, allMedicineRequests.get(1));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req3));

        allMedicineRequests = DatabaseService.getDatabaseService().getAllMedicineRequests();
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

        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getMedicineRequest(0));

        req.setNotes("Capsules");
        req.setQuantity(5.333);

        assertTrue(DatabaseService.getDatabaseService().updateMedicineRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getMedicineRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void deleteMedicineRequest() {
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);

        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req));
        assertEquals(req, DatabaseService.getDatabaseService().getMedicineRequest(0));

        assertTrue(DatabaseService.getDatabaseService().deleteMedicineRequest(req));
        assertNull(DatabaseService.getDatabaseService().getMedicineRequest(0));
    }

    @Test
    @Category(FastTest.class)
    public void getAllIncompleteMedicineRequests() {
        // Assume an empty DB (ensured by setUp())

        MedicineRequest value;

        // First verify that these requests are null
        value = DatabaseService.getDatabaseService().getMedicineRequest(0);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getMedicineRequest(1);
        assertThat(value, is(nullValue()));
        value = DatabaseService.getDatabaseService().getMedicineRequest(2);
        assertThat(value, is(nullValue()));


        // Create a some requests - don't care about node, so all the same
        Node node = new Node("ACONF00102", 1580, 2538, "2", "BTM", "HALL", "Hall", "Hall");
        MedicineRequest req1 = new MedicineRequest(0, "No notes", node, false, "Ibuprofen", 3.75);
        MedicineRequest req2 = new MedicineRequest(1, "Priority", node, true, "Asprin", 10);
        MedicineRequest req3 = new MedicineRequest(2, "Notes go here", node, false, "Some other medicine", 1);

        // Verify successful insertion
        assertTrue(DatabaseService.getDatabaseService().insertNode(node));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req1));
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req2));

        // Check that there are two and only two, and that they are the right two
        List<MedicineRequest> allMedicineRequests = DatabaseService.getDatabaseService().getAllIncompleteMedicineRequests();
        assertThat(allMedicineRequests.size(), is(1));
        assertEquals(req1, allMedicineRequests.get(0));

        // Insert #3, and rerun checks
        assertTrue(DatabaseService.getDatabaseService().insertMedicineRequest(req3));

        allMedicineRequests = DatabaseService.getDatabaseService().getAllIncompleteMedicineRequests();
        assertThat(allMedicineRequests.size(), is(2));
        assertEquals(req1, allMedicineRequests.get(0));
        assertEquals(req3, allMedicineRequests.get(1));
    }
}