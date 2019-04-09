package controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.junit.Assert.*;

public class MapViewTest {
    private MapView mp = new MapView();
    
    @Test
    @Category( FastTest.class)
    public void makeDirections() {

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
}