package database;

import database.CSVService;
import database.DatabaseService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import employee.model.Employee;
import employee.model.JobType;
import map.Edge;
import map.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import scheduler.model.ReservableSpace;
import service.ResourceLoader;
import testclassifications.FastTest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CSVServiceTest {

    private ArrayList<Node> testNodes;
    private ArrayList<Edge> testEdges;
    private ArrayList<ReservableSpace> testSpaces;
    private ArrayList<Employee> testEmployees;

    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);

        testNodes = new ArrayList<>();
        testEdges = new ArrayList<>();
        testSpaces = new ArrayList<>();
        testEmployees = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date1 = sdf.parse("2019-03-31 12:00");
        Date date2 = sdf.parse("2019-03-31 12:30");
        Date date3 = sdf.parse("2019-03-25 14:00");
        Date date4 = sdf.parse("2019-03-25 15:30");
        Date date5 = sdf.parse("2019-04-20 00:00");
        Date date6 = sdf.parse("2019-04-20 23:59");

        GregorianCalendar calendar1 = new GregorianCalendar();
        GregorianCalendar calendar2 = new GregorianCalendar();
        GregorianCalendar calendar3 = new GregorianCalendar();
        GregorianCalendar calendar4 = new GregorianCalendar();
        GregorianCalendar calendar5 = new GregorianCalendar();
        GregorianCalendar calendar6 = new GregorianCalendar();

        Employee emp1 = new Employee(1,"ww", JobType.DOCTOR,true,"wong");
        Employee emp2 = new Employee(2,"dan", JobType.NURSE,false,"duff");
        Employee emp3 = new Employee(3,"bennett", JobType.NURSE,false,"bennett");

        calendar1.setTime(date1);
        calendar2.setTime(date2);
        calendar3.setTime(date3);
        calendar4.setTime(date4);
        calendar5.setTime(date5);
        calendar6.setTime(date6);

        ReservableSpace space1 = new ReservableSpace("AAAAA00101","Bob","Computer","BBBBB00101", calendar1, calendar2);
        ReservableSpace space2 = new ReservableSpace("AAAAA00102","Alice","Conference","BBBBB00102",calendar3, calendar4);
        ReservableSpace space3 = new ReservableSpace("AAAAA00103","John","Computer","BBBBB00103",calendar5,calendar6);

        Node n1 = new Node("ABC123", 0, 0, "1", "Main", "ABC", "Test Node 1", "T1");
        Node n2 = new Node("XYZ4242", 50, 50, "L1", "Aux", "XYZ", "Test Node 2", "T2");
        Node n3 = new Node("LMNO123", 0, 50, "G", "Main", "LMNO", "Test Node 3", "T3");

        Edge e1 = new Edge(n1, n2);
        Edge e2 = new Edge(n2, n3);
        Edge e3 = new Edge(n1, n3);

        testNodes.add(n1);
        testNodes.add(n2);
        testNodes.add(n3);
        testEdges.add(e1);
        testEdges.add(e2);
        testEdges.add(e3);
        testSpaces.add(space1);
        testSpaces.add(space2);
        testSpaces.add(space3);
        testEmployees.add(emp1);
        testEmployees.add(emp2);
        testEmployees.add(emp3);

        when(dbs.getAllNodes()).thenReturn(testNodes);
        when(dbs.getAllEdges()).thenReturn(testEdges);
        when(dbs.getAllReservableSpaces()).thenReturn(testSpaces);
        when(dbs.getAllEmployees()).thenReturn(testEmployees);

        when(dbs.getNode(n1.getNodeID())).thenReturn(n1);
        when(dbs.getNode(n2.getNodeID())).thenReturn(n2);
        when(dbs.getNode(n3.getNodeID())).thenReturn(n3);

        when(dbs.getReservableSpace(space1.getSpaceID())).thenReturn(space1);
        when(dbs.getReservableSpace(space2.getSpaceID())).thenReturn(space2);
        when(dbs.getReservableSpace(space3.getSpaceID())).thenReturn(space3);
        when(dbs.getEmployee(emp1.getID())).thenReturn(emp1);
        when(dbs.getEmployee(emp2.getID())).thenReturn(emp2);
        when(dbs.getEmployee(emp3.getID())).thenReturn(emp3);

        CSVService.myDBS = dbs;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Category(FastTest.class)
    public void exportNodes() throws IOException {
        // Precondition: Check that ./nodes.csv does not exist
        File tempfile = new File("nodes.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVController.exportNodes
        CSVService.exportNodes();
        // Assert that export nodes has the correct content
        File nodecsv = new File("nodes.csv");
        assertTrue(nodecsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("nodes.csv"), StandardCharsets.UTF_8));

        StringBuffer fileContents = new StringBuffer();
        String line = reader.readLine();
        while(line != null){
            fileContents.append(line);
            fileContents.append("\n");
            line = reader.readLine();
        }

        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();

            if (reader != null) {
                reader.close();
            }
        }

        String expectedValue = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName" + "\n"
                + "ABC123,0,0,1,Main,ABC,Test Node 1,T1\n"
                + "XYZ4242,50,50,L1,Aux,XYZ,Test Node 2,T2\n"
                + "LMNO123,0,50,G,Main,LMNO,Test Node 3,T3\n";

        assertThat(fileContents.toString(), is(expectedValue));

        File file = new File("nodes.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    @Category(FastTest.class)
    public void exportEdges() throws IOException{
        // Precondition: Check that ./edges.csv does not exist
        File tempfile = new File("edges.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVController.exportEdges
        CSVService.exportEdges();

        // Assert that export nodes has the correct content
        File edgecsv = new File("edges.csv");
        assertTrue(edgecsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("edges.csv"), StandardCharsets.UTF_8));

        StringBuffer fileContents = new StringBuffer();
        String line = reader.readLine();
        while(line != null){
            fileContents.append(line);
            fileContents.append("\n");
            line = reader.readLine();
        }

        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();

            if (reader != null) {
                reader.close();
            }
        }

        String expectedValue = "edgeID,startNode,endNode" + "\n"
                + "ABC123XYZ4242,ABC123,XYZ4242\n"
                + "XYZ4242LMNO123,XYZ4242,LMNO123\n"
                + "ABC123LMNO123,ABC123,LMNO123\n";

        assertThat(fileContents.toString(), is(expectedValue));

        File file = new File("edges.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    @Category(FastTest.class)
    public void exportEmployees() throws IOException{
        // Precondition: Check that ./employees.csv does not exist
        File tempfile = new File("employees.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVController.exportEmployees
        CSVService.exportEmployees();

        // Assert that export employees has the correct content
        File empcsv = new File("employees.csv");
        assertTrue(empcsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("employees.csv"), StandardCharsets.UTF_8));

        StringBuffer fileContents = new StringBuffer();
        String line = reader.readLine();
        while(line != null){
            fileContents.append(line);
            fileContents.append("\n");
            line = reader.readLine();
        }

        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();

            if (reader != null) {
                reader.close();
            }
        }

        String expectedValue = "ID,username,job,isAdmin,password" + "\n"
                + "1,ww,DOCTOR,true,wong\n"
                + "2,dan,NURSE,false,duff\n"
                + "3,bennett,NURSE,false,bennett\n";

        assertThat(fileContents.toString(), is(expectedValue));

        File file = new File("employees.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    @Category(FastTest.class)
    public void exportReservableSpaces() throws IOException{
        // Precondition: Check that ./reservablespaces.csv does not exist
        File tempfile = new File("reservablespaces.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVService.exportReservableSpaces
        CSVService.exportReservableSpaces();

        // Assert that export employees has the correct content
        File spacecsv = new File("reservablespaces.csv");
        assertTrue(spacecsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("reservablespaces.csv"), StandardCharsets.UTF_8));

        StringBuffer fileContents = new StringBuffer();
        String line = reader.readLine();
        while(line != null){
            fileContents.append(line);
            fileContents.append("\n");
            line = reader.readLine();
        }

        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();

            if (reader != null) {
                reader.close();
            }
        }

        String expectedValue = "spaceID,spaceName,spaceType,locationNodeID,timeOpen,timeClosed" + "\n"
                + "AAAAA00101,Bob,Computer,BBBBB00101,2019-03-31 12:00,2019-03-31 12:30\n"
                + "AAAAA00102,Alice,Conference,BBBBB00102,2019-03-25 14:00,2019-03-25 15:30\n"
                + "AAAAA00103,John,Computer,BBBBB00103,2019-04-20 00:00,2019-04-20 23:59\n";

        assertThat(fileContents.toString(), is(expectedValue));

        File file = new File("reservablespaces.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    @Category(FastTest.class)
    // Warning: this test contains large amounts of black magic
    public void importNodes() throws Exception {
        URL originalURL = ResourceLoader.nodes;
        // Override the csv file
        setFinalStatic(ResourceLoader.class.getDeclaredField("nodes"), ResourceLoader.class.getResource("/test_nodes.csv"));

        // Create a class to capture arguments of the type Node
        ArgumentCaptor<ArrayList<Node>> nodeCaptor = ArgumentCaptor.forClass(ArrayList.class);

        // Action being tested
        CSVService.importNodes();

        // Capture the calls to insert node
        verify(CSVService.myDBS, times(1)).insertAllNodes(nodeCaptor.capture());

        // Check that each node captured is equal to the test nodes
        List<ArrayList<Node>> capturedNodes = nodeCaptor.getAllValues();
        assertEquals(testNodes, capturedNodes.get(0));

        // Reset to original URL
        setFinalStatic(ResourceLoader.class.getDeclaredField("nodes"), originalURL);
    }

    @Test
    @Category(FastTest.class)
    public void importEdges() throws Exception {
        URL originalURL = ResourceLoader.edges;
        // Override the csv file
        setFinalStatic(ResourceLoader.class.getDeclaredField("edges"), service.ResourceLoader.class.getResource("/test_edges.csv"));

        // Create a class to capture arguments of the type Edge
        ArgumentCaptor<Edge> edgeCaptor = ArgumentCaptor.forClass(Edge.class);

        // Action being tested
        CSVService.importEdges();

        // Capture the calls to insert edge
        verify(CSVService.myDBS, times(3)).insertEdge(edgeCaptor.capture());

        // Check that each edge captured is equal to the test edge
        List<Edge> capturedEdges = edgeCaptor.getAllValues();
        assertEquals(testEdges.get(0), capturedEdges.get(0));
        assertEquals(testEdges.get(1), capturedEdges.get(1));
        assertEquals(testEdges.get(2), capturedEdges.get(2));

        // Reset to original URL
        setFinalStatic(ResourceLoader.class.getDeclaredField("edges"), originalURL);
    }

    @Test
    @Category(FastTest.class)
    public void importReservableSpaces() throws Exception {

        URL originalURL = ResourceLoader.nodes;
        // Override the csv file
        setFinalStatic(ResourceLoader.class.getDeclaredField("reservablespaces"), service.ResourceLoader.class.getResource("/test_reservablespaces.csv"));

        // Create a class to capture arguments of the type ReservableSpace
        ArgumentCaptor<ReservableSpace> spaceCaptor = ArgumentCaptor.forClass(ReservableSpace.class);

        // Action being tested
        CSVService.importReservableSpaces();

        // Capture the calls to insert spaces
        verify(CSVService.myDBS, times(3)).insertReservableSpace(spaceCaptor.capture());

        // Check that each node captured is equal to the test spaces
        List<ReservableSpace> capturedSpaces = spaceCaptor.getAllValues();
        assertEquals(testSpaces.get(0), capturedSpaces.get(0));
        assertEquals(testSpaces.get(1), capturedSpaces.get(1));
        assertEquals(testSpaces.get(2), capturedSpaces.get(2));

        // Reset to original URL
        setFinalStatic(ResourceLoader.class.getDeclaredField("reservablespaces"), originalURL);
    }

    @Test
    @Category(FastTest.class)
    public void importEmployees() throws Exception {

        URL originalURL = ResourceLoader.employees;
        // Override the csv file
        setFinalStatic(ResourceLoader.class.getDeclaredField("employees"), service.ResourceLoader.class.getResource("/test_employees.csv"));

        // Create a class to capture arguments of the type Employee
        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);

        // Action being tested
        CSVService.importEmployees();
        // Capture the calls to insert employees
        verify(CSVService.myDBS, times(3)).insertEmployee(empCaptor.capture());

        // Check that each node captured is equal to the test employee
        List<Employee> capturedEmp = empCaptor.getAllValues();
        assertEquals(testEmployees.get(0), capturedEmp.get(0));
        assertEquals(testEmployees.get(1), capturedEmp.get(1));
        assertEquals(testEmployees.get(2), capturedEmp.get(2));

        // Reset to original URL
        setFinalStatic(ResourceLoader.class.getDeclaredField("employees"), originalURL);

    }

    // DANGER! Use wisely!
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
