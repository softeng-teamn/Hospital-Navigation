package edu.wpi.cs3733d19.teamN.employee;

import edu.wpi.cs3733d19.teamN.employee.model.Employee;
import edu.wpi.cs3733d19.teamN.employee.model.JobType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import edu.wpi.cs3733d19.teamN.testclassifications.FastTest;

import static org.junit.Assert.assertEquals;

public class EmployeeTest {

   private Employee empA = new Employee(1337, "dcuduro", "Dave", "Cuduro", JobType.JANITOR, true, "danzaCuduro");
   private Employee empB = new Employee(1337, "dcuduro", "Dave", "Cuduro", JobType.JANITOR, true,"danzaCuduro");
   private Employee empC = new Employee(9876, "username","User", "Name", JobType.DOCTOR, false,"LAGODOSMACACOS");

    @Test
    @Category(FastTest.class)
    public void equalsTest(){
        assertEquals(empA.equals(empB), true);
        assertEquals(empA.equals(empC), false);
    }

}
