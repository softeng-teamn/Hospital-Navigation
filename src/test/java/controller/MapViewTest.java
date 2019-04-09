package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.*;
import javafx.scene.control.*;
import model.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import testclassifications.FastTest;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import service.DatabaseService;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static controller.Controller.elevatorCon;
import static controller.Controller.floorIsAt;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import java.util.ArrayList;
import java.util.Arrays;

import static controller.EmployeeEditController.myDBS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapViewTest extends ApplicationTest {
    private MapView mp = new MapView();;
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

    @Override
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void start(Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(ResourceLoader.home);
        stage.setScene(new Scene(mainNode));
    }

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
        assertTrue(mp.makeDirections(path).get(1).contains("V"));
        path.clear();
        path.addAll(Arrays.asList(n0, n2, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("W"));
        path.clear();
        path.addAll(Arrays.asList(n0, n3, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("Y"));
        path.clear();
        path.addAll(Arrays.asList(n0, n4, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("Y"));
        path.clear();
        path.addAll(Arrays.asList(n0, n5, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("Z"));
        path.clear();
        path.addAll(Arrays.asList(n0, n6, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("T"));
        path.clear();
        path.addAll(Arrays.asList(n0, n7, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("U"));
        path.clear();
        path.addAll(Arrays.asList(n0, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("U"));
        path.clear();
        path.addAll(Arrays.asList(nE, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("U"));
        path.clear();
        path.addAll(Arrays.asList(nS, n8, n2));
        assertTrue(mp.makeDirections(path).get(1).contains("U"));

        // Second node tests
        path.clear();
        path.addAll(Arrays.asList(n0, nE, topFloor));
        assertTrue(mp.makeDirections(path).get(2).contains("N"));
        path.clear();
        path.addAll(Arrays.asList(n0, nS, topFloor));
        assertTrue(mp.makeDirections(path).get(2).contains("P"));

        // Third node tests
        path.clear();
        path.addAll(Arrays.asList(n0, n6, nE));
        assertTrue(mp.makeDirections(path).get(2).contains("I"));
        path.clear();
        path.addAll(Arrays.asList(n0, n6, nS));
        assertTrue(mp.makeDirections(path).get(2).contains("J"));

        // Double elevator tests
        path.clear();
        path.addAll(Arrays.asList(n0, nE, nE2));
        assertTrue(mp.makeDirections(path).get(1).contains("I"));
        path.clear();
        path.addAll(Arrays.asList(nE, nE2, topFloor));
        assertTrue(mp.makeDirections(path).get(1).contains("N"));
        path.clear();
        path.addAll(Arrays.asList(nE, n7, nE));
        assertTrue(mp.makeDirections(path).get(2).contains("I"));
        path.clear();
        path.addAll(Arrays.asList(nE, nE2, nE3));
        assertTrue(mp.makeDirections(path).get(1).contains("N"));

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
        assertTrue(mp.makeDirections(path).get(1).contains("F"));
        Node ns1 = new Node(0,0,"ID E", "L2", "Tower", "STAI", "Stairs B5", "SB5");
        Node ns2 = new Node(0,0,"ID E", "L1", "Tower", "STAI", "Stairs C5", "SC5");
        Node ns3 = new Node(0,0,"ID E", "G", "Tower", "STAI", "Stairs C5", "SC5");
        Node ns4 = new Node(0,0,"ID E", "1", "Tower", "STAI", "Stairs B5", "SB5");
        Node ns5 = new Node(0,0,"ID E", "2", "Tower", "STAI", "Stairs C5", "SC5");
        Node ns6 = new Node(0,0,"ID E", "3", "Tower", "STAI", "Stairs C5", "SF5");
        path.clear();
        path.addAll(Arrays.asList(ns6, ns5, ns4, ns3, ns2, ns1));
        assertTrue(mp.makeDirections(path).get(1).contains("QFA"));


        // TODO - finish
        // test afterFloorchange ->, write
    }

    @Test
    @Category( FastTest.class)
    public void convertToCardinal() {
        assertTrue(mp.convertToCardinal("A").contains("W"));
        assertTrue(mp.convertToCardinal("B").contains("Y"));
        assertTrue(mp.convertToCardinal("C").contains("X"));
        assertTrue(mp.convertToCardinal("D").contains("Z"));
        assertTrue(mp.convertToCardinal("E").contains("U"));
        assertTrue(mp.convertToCardinal("F").contains("V"));
        assertTrue(mp.convertToCardinal("G").contains("T"));
        assertTrue(mp.convertToCardinal("H").contains("S"));
    }

    @Test
    @Category( FastTest.class)
    public void csDirPrint() {
        // Up right diagonal
        assertTrue(mp.csDirPrint(new Node(0, 0), new Node(3, 3), new Node(4, 4)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(0, 0), new Node(3, 3), new Node(4, 8)).contains("F"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(3,3), new Node(4, 0)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,0),new Node(3,3), new Node(2, 2)).contains("H"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(3,3), new Node(3, 9)).contains("F"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(3,3), new Node(3, -8)).contains("D"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(3,3), new Node(5, 3)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(3,3), new Node(0, 3)).contains("G"));

        // Up right diagonal
        assertTrue(mp.csDirPrint(new Node(6,0), new Node(3,3), new Node(2, 4)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(1, 9)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(1, -9)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(6,0), new Node(3,3), new Node(4, 2)).contains("H"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(3, 9)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(3, -8)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(5, 3)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(0, 3)).contains("E"));

        // Down left diagonal
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3, 2), new Node(5,0)).contains("H"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3, 2), new Node(0, 8)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3, 2), new Node(5, -7)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3, 2), new Node(1, 4)).contains("A"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(3, 9)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(3, -8)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(5, 3)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(3,3), new Node(0, 3)).contains("E"));

        // Down right diagonal
        assertTrue(mp.csDirPrint(new Node(5, 5), new Node(3,3), new Node(0, 0)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(5, 5), new Node(3,3),new Node( 0, 1)).contains("C"));
        assertTrue(mp.csDirPrint(new Node(5, 5), new Node(3,3),new Node( 0, -8)).contains("F"));
        assertTrue(mp.csDirPrint(new Node(5, 5), new Node(3,3),new Node( 6, 6)).contains("H"));
        // Horiz and vert
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(-3,3), new Node(-3, 9)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(-3,3), new Node(-3, -8)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(-3,3), new Node(-5, 3)).contains("F"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(-3,3), new Node(0, 3)).contains("D"));

        // Straight up
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(5,0), new Node(1, 6)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(5,0), new Node(-1, 2)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(5,0), new Node(-2, 9)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(5,0), new Node(7, 9)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(0,5),new Node(5, 5)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(5,0), new Node(-9, 5)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(0,5),new Node(0, 19)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(0,0),new Node(0,5),new Node(0, -7)).contains("H"));

        // Straight down
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(5,5)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(9, 0)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(0, -9)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(0, 10)).contains("H"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(5, -5)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(-2, -2)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,5),new Node(0, 0), new Node(-3, 0)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,5), new Node(0, 0), new Node(-7, 19)).contains("D"));

        // Straight left
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(-1, 0)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(3, 4)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(0, 9)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(9, 10)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(10, 0)).contains("H"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(-9, -17)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(0, -2)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(5,0), new Node(0, 0), new Node(9, -8)).contains("G"));

        // Straight right
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(9,0)).contains("A"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(1,0)).contains("H"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(-9, 8)).contains("G"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(6, 9)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(10, 8)).contains("E"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(8, -9)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(6, -10)).contains("B"));
        assertTrue(mp.csDirPrint(new Node(0,0), new Node(6,0), new Node(99, -88)).contains("C"));
    }

