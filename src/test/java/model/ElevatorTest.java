package model;

import model.Elevator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;
import static org.junit.Assert.assertEquals;

public class ElevatorTest {

    Elevator e;

    public ElevatorTest()  {
    }

    @Before
    public void setup()throws Exception{
        System.out.println("Searching for robots!");
        e = Elevator.get("MyRobotName");
    }
    @After
    public void shutdown(){
        e.disconnect();
    }

    @Test
    @Category(FastTest.class)
    public void connectionTest() throws Exception {
        double[] d = e.data;
        System.out.println(d[0]);
        Thread.sleep(100);
        System.out.println(d[0]);
        assertEquals(37.0, d[0], 0.1);
    }
}
