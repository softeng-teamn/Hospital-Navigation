package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import model.Employee;
import model.ReservableSpace;
import model.Reservation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.exceptions.NoNodesFoundException;
import org.loadui.testfx.exceptions.NoNodesVisibleException;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import service.DatabaseService;
import service.ResourceLoader;
import testclassifications.*;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.JUNE;
import static java.util.Calendar.MINUTE;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;
import org.mockito.Mock;

public class ScheduleControllerTest extends ApplicationTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<ReservableSpace> rooms = new ArrayList<>();
    private ArrayList<Reservation> reservationsA = new ArrayList<Reservation>();
    private ArrayList<Reservation> reservationsB = new ArrayList<Reservation>();

    final static String instrP = "#instructionsPane";
    final static String instrBtn = "#instructionsBtn";
    final static String homeBtn = "#homeBtn";
    final static String closeInstrBrn = "#closeInstructionsBtn";

    /**
     * Create fake rooms and reservations
     */
    @Before
    public void initRooms() {
        ReservableSpace A = new ReservableSpace("ID A", "Conf room A", "CONF", "location A", new GregorianCalendar(), new GregorianCalendar());
        ReservableSpace B = new ReservableSpace("ID B", "Conf room B", "CONF", "location B", new GregorianCalendar(), new GregorianCalendar());
        rooms.add(A);
        rooms.add(B);

        reservationsA.add(new Reservation(123,0,456, "Party A", "Room A",
                new GregorianCalendar(2019, 4, 1, 10, 0),
                new GregorianCalendar(2019,4,1,15,0)));
        reservationsB.add(new Reservation(41,1,13, "ER Meeting", "Room B",
                new GregorianCalendar(2019, 4, 1, 18, 0),
                new GregorianCalendar(2019,4,1,19,0)));
    }

    @Mock private DatabaseService dbs;
    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init() {
        GregorianCalendar gcalStart = GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()));

        DatabaseService dbs = mock(DatabaseService.class);
        // use these
//        when(dbs.insertReservation(reservA)).thenReturn(true).thenReturn(false) ;
//        when(dbs.insertReservation(reservB)).thenReturn(false) ;
//        when(dbs.insertReservation(reservC)).thenReturn(false) ;
        when(dbs.getAllReservableSpaces()).thenReturn(rooms);
        when(dbs.getReservationBySpaceIdBetween("Room A",gcalStart,gcalStart)).thenReturn(reservationsA);
        when(dbs.getReservationBySpaceIdBetween("Room B",gcalStart,gcalStart)).thenReturn(reservationsB);
        when(dbs.getEmployee(123)).thenReturn(new Employee(123, "Janitor", false));
        when(dbs.getEmployee(77)).thenReturn(null);

        ScheduleController.dbs=dbs ;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(ResourceLoader.scheduler);
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showHome() throws InterruptedException {
        clickOn(homeBtn);
        Thread.sleep(200);
        //TODO: need a valid home screen w/ something to ID
       // TitledPane pane = (TitledPane) GuiTest.find(instrP);
        //assertTrue(pane.isVisible());
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showRoomSchedule() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void makeReservation() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void createReservation() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void submit() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showInstructions() throws InterruptedException {
        clickOn(instrBtn);
        Thread.sleep(200);
        TitledPane pane = (TitledPane) GuiTest.find(instrP);
        assertTrue(pane.isVisible());
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeInstructions() throws InterruptedException {
        clickOn(instrBtn);
        Thread.sleep(200);
        clickOn(closeInstrBrn);
        Thread.sleep(200);
        boolean vis = true;
        try {
            GuiTest.exists(instrP);
        } catch (NoNodesFoundException | NoNodesVisibleException e) {
            vis = false;
        }
        assertFalse(vis);
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeError() throws InterruptedException {
        clickOn("#startTimePicker").write("1:00 AM");
        Thread.sleep(200);
        // TODO: need database to select location and make reservation to get an error
//        clickOn("#errorBtn");
//        Thread.sleep(200);
//        boolean vis = true;
//        try {
//            GuiTest.exists("#errorDlg");
//        } catch (NoNodesFoundException | NoNodesVisibleException e) {
//            vis = false;
//        }
//        assertFalse(vis);
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeConf() {
        // TODO need database
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showConf() {
        // TODO need database
    }

    @Test
    @Category({FastTest.class})
    public void validTimes() {
        // TODO need database
    }

    @Test
    @Category({FastTest.class})
    public void makeTimesValid() {
        // TODO need database
    }

    @After
    public void clear(){
        rooms.clear();
    }


    // Currently Unused
    @Category(FastTest.class)
    @Test
    public void testGetMaxPeopleContent(){//has all the right rooms
        //assertThat(sc.getMaxPeople(20), containsInAnyOrder(rooms));
    }

    @Category(FastTest.class)
    @Test
    public void testGetMaxPeopleSize(){//does not have more rooms than expected(test limitation from maxPeopleContent
        //assertThat(sc.getMaxPeople(20).size(), equalTo(5));
    }

    @Test
    @Category({FastTest.class})
    public void showAvailTimesTest() {

    }
}
