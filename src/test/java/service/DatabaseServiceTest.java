package service;

import model.Node;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseServiceTest {
/**
    //@Before
    public void setUp(){

        try {
            DatabaseService.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
    //@After
   // public void tearDown() throws Exception {
   //     DatabaseService.getMyDBC().dropAll();
   //     close();
   // }

    @Test
    @Category(FastTest.class)
    public void init() {
        boolean testor;
        try {
            DatabaseService.init();
            testor = true;
        } catch (SQLException e) {
            e.printStackTrace();
            testor = false;
        }
        assertTrue(testor);


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
}