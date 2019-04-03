package controller;

import model.Edge;
import model.Employee;
import model.Node;
import model.ReservableSpace;
import service.ResourceLoader;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Date;

public class CSVController extends Controller {

    public static final String NODE_EXPORT_PATH = "nodes.csv";
    public static final String EDGE_EXPORT_PATH = "edges.csv";
    public static final String SPACE_EXPORT_PATH = "reservablespaces.csv";

    private static final String NODE_HEADER = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName\n";
    private static final String EDGES_HEADER = "edgeID,startNode,endNode\n";
    private static final String SPACE_HEADER = "spaceID,spaceName,spaceType,locationNodeID,timeOpen,timeClosed\n";

    /**
     * Export the Nodes table
     */
    public static void exportNodes() throws IOException {
        // Open a file
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(NODE_EXPORT_PATH), "UTF-8");

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
            writer = new OutputStreamWriter(new FileOutputStream(EDGE_EXPORT_PATH), "UTF-8");
            ;
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
     * Export the ReservableSpace table
     */
    public static void exportReservableSpaces() {
        // Open a file
        Writer writer = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            writer = new OutputStreamWriter(new FileOutputStream(SPACE_EXPORT_PATH), "UTF-8");
            //Write header
            writer.write(SPACE_HEADER);

            // Write out each space
            for (ReservableSpace space : dbs.getAllReservableSpaces()) {
                writer.write(space.getSpaceID() + ",");
                writer.write(space.getSpaceName() + ",");
                writer.write(space.getSpaceType() + ",");
                writer.write(space.getLocationNodeID() + ",");
                writer.write(sdf.format(space.getTimeOpen().getTime())+ ",");
                writer.write(sdf.format(space.getTimeClosed().getTime()) + "\n");
            }

            // Close the writer
            writer.close();
        } catch (IOException e) {
            // Cleanup the writer if possible
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    /**
     * Import the Nodes table
     */
    public static void importNodes() {
        BufferedReader reader = null;

        ArrayList<Node> nodes = new ArrayList<>();

        try {
            //load file to be read
            reader = new BufferedReader(new InputStreamReader(ResourceLoader.nodes.openStream(), "UTF-8"));

            //read first line
            reader.readLine();

            String line = null;
            //loop until there is nothing to read
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                //Create node and populate it with data
                Node node = new Node(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), data[3], data[4], data[5], data[6], data[7]);

                //insert node into list
                nodes.add(node);
            }

            dbs.insertAllNodes(nodes);

            //close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            //clean up reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Import the Edges table
     */
    public static void importEdges() {

        BufferedReader reader = null;

        try {
            //load file to read
            reader = new BufferedReader(new InputStreamReader(ResourceLoader.edges.openStream(), "UTF-8"));

            //read first line
            reader.readLine();

            String line = null;

            //loop until there is nothing to read
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                //retrieve nodes from database based on ID
                Node node1 = dbs.getNode(data[1]);
                Node node2 = dbs.getNode(data[2]);

                //checks to see if nodes are not null before creating and adding an edge
                if ((node1 != null) && (node2 != null)) {
                    //Create edge and populate it with two nodes
                    Edge edge = new Edge(node1, node2);

                    //Add edge to the database
                    dbs.insertEdge(edge);
                } else {
                    //Print out error statement
                    System.out.println("Invalid Edge Found: " + line);
                }
            }

            //close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            //clean up reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * Import the ReservableSpace table
     */
    public static void importReservableSpaces() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(ResourceLoader.reservablespaces.openStream(), "UTF-8"));

            //read first line
            reader.readLine();

            String line = null;
            //loop until there is nothing to read
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                //create calendars for ReservableSpace object
                GregorianCalendar openCalender = new GregorianCalendar();
                GregorianCalendar closedCalender = new GregorianCalendar();

                //create date to later add to a calender
                Date openDate = null;
                Date closedDate = null;

                //create simpledateformat to be used for parsing
                //If date format changes, change pattern
                SimpleDateFormat simpleDateFormatOpen = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat simpleDateFormatClosed = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                try {
                    //set parse string from csv and set the date
                    openDate = simpleDateFormatOpen.parse(data[4]);
                    closedDate = simpleDateFormatClosed.parse(data[5]);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (openDate != null && closedDate != null) {
                    //set time(date) for calendars
                    openCalender.setTime(openDate);
                    closedCalender.setTime(closedDate);


                    //Create space and populate it with data
                    ReservableSpace space = new ReservableSpace(data[0], data[1], data[2], data[3], openCalender, closedCalender);

                    //insert space into database
                    dbs.insertReservableSpace(space);
                } else {
                    System.out.println("Invalid Time Found: " + line);
                }

            }
            //close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            //clean up reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Import CSVs if dbs was just created.
     */
    public static void importIfNecessary() {
        if (dbs.isNewlyCreated()) {
            Employee newEmployee = new Employee(1234, "Admin", true, "test");
            importNodes();
            importEdges();
            dbs.insertEmployee(newEmployee);
        }
    }
}
