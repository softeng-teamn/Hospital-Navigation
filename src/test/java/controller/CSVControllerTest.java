package controller;

import model.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import org.junit.experimental.categories.Category;
import service.DatabaseService;
import testclassifications.FastTest;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVControllerTest {

    private ArrayList<Node> testNodes;

    @Before
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);

        testNodes = new ArrayList<>();

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

        testNodes.add(n1);
        testNodes.add(n2);
        testNodes.add(n3);

        when(dbs.getAllNodes()).thenReturn(testNodes);
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
        BufferedReader reader = new BufferedReader(new FileReader("./nodes.csv"));

        String fileContents = "";
        String line = reader.readLine();
        while(line != null){
            fileContents += line + "\n";
            line = reader.readLine();
        }
        String expectedValue = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName" + "\n"
                + "ABC123,0,0,1,Main,ABC,Test Node 1,T1\n"
                + "XYZ4242,50,50,L1,Aux,XYZ,Test Node 2,T2\n"
                + "LMNO123,0,50,G,Main,LMNO,Test Node 3,T3\n";

        assertThat(fileContents, is(expectedValue));
    }

    @Test
    public void exportEdges() {
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
