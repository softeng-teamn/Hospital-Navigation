package model;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.junit.Assert.assertEquals;

public class EmployeeTest {

   private Employee empA = new Employee(1337, "dcuduro", JobType.JANITOR, true, "danzaCuduro");
   private Employee empB = new Employee(1337, "dcuduro", JobType.JANITOR, true,"danzaCuduro");
   private Employee empC = new Employee(9876, "username", JobType.DOCTOR, false,"LAGODOSMACACOS");

    @Test
    @Category(FastTest.class)
    public void equalsTest(){
        assertEquals(empA.equals(empB), true);
        assertEquals(empA.equals(empC), false);
    }

}
