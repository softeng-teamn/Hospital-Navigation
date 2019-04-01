package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.Node;
import model.Edge;
import model.ReservableSpace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import service.DatabaseService;
import service.ResourceLoader;
import testclassifications.FastTest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CSVControllerTest {

    private ArrayList<Node> testNodes;
    private ArrayList<Edge> testEdges;
    private ArrayList<ReservableSpace> testSpaces;

    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);

        testNodes = new ArrayList<>();
        testEdges = new ArrayList<>();
        testSpaces = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm");

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

        calendar1.setTime(date1);
        calendar2.setTime(date2);
        calendar3.setTime(date3);
        calendar4.setTime(date4);
        calendar5.setTime(date5);
        calendar6.setTime(date6);

        Node n1 = new Node("ABC123", 0, 0, "1", "Main", "ABC", "Test Node 1", "T1");
        Node n2 = new Node("XYZ4242", 50, 50, "L1", "Aux", "XYZ", "Test Node 2", "T2");
        Node n3 = new Node("LMNO123", 0, 50, "G", "Main", "LMNO", "Test Node 3", "T3");

        Edge e1 = new Edge(n1, n2);
        Edge e2 = new Edge(n2, n3);
        Edge e3 = new Edge(n1, n3);

        ReservableSpace space1 = new ReservableSpace("AAAAA00101","Bob","Computer","BBBBB00101", calendar1, calendar2);
        ReservableSpace space2 = new ReservableSpace("AAAAA00102","Alice","Conference","BBBBB00102",calendar3, calendar4);
        ReservableSpace space3 = new ReservableSpace("AAAAA00103","John","Computer","BBBBB00103",calendar5,calendar6);

        testNodes.add(n1);
        testNodes.add(n2);
        testNodes.add(n3);
        testEdges.add(e1);
        testEdges.add(e2);
        testEdges.add(e3);

        when(dbs.getAllNodes()).thenReturn(testNodes);
        when(dbs.getAllEdges()).thenReturn(testEdges);

        when(dbs.getNode(n1.getNodeID())).thenReturn(n1);
        when(dbs.getNode(n2.getNodeID())).thenReturn(n2);
        when(dbs.getNode(n3.getNodeID())).thenReturn(n3);

        CSVController.dbs = dbs;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void exportDatabase() {
    }

    @Test
    public void importDatabase() {
    }

    @Test
    @Category(FastTest.class)
    public void exportNodes() throws IOException {
        // Precondition: Check that ./nodes.csv does not exist
        File tempfile = new File("./nodes.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVController.exportNodes
        CSVController.exportNodes();
        // Assert that export nodes has the correct content
        File nodecsv = new File("./nodes.csv");
        assertTrue(nodecsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("./nodes.csv"), StandardCharsets.UTF_8));

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

        File file = new File("./nodes.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    @Category(FastTest.class)
    public void exportEdges() throws IOException{
        // Precondition: Check that ./edges.csv does not exist
        File tempfile = new File("./edges.csv");
        assertFalse(tempfile.exists());

        // Action: call CSVController.exportEdges
        CSVController.exportEdges();

        // Assert that export nodes has the correct content
        File edgecsv = new File("./edges.csv");
        assertTrue(edgecsv.exists());
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream("./edges.csv"), StandardCharsets.UTF_8));

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

        File file = new File("./edges.csv");
        assertThat(file.delete(), is(true));
    }

    @Test
    public void exportReservableSpaces() {
    }

    @Test
    @Category(FastTest.class)
    // Warning: this test contains large amounts of black magic
    public void importNodes() throws Exception {
        URL originalURL = ResourceLoader.nodes;
        // Override the csv file
        setFinalStatic(ResourceLoader.class.getDeclaredField("nodes"), service.ResourceLoader.class.getResource("/test_nodes.csv"));

        // Create a class to capture arguments of the type Node
        ArgumentCaptor<Node> nodeCaptor = ArgumentCaptor.forClass(Node.class);

        // Action being tested
        CSVController.importNodes();

        // Capture the calls to insert node
        verify(CSVController.dbs, times(3)).insertNode(nodeCaptor.capture());

        // Check that each node captured is equal to the test nodes
        List<Node> capturedNodes = nodeCaptor.getAllValues();
        assertEquals(testNodes.get(0), capturedNodes.get(0));
        assertEquals(testNodes.get(1), capturedNodes.get(1));
        assertEquals(testNodes.get(2), capturedNodes.get(2));

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
        CSVController.importEdges();

        // Capture the calls to insert edge
        verify(CSVController.dbs, times(3)).insertEdge(edgeCaptor.capture());

        // Check that each edge captured is equal to the test edge
        List<Edge> capturedEdges = edgeCaptor.getAllValues();
        assertEquals(testEdges.get(0), capturedEdges.get(0));
        assertEquals(testEdges.get(1), capturedEdges.get(1));
        assertEquals(testEdges.get(2), capturedEdges.get(2));

        // Reset to original URL
        setFinalStatic(ResourceLoader.class.getDeclaredField("edges"), originalURL);
    }

    @Test
    public void importReservableSpaces() {
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