//    @Test
//    @Category( FastTest.class)
//    public void showDirections() {
//        //TODO
//    }

    @Test
    @Category( FastTest.class)
    public void printDirections() {
//        // Empty/too short path tests
//        assertNull(mp.makeDirections(null));
//        ArrayList<Node> path = new ArrayList<>();
//        path.add(n1);
//        assertNull(mp.makeDirections(path));
//        path.add(n2);
//        assertNotNull(mp.makeDirections(path));
//        path.add(n3);
//        assertNotNull(mp.makeDirections(path));
//        path.clear();
//
//        // First node tests
//        path.addAll(Arrays.asList(n0, n1, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("south west"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n2, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("south"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n3, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("east"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n4, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("east"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n5, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("north east"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n6, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("north"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n7, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("west"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n8, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("west"));
//        path.clear();
//        path.addAll(Arrays.asList(nE, n8, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("west"));
//        path.clear();
//        path.addAll(Arrays.asList(nS, n8, n2));
//        assertTrue(mp.makeDirections(path).get(1).contains("west"));
//
//        // Second node tests
//        path.clear();
//        path.addAll(Arrays.asList(n0, nE, topFloor));
//        System.out.println(mp.makeDirections(path).get(2).contains("elevator"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, nS, topFloor));
//        System.out.println(mp.makeDirections(path).get(2).contains("stairs"));
//
//        // Third node tests
//        path.clear();
//        path.addAll(Arrays.asList(n0, n6, nE));
//        System.out.println(mp.makeDirections(path).get(2).contains("to the elevator"));
//        path.clear();
//        path.addAll(Arrays.asList(n0, n6, nS));
//        System.out.println(mp.makeDirections(path).get(2).contains("to the stairs"));
//
//        // Double elevator tests
//        path.clear();
//        path.addAll(Arrays.asList(n0, nE, nE2));
//        System.out.println(mp.makeDirections(path).get(2).contains("to the elevator"));
//        path.clear();
//        path.addAll(Arrays.asList(nE, nE2, topFloor));
//        System.out.println(mp.makeDirections(path).get(1).contains("the elevator"));
//        path.clear();
//        path.addAll(Arrays.asList(nE, n7, nE));
//        System.out.println(mp.makeDirections(path).get(2).contains("to the elevator"));
//        path.clear();
//        path.addAll(Arrays.asList(nE, nE2, nE3));
//        System.out.println(mp.makeDirections(path).get(1).contains("Take"));
//
//        //Progressions
//        Node str1 = new Node(0,-5,"ID 6", "L2", "Tower", "HALL", "Hallway A2", "HA2");
//        Node str2 = new Node(0,0,"ID 7", "L2", "Tower", "HALL", "Hallway D3", "HD3");
//        Node str3 = new Node(0,5,"ID 8", "L2", "Tower", "HALL", "Hallway F2", "HF2");
//        Node str4 = new Node(0,15,"ID 9", "L2", "Tower", "HALL", "Hallway F3", "HF3");
//        path.clear();
//        path.addAll(Arrays.asList(str1, str2, str3, str4));
//        System.out.println(mp.makeDirections(path).get(1).contains("7"));
//        Node nE4 = new Node(0,0,"ID E", "1", "Tower", "ELEV", "Elevator B5", "EB5");
//        Node nE5 = new Node(0,0,"ID E", "2", "Tower", "ELEV", "Elevator C5", "EC5");
//        Node nE6 = new Node(0,0,"ID E", "3", "Tower", "ELEV", "Elevator C5", "EC5");
//        path.clear();
//        path.addAll(Arrays.asList(nE, nE2, nE3, nE4, nE5, nE6));
//        System.out.println(mp.makeDirections(path).get(1).contains("3"));
//        Node ns1 = new Node(0,0,"ID E", "L2", "Tower", "STAI", "Stairs B5", "SB5");
//        Node ns2 = new Node(0,0,"ID E", "L1", "Tower", "STAI", "Stairs C5", "SC5");
//        Node ns3 = new Node(0,0,"ID E", "G", "Tower", "STAI", "Stairs C5", "SC5");
//        Node ns4 = new Node(0,0,"ID E", "1", "Tower", "STAI", "Stairs B5", "SB5");
//        Node ns5 = new Node(0,0,"ID E", "2", "Tower", "STAI", "Stairs C5", "SC5");
//        Node ns6 = new Node(0,0,"ID E", "3", "Tower", "STAI", "Stairs C5", "SF5");
//        path.clear();
//        path.addAll(Arrays.asList(ns1, ns2, ns3, ns4, ns5, ns6));
//        System.out.println(mp.makeDirections(path).get(1).contains("3"));

        // TODO - finish
    }

    // Other/changed funcions TODO
}