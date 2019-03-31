package service;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;
import testclassifications.SlowTest;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DBInitTests {
    DatabaseService myDB;
    DatabaseService altDB;


    @Test
    @Category(FastTest.class)
    public void initBase() throws MismatchedDatabaseVersionException {
        boolean testor;
        try {
            myDB = DatabaseService.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertTrue(myDB.tableExists("NODE"));
    }

    @Test
    @Category(FastTest.class)
    public void testVersionCheck() throws Exception {
        FileUtils.deleteDirectory(new File("hospital-db-version-test"));

        int initialDBVersion = DatabaseService.DATABASE_VERSION;

        DatabaseService dbs = null;
        try {
            dbs = DatabaseService.init("hospital-db-version-test");
        } catch (MismatchedDatabaseVersionException e) {
            fail("MismatchedDatabaseVersionException when starting from no DB");
        }
        dbs.close();

        try {
            dbs = DatabaseService.init("hospital-db-version-test");
        } catch (MismatchedDatabaseVersionException e) {
            fail("MismatchedDatabaseVersionException when starting with correct version");
        }
        dbs.close();


        DatabaseService.DATABASE_VERSION = initialDBVersion + 42;

        try {
            dbs = DatabaseService.init("hospital-db-version-test");
            fail("Expected MismatchedDatabaseVersionException!");
        } catch (MismatchedDatabaseVersionException e) {
            assertEquals(e.getMessage(), "Existing database version: " + initialDBVersion + ", expected: " + (initialDBVersion + 42));
        }
        dbs.close();

        FileUtils.deleteDirectory(new File("hospital-db-version-test"));
    }
}

