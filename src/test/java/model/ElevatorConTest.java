package model;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.io.IOException;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;


public class ElevatorConTest {

    @Test
    @Category(FastTest.class)
    public void getTest(){
        String floor = "";
        ElevatorCon e = new ElevatorCon();
        GregorianCalendar cal = new GregorianCalendar();
        try {
            e.postFloor("S", 3, cal);
        }catch (IOException ioe){
            System.out.println("IO Exception");
        }
        try {
            floor = e.getFloor("S");
        }catch (IOException ioe){
            System.out.println("IO Exceptoin");
        }

        assertEquals("3", floor);
    }

}
