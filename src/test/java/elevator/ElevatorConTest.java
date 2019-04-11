package elevator;

import elevator.ElevatorConnnection;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.ElevatorTest;

import java.io.IOException;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;


public class ElevatorConTest {

    @Test
    @Category(ElevatorTest.class)
    public void getTest(){
        String floor = "";
        ElevatorConnnection e = new ElevatorConnnection();

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
