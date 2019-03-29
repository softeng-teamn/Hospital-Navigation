package service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DBInitTests {
    DatabaseService myDB;
    DatabaseService altDB;


    @Test
    @Category(FastTest.class)
    public void initBase() {
        boolean testor;
        try {
            myDB = DatabaseService.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertTrue(myDB.tableExists("NODE"));
    }


}

