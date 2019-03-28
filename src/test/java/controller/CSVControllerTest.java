package controller;

import model.Node;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class CSVControllerTest {

    @Test
    public void loadNodes() {
        try {
            // create the nodes
            ArrayList<Node> nodes = (ArrayList<Node>) CSVController.loadNodes("nodes.csv");
            // test first row
            assertEquals("ACONF00102", nodes.get(0).getNodeID());
            assertEquals(1580, nodes.get(0).getXcoord());
            assertEquals(2538, nodes.get(0).getYcoord());
            assertEquals("2", nodes.get(0).getFloor());
            assertEquals("BTM", nodes.get(0).getBuilding());
            assertEquals("HALL", nodes.get(0).getNodeType());
            assertEquals("Hall", nodes.get(0).getLongName());
            assertEquals("Hall", nodes.get(0).getShortName());
            // test random info
            assertEquals("Department C003L1", nodes.get(120).getShortName());
            // testing size
            assertEquals(581, nodes.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
