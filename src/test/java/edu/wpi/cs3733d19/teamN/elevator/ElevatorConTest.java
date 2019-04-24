package edu.wpi.cs3733d19.teamN.elevator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import edu.wpi.cs3733d19.teamN.testclassifications.ElevatorTest;

import java.io.IOException;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;


public class ElevatorConTest {

    @Test
    @Category(ElevatorTest.class)
    public void getTest(){
        String floor = "";
        ElevatorConnection e = new ElevatorConnection();

        GregorianCalendar cal = new GregorianCalendar();
        try {
            e.postFloor("S", "3", cal);
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
