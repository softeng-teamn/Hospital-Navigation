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
import static org.junit.Assert.assertTrue;


public class MapServiceTest {

    boolean result = false;

    @Test
    @Category(FastTest.class)
    public void findPathTest(){
        assertEquals(false, result);
    }
    @Test
    @Category(FastTest.class)
    public void addNodeTest(){
        assertEquals(false, result);
    }
    @Test
    @Category(FastTest.class)
    public void editNodeTest(){
        assertEquals(false, result);
    }
    @Test
    @Category(FastTest.class)
    public void deleteNodeTest(){
        assertEquals(false, result);
    }
}
