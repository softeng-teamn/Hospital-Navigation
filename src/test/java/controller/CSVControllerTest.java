package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.Node;
import model.Edge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import org.junit.experimental.categories.Category;
import service.DatabaseService;
import testclassifications.FastTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVControllerTest {

    private ArrayList<Node> testNodes;
    private ArrayList<Edge> testEdges;

    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);

        testNodes = new ArrayList<>();
        testEdges = new ArrayList<>();

        Node n1 = new Node(0, 0);
        n1.setNodeID("ABC123");
        n1.setFloor("1");
        n1.setBuilding("Main");
        n1.setNodeType("ABC");
        n1.setShortName("T1");
        n1.setLongName("Test Node 1");

        Node n2 = new Node(50, 50);
        n2.setNodeID("XYZ4242");
        n2.setFloor("L1");
        n2.setBuilding("Aux");
        n2.setNodeType("XYZ");
        n2.setShortName("T2");
        n2.setLongName("Test Node 2");

        Node n3 = new Node(0, 50);
        n3.setNodeID("LMNO123");
        n3.setFloor("G");
        n3.setBuilding("Main");
        n3.setNodeType("LMNO");
        n3.setShortName("T3");
        n3.setLongName("Test Node 3");

        Edge e1 = new Edge(n1, n2);
        Edge e2 = new Edge(n2, n3);
        Edge e3 = new Edge(n1, n3);

        testNodes.add(n1);
        testNodes.add(n2);
        testNodes.add(n3);
        testEdges.add(e1);
        testEdges.add(e2);
        testEdges.add(e3);

        when(dbs.getAllNodes()).thenReturn(testNodes);
        when(dbs.getAllEdges()).thenReturn(testEdges);

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
    public void exportRequests() {
    }

    @Test
    public void importNodes() {
    }

    @Test
    public void importEdges() {
    }

    @Test
    public void importRequests() {
    }
}
