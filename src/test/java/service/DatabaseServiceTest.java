package service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class DatabaseServiceTest {

    boolean result = false;

    @Test
    @Category(FastTest.class)
    public void initTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void addNodeTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void editNodeTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void deleteNodeTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void getRoomSchedTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void bookRoomTest(){
        assertEquals(true, result);
    }

    @Test
    @Category(FastTest.class)
    public void addRequestTest(){
        assertEquals(true, result);
    }
    @Test
    @Category(FastTest.class)
    public void loginTest(){
        assertEquals(true, result);
    }


}
