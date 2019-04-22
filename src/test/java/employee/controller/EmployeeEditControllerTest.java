package employee.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import employee.model.Employee;
import employee.model.JobType;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import database.DatabaseService;
import service.ResourceLoader;

import java.util.Arrays;
import java.util.List;

import static employee.controller.EmployeeEditController.myDBS;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.DebugUtils.informedErrorMessage;
import static org.hamcrest.CoreMatchers.is;


public class EmployeeEditControllerTest extends ApplicationTest {
    private Employee testEmployee1 = new Employee(0, "staff", "Staff", "Staff", JobType.ADMINISTRATOR, true, "1234");
    private Employee testEmployee2 = new Employee(1, "admin","Admin", "Admin",  JobType.ADMINISTRATOR, true, "1234");
    private Employee testEmployee3 = new Employee(2, "doc1", "Doc", "1", JobType.DOCTOR, false, "1234");
    private Employee testEmployee4 = new Employee(3, "nurse1","Nurse", "1",  JobType.NURSE, false, "1234");
    private Employee testEmployee5 = new Employee(4, "nurse2", "Nurse", "2", JobType.NURSE, false, "1234");
    private Employee testEmployee6 = new Employee(5, "worker1","Worker", "1",  JobType.MAINTENANCE_WORKER, false, "1234");
    private Employee testEmployee7 = new Employee(6, "worker2","Worker", "2",  JobType.MAINTENANCE_WORKER, false, "1234");
    private Employee testEmployee8 = new Employee(7, "worker3", "Worker", "3", JobType.MAINTENANCE_WORKER, false, "1234");
    private Employee testEmployee9 = new Employee(8, "worker4", "Worker", "4", JobType.SECURITY_PERSONNEL, false, "1234");
    private Employee testEmployee10 = new Employee(9, "worker5","Worker", "5",  JobType.JANITOR, false, "1234");

    private List<Employee> testEmployees = Arrays.asList(testEmployee1, testEmployee2, testEmployee3, testEmployee4, testEmployee5, testEmployee6, testEmployee7, testEmployee8, testEmployee9, testEmployee10);

    @Override
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void start(Stage stage) throws Exception {
        DatabaseService mockDBS = mock(DatabaseService.class);
        when(mockDBS.getAllEmployees()).thenReturn(testEmployees);
        myDBS = mockDBS;

        Parent mainNode = FXMLLoader.load(ResourceLoader.employeeEdit);
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        stage.sizeToScene();
        stage.setFullScreen(true);
    }

    @Before
    public void setupTest() {
    }

    @Test
    public void tempTest() throws InterruptedException {
        Thread.sleep(20000);
    }

    @Test
    public void employeeDisplayTest() {
        TableView<Employee> employee_table = GuiTest.find("#employee_table");

        ObservableList<TableColumn<Employee, ?>> cols = employee_table.getColumns();

        verifyThat(cols, hasSize(4), informedErrorMessage(this));

        TableColumn<Employee, Integer> col_id = (TableColumn<Employee, Integer>) cols.get(0);
        TableColumn<Employee, String> col_username = (TableColumn<Employee, String>) cols.get(1);
        TableColumn<Employee, JobType> col_job = (TableColumn<Employee, JobType>) cols.get(2);
        TableColumn<Employee, Boolean> col_admin = (TableColumn<Employee, Boolean>) cols.get(3);

        for (int i = 0; i < testEmployees.size(); i++) {
            verifyThat(col_id.getCellData(i), is(testEmployees.get(i).getID()), informedErrorMessage(this));
            verifyThat(col_username.getCellData(i), is(testEmployees.get(i).getUsername()), informedErrorMessage(this));
            verifyThat(col_job.getCellData(i), is(testEmployees.get(i).getJob()), informedErrorMessage(this));
            verifyThat(col_admin.getCellData(i), is(testEmployees.get(i).isAdmin()), informedErrorMessage(this));
        }

        // Verify next row is null
        verifyThat(col_id.getCellData(testEmployees.size() + 1), is(nullValue()), informedErrorMessage(this));
        verifyThat(col_username.getCellData(testEmployees.size() + 1), is(nullValue()), informedErrorMessage(this));
        verifyThat(col_job.getCellData(testEmployees.size() + 1), is(nullValue()), informedErrorMessage(this));
        verifyThat(col_admin.getCellData(testEmployees.size() + 1), is(nullValue()), informedErrorMessage(this));
    }

    @Test
    public void testEdit() {
        TableView<Employee> employee_table = GuiTest.find("#employee_table");

        ObservableList<TableColumn<Employee, ?>> cols = employee_table.getColumns();

        verifyThat(cols, hasSize(4), informedErrorMessage(this));

        TableColumn<Employee, Integer> col_id = (TableColumn<Employee, Integer>) cols.get(0);
//        TableColumn<Employee, String> col_username = (TableColumn<Employee, String>) cols.get(1);
//        TableColumn<Employee, JobType> col_job = (TableColumn<Employee, JobType>) cols.get(2);
//        TableColumn<Employee, Boolean> col_admin = (TableColumn<Employee, Boolean>) cols.get(3);

        clickOn(getCell(0, 0)).write("abcd").clickOn(0, 0);
        verifyThat(col_id.getCellData(0), is(testEmployee1.getID()), informedErrorMessage(this));

        //clickOn(getCell(1, 1)).doubleClickOn((Node) lookup(".text-field-table-cell").nth(1).query()).write("lmno").clickOn(0, 0);
        //verifyThat(col_username.getCellData(1), is("lmno"), informedErrorMessage(this));
    }


    private Node getCell(int column, int row) {
        return ((TableRow) lookup(".table-row-cell").nth(column).query()).getChildrenUnmodifiable().get(row);
    }
}
