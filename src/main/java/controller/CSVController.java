package controller;

import model.Edge;
import model.Node;
import service.ResourceLoader;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

public class CSVController extends Controller {

    // export tables from db to .csv file
    public boolean exportDB() {
        // NEEDS DATABASE TO BE FUNCTIONING
        return true;
    }

    // loads .csv file into into nodes database
    public static Collection<Node> loadNodes() throws FileNotFoundException, URISyntaxException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        File file = Paths.get(ResourceLoader.nodes.toURI()).toFile();
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            List<String> line = Arrays.asList(scanner.nextLine().split(","));
            //nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName
            Node myNode = new Node(
                    line.get(0),
                    Integer.parseInt(line.get(1)),
                    Integer.parseInt(line.get(2)),
                    line.get(3),
                    line.get(4),
                    line.get(5),
                    line.get(6),
                    line.get(7)
            );
            nodes.add(myNode);
        }
        scanner.close();
        return nodes;
    }


}
