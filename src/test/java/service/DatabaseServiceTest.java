package service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseServiceTest {
    DatabaseService myDB;

    @Before
    public void setUp(){

        try {
            myDB = DatabaseService.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        myDB.close();
    }


    @Test
    public void addNode() {
    }

    @Test
    public void editNode() {
    }

    @Test
    public void deleteNode() {
    }

    @Test
    public void getNodes() {
    }

    @Test
    public void getEdges() {
    }

    // uh i legit don't know how to test this because everything relies on it and we can't delete
    // the tables yet
    @Test
    public void createTables() {
    }

    @Test
    @Category(FastTest.class)
    public void tableExists() {
        assertTrue(myDB.tableExists("NODE"));
        assertFalse(myDB.tableExists("NOTPRESENT"));


    }
}