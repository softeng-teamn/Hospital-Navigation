package controller;

import model.Node;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MapViewTest {
    private MapView mp = new MapView();
    Node n0 = new Node(0,0,"ID 1", "L2", "Tower", "HALL", "Hallway A1", "HA1");
    Node n2 = new Node(5,5,"ID 2", "L2", "Tower", "HALL", "Hallway D2", "HD2");
    Node n3 = new Node(5,0,"ID 3", "L2", "Tower", "HALL", "Hallway B1", "HB1");
    Node n4 = new Node(5,-5,"ID 4", "L2", "Tower", "HALL", "Hallway E2", "HE2");
    Node n5 = new Node(0,-5,"ID 5", "L2", "Tower", "HALL", "Hallway F1", "HF1");
    Node n6 = new Node(-5,-5,"ID 6", "L2", "Tower", "HALL", "Hallway A2", "HA2");
    Node n7 = new Node(-5,0,"ID 7", "L2", "Tower", "HALL", "Hallway D3", "HD3");
    Node n8 = new Node(-5,5,"ID 8", "L2", "Tower", "HALL", "Hallway F2", "HF2");
    Node n1 = new Node(0,5,"ID 9", "L2", "Tower", "HALL", "Hallway F3", "HF3");
    Node nE = new Node(0,0,"ID E", "L2", "Tower", "ELEV", "Elevator A5", "EA5");
    Node nE2 = new Node(0,0,"ID E", "L1", "Tower", "ELEV", "Elevator B5", "EB5");
    Node nE3 = new Node(0,0,"ID E", "G", "Tower", "ELEV", "Elevator C5", "EC5");
    Node nS =new Node(0,0,"ID S", "L2", "Tower", "STAI", "Staircase F9", "SF9");
    Node topFloor = new Node(9,9,"top", "3", "Tower", "HALL", "top hall", "TH");

    @Test
    @Category( FastTest.class)
    public void makeDirections() {
        // Empty/too short path tests
        assertNull(mp.makeDirections(null));
        ArrayList<Node> path = new ArrayList<>();
        path.add(n1);
        assertNull(mp.makeDirections(path));
        path.add(n2);
        assertNotNull(mp.makeDirections(path));
        path.add(n3);
        assertNotNull(mp.makeDirections(path));
        path.clear();

        // First node tests
        path.addAll(Arrays.asList(n0, n1, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("south west"));
        path.clear();
        path.addAll(Arrays.asList(n0, n2, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("south"));
        path.clear();
        path.addAll(Arrays.asList(n0, n3, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("east"));
        path.clear();
        path.addAll(Arrays.asList(n0, n4, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("east"));
        path.clear();
        path.addAll(Arrays.asList(n0, n5, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("north east"));
        path.clear();
        path.addAll(Arrays.asList(n0, n6, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("north"));
        path.clear();
        path.addAll(Arrays.asList(n0, n7, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("west"));
        path.clear();
        path.addAll(Arrays.asList(n0, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("west"));
        path.clear();
        path.addAll(Arrays.asList(nE, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("west"));
        path.clear();
        path.addAll(Arrays.asList(nS, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("west"));

        // Second node tests
        path.clear();
        path.addAll(Arrays.asList(n0, nE, topFloor));
        assertTrue(mp.makeDirections(path).get(2).contains("elevator"));
        path.clear();
        path.addAll(Arrays.asList(n0, nS, topFloor));
        assertTrue(mp.makeDirections(path).get(2).contains("stairs"));

        // Third node tests
        path.clear();
        path.addAll(Arrays.asList(n0, n6, nE));
        assertTrue(mp.makeDirections(path).get(2).contains("to the elevator"));
        path.clear();
        path.addAll(Arrays.asList(n0, n6, nS));
        assertTrue(mp.makeDirections(path).get(2).contains("to the stairs"));

        // Double elevator tests
        path.clear();
        path.addAll(Arrays.asList(n0, nE, nE2));
        assertTrue(mp.makeDirections(path).get(2).contains("to the elevator"));
        path.clear();
        path.addAll(Arrays.asList(nE, nE2, topFloor));
        assertTrue(mp.makeDirections(path).get(1).contains("the elevator"));
        path.clear();
        path.addAll(Arrays.asList(nE, n7, nE));
        assertTrue(mp.makeDirections(path).get(2).contains("to the elevator"));
        path.clear();
        path.addAll(Arrays.asList(nE, nE2, nE3));
        assertTrue(mp.makeDirections(path).get(1).contains("Take"));

        //Progressions
        Node str1 = new Node(0,-5,"ID 6", "L2", "Tower", "HALL", "Hallway A2", "HA2");
        Node str2 = new Node(0,0,"ID 7", "L2", "Tower", "HALL", "Hallway D3", "HD3");
        Node str3 = new Node(0,5,"ID 8", "L2", "Tower", "HALL", "Hallway F2", "HF2");
        Node str4 = new Node(0,15,"ID 9", "L2", "Tower", "HALL", "Hallway F3", "HF3");
        path.clear();
        path.addAll(Arrays.asList(str1, str2, str3, str4));
        assertTrue(mp.makeDirections(path).get(1).contains("7"));
        Node nE4 = new Node(0,0,"ID E", "1", "Tower", "ELEV", "Elevator B5", "EB5");
        Node nE5 = new Node(0,0,"ID E", "2", "Tower", "ELEV", "Elevator C5", "EC5");
        Node nE6 = new Node(0,0,"ID E", "3", "Tower", "ELEV", "Elevator C5", "EC5");
        path.clear();
        path.addAll(Arrays.asList(nE, nE2, nE3, nE4, nE5, nE6));
        assertTrue(mp.makeDirections(path).get(1).contains("3"));

        // TODO - finish
    }

    @Test
    @Category( FastTest.class)
    public void convertToCardinal() {
        assertTrue(mp.convertToCardinal("straight").contains("south"));
        assertTrue(mp.convertToCardinal("around").contains("north"));
        assertTrue(mp.convertToCardinal("slightly left").contains("south east"));
        assertTrue(mp.convertToCardinal("slightly right").contains("south west"));
        assertTrue(mp.convertToCardinal("sharply left").contains("north east"));
        assertTrue(mp.convertToCardinal("sharply right").contains("north west"));
        assertTrue(mp.convertToCardinal("left").contains("east"));
        assertTrue(mp.convertToCardinal("right").contains("west"));
    }

    @Test
    @Category( FastTest.class)
    public void csDirPrint() {
        // Up right diagonal
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 4, 4).contains("straight"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 4, 8).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 4, 0).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 2, 2).contains("around"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 3, 9).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 3, -8).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 5, 3).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 3, 3, 0, 3).contains("right"));

        // Up left diagonal
        assertTrue(mp.csDirPrint(6, 0, 3, 3, 2, 4).contains("straight"));
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 1, 9).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 1, -9).contains("right"));
        assertTrue(mp.csDirPrint(6, 0, 3, 3, 4, 2).contains("around"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 3, 9).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 3, -8).contains("right"));
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 5, 3).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 3, 3, 0, 3).contains("right"));

        // Down right diagonal
        assertTrue(mp.csDirPrint(0, 5, 3, 2, 5, 0).contains("straight"));
        assertTrue(mp.csDirPrint(0, 5, 3, 2, 0, 8).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 3, 2, 5, -7).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, 3, 2, 1, 4).contains("around"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(0, 5, 3, 3, 3, 9).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 3, 3, 3, -8).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, 3, 3, 5, 3).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 3, 3, 0, 3).contains("left"));

        // Down left diagonal
        assertTrue(mp.csDirPrint(5, 5, 3, 3, 0, 0).contains("straight"));
        assertTrue(mp.csDirPrint(5, 5, 3, 3, 0, 1).contains("left"));
        assertTrue(mp.csDirPrint(5, 5, 3, 3, 0, -8).contains("right"));
        assertTrue(mp.csDirPrint(5, 5, 3, 3, 6, 6).contains("around"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(0, 5, -3, 3, -3, 9).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, -3, 3, -3, -8).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, -3, 3, -5, 3).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, -3, 3, 0, 3).contains("right"));

        // Straight up
        assertTrue(mp.csDirPrint(0, 0, 0, 5, 1, 6).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, -1, 2).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, -2, 9).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, 7, 9).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, 5, 5).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, -9, 5).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, 0, 19).contains("straight"));
        assertTrue(mp.csDirPrint(0, 0, 0, 5, 0, -7).contains("around"));

        // Straight down
        assertTrue(mp.csDirPrint(0, 5, 0, 0, 5, 5).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, 9, 0).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, 0, -9).contains("straight"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, 0, 10).contains("around"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, 5, -5).contains("right"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, -2, -2).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, -3, 0).contains("left"));
        assertTrue(mp.csDirPrint(0, 5, 0, 0, -7, 19).contains("left"));

        // Straight left
        assertTrue(mp.csDirPrint(5, 0, 0, 0, -1, 0).contains("straight"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 3, 4).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 0, 9).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 9, 10).contains("left"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 10, 0).contains("around"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, -9, -17).contains("right"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 0, -2).contains("right"));
        assertTrue(mp.csDirPrint(5, 0, 0, 0, 9, -8).contains("right"));

        // Straight right
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 9, 0).contains("straight"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 1, 0).contains("around"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, -9, 8).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 6, 9).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 10, 8).contains("right"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 8, -9).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 6, -10).contains("left"));
        assertTrue(mp.csDirPrint(0, 0, 6, 0, 99, -88).contains("left"));
    }

    @Test
    @Category( FastTest.class)
    public void showDirections() {
        //TODO
    }

    @Test
    @Category( FastTest.class)
    public void printDirections() {
        //TODO
    }
}