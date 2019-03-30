package controller;

import model.Edge;
import model.Node;

import java.io.*;

public class CSVController extends Controller {

    public static final String NODE_EXPORT_PATH = "nodes.csv";
    public static final String EDGE_EXPORT_PATH = "edges.csv";

    private static final String NODE_HEADER = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName\n";
    private static final String EDGES_HEADER = "edgeID,startNode,endNode\n";

    /**
     * Export the entire database into CSV format
     */
    public static void exportDatabase() {
    }

    /**
     * Import the entire database from CSVs into the database
     */
    public static void importDatabase() {
    }

    /**
     * Export the Nodes table
     */
    public static void exportNodes() throws IOException {
        // Open a file
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(NODE_EXPORT_PATH), "UTF-8");;
            writer.write(NODE_HEADER);

            // Write out each node
            for (Node node : dbs.getAllNodes()) {
                writer.write(node.getNodeID() + ",");
                writer.write(node.getXcoord() + ",");
                writer.write(node.getYcoord() + ",");
                writer.write(node.getFloor() + ",");
                writer.write(node.getBuilding() + ",");
                writer.write(node.getNodeType() + ",");
                writer.write(node.getLongName() + ",");
                writer.write(node.getShortName() + "\n");
            }

            // Close the writer
            writer.close();
        } catch (IOException e) {
            // Cleanup the writer if possible
            if (writer != null) {
                writer.close();
            }

            // Throw the error so upstream can handle it
            throw e;
        }
    }

    /**
     * Export the Edges table
     */
    public static void exportEdges() throws IOException {
        // Open a file
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(EDGE_EXPORT_PATH), "UTF-8");;
            writer.write(EDGES_HEADER);

            // Write out each node
            for (Edge edge : dbs.getAllEdges()) {
                writer.write(edge.getEdgeID() + ",");
                writer.write(edge.getNode1().getNodeID() + ",");
                writer.write(edge.getNode2().getNodeID() + "\n");
            }

            // Close the writer
            writer.close();
        } catch (IOException e) {
            // Cleanup the writer if possible
            if (writer != null) {
                writer.close();
            }

            // Throw the error so upstream can handle it
            throw e;
        }
    }

    /**
     * Export the Requests table
     */
    public static void exportRequests() {
    }

    /**
     * Import the Nodes table
     */
    public static void importNodes() {
    }

    /**
     * Import the Edges table
     */
    public static void importEdges() {
    }

    /**
     * Import the Requests table
     */
    public static void importRequests() {
    }
}
