package edu.wpi.cs3733d19.teamN.database;

import edu.wpi.cs3733d19.teamN.database.DatabaseService;

public class DBInitTests {
    DatabaseService myDB;
    DatabaseService altDB;

//    @Ignore
//    @Test
//    @Category(FastTest.class)
//    public void initBase() {
//        boolean testor;
//        myDB = DatabaseService.getDatabaseService(true);
//        assertTrue(myDB.tableExists("NODE"));
//    }

//    @Test
//    @Category(FastTest.class)
//    public void testVersionCheck() throws Exception {
//        FileUtils.deleteDirectory(new File("hospital-db-version-test"));
//
//        int initialDBVersion = DatabaseService.DATABASE_VERSION;
//
//        DatabaseService dbs = null;
//        try {
//            dbs = DatabaseService.init("hospital-db-version-test");
//        } catch (MismatchedDatabaseVersionException e) {
//            fail("MismatchedDatabaseVersionException when starting from no DB");
//        }
//        dbs.close();
//
//        try {
//            dbs = DatabaseService.init("hospital-db-version-test");
//        } catch (MismatchedDatabaseVersionException e) {
//            fail("MismatchedDatabaseVersionException when starting with correct version");
//        }
//        dbs.close();
//
//
//        setFinalStatic(DatabaseService.class.getDeclaredField("DATABASE_VERSION"), Integer.valueOf(initialDBVersion + 42));
//
//        try {
//            dbs = DatabaseService.init("hospital-db-version-test");
//            fail("Expected MismatchedDatabaseVersionException!");
//        } catch (MismatchedDatabaseVersionException e) {
//            assertEquals(e.getMessage(), "Existing database version: " + initialDBVersion + ", expected: " + (initialDBVersion + 42));
//        }
//        dbs.close();
//
//        FileUtils.deleteDirectory(new File("hospital-db-version-test"));
//
//        setFinalStatic(DatabaseService.class.getDeclaredField("DATABASE_VERSION"), Integer.valueOf(initialDBVersion));
//    }

//    static void setFinalStatic(Field field, Object newValue) throws Exception {
//        field.setAccessible(true);
//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//        field.set(null, newValue);
//    }
}

