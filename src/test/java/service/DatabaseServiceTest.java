package service;

import model.*;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        value = myDBS.getReservation(23);
        assertThat(value, is(nullValue()));

        // Create a reservation
        GregorianCalendar reservationStart = new GregorianCalendar();
        reservationStart.setTime(new Date());
        GregorianCalendar reservationEnd = new GregorianCalendar();
        reservationEnd.setTime(new Date());
        reservationEnd.add(Calendar.HOUR, 1);
        Reservation reservation1 = new Reservation(0, 0, 23, "Event 0", "None", reservationStart, reservationEnd);

        // This insertion will fail because there exists no employee zero in the system to make the reservation
       // boolean insertRes = myDBS.insertReservation(reservation1);
       // assertFalse(insertRes);

        // Insert Employee in to make sure the reservation will work
        //Employee employee = new Employee(23, "Doctor", false);
        //myDBS.insertEmployee(employee);

        // successful insert because of constraints
        boolean insertRes = myDBS.insertReservation(reservation1);
        assertTrue(insertRes);




        // Verify successful get
        expected = reservation1;
        value = myDBS.getReservation(0);
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
    public void updateReservation() {
    }

    @Test
    public void deleteReservation() {
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

        // Insert two
        assertTrue(myDBS.insertReservation(res0));
        assertTrue(myDBS.insertReservation(res1));


        // Check that only one is retrieved (small time block)
        reservationList = myDBS.getReservationBySpaceIdBetween("ABCD", new Date(now - 6000), new Date(now + 200));
        assertThat(reservationList.size(), is(1));
        assertEquals(res0, reservationList.get(0));

        // Check that both are retrieved (large time block)
        reservationList = myDBS.getReservationBySpaceIdBetween("ABCD", new Date(now - 1000000), new Date(now + 1100000));
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
        Employee employee = new Employee(0, "Doctor", false, "douglas");

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
        Employee employee1 = new Employee(0, "Doctor", false, "douglas");
        Employee employee2 = new Employee(1, "Nurse", false, "tyler");
        Employee employee3 = new Employee(2, "Admin", true, "joshua");

        // Verify successful insertion
        boolean insertRes = myDBS.insertEmployee(employee1);
        assertTrue(insertRes);
        insertRes = myDBS.insertEmployee(employee2);
        assertTrue(insertRes);

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
    public void updateEmployee() {
    }

    @Test
    public void deleteEmployee() {
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
    public void updateReservableSpace() {
    }

    @Test
    public void deleteReservableSpace() {
    }

    @Test
    public void insertITRequest() {
    }

    @Test
    public void getITRequest() {
    }

    @Test
    public void getAllITRequests() {
    }

    @Test
    public void updateITRequest() {
    }

    @Test
    public void deleteITRequest() {
    }

    @Test
    public void getAllIncompleteITRequests() {
    }

    @Test
    public void insertMedicineRequest() {
    }

    @Test
    public void getMedicineRequest() {
    }

    @Test
    public void getAllMedicineRequests() {
    }

    @Test
    public void updateMedicineRequest() {
    }

    @Test
    public void deleteMedicineRequest() {
    }

    @Test
    public void getAllIncompleteMedicineRequests() {
    }
}