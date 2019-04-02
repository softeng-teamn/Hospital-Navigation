package model;

import model.Elevator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

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
    @Category(ElevatorTest.class)
    public void connectionTest() throws Exception {
        double[] d = e.data;
        System.out.println(d[0]);
        Thread.sleep(100);
        System.out.println(d[0]);
    }
}
