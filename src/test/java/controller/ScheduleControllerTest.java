package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Employee;
import model.ReservableSpace;
import model.Reservation;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

import java.time.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;

import java.util.TimeZone ;




public class ScheduleControllerTest extends ApplicationTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<String> rooms = new ArrayList<>();

    private GregorianCalendar gc = new GregorianCalendar();

    private ArrayList<ReservableSpace> spaces = new ArrayList<>();
    private ArrayList<Reservation> reservationsA = new ArrayList<Reservation>();
    private ArrayList<Reservation> reservationsB = new ArrayList<Reservation>();

    final static String homeBtn = "#homeBtn";

    private Reservation reservA = new Reservation(23, 0, 1337, "Cancer Seminar",
            "TFB", gc, gc);

    private Reservation reservB = new Reservation(24, 0, 1337, "HIV Seminar",
            "FFD", gc, gc);

    private Reservation reservC = new Reservation(25, 0, 1337, "Alzheimer's Seminar",
            "SFF", gc, gc);

    // general variables
    private String roomID ;
    private GregorianCalendar theDate = new GregorianCalendar(2019, 3, 20, 0, 0, 0) ;
    private ArrayList<Reservation> reservationReturns = new ArrayList<>() ;
    private ArrayList<GregorianCalendar> returnList = new ArrayList<>() ;
    private TimeZone tz = TimeZone.getTimeZone("GMT");


    @Mock private DatabaseService dbs;

    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init() {
        GregorianCalendar gcalStart = GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()));

        ReservableSpace A = new ReservableSpace("ID A", "Conf room A", "CONF", "location A", new GregorianCalendar(), new GregorianCalendar());
        ReservableSpace B = new ReservableSpace("ID B", "Conf room B", "CONF", "location B", new GregorianCalendar(), new GregorianCalendar());
        spaces.add(A);
        spaces.add(B);

        reservationsA.add(new Reservation(123,0,456, "Party A", "Room A",
                new GregorianCalendar(2019, 4, 1, 10, 0),
                new GregorianCalendar(2019,4,1,15,0)));
        reservationsB.add(new Reservation(41,1,13, "ER Meeting", "Room B",
                new GregorianCalendar(2019, 4, 1, 18, 0),
                new GregorianCalendar(2019,4,1,19,0)));
        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        // use these
//        when(dbs.insertReservation(reservA)).thenReturn(true).thenReturn(false) ;
//        when(dbs.insertReservation(reservB)).thenReturn(false) ;
//        when(dbs.insertReservation(reservC)).thenReturn(false) ;
        when(dbs.getAllReservableSpaces()).thenReturn(spaces);
        when(dbs.getReservationsBySpaceIdBetween("Room A",gcalStart,gcalStart)).thenReturn(reservationsA);
        when(dbs.getReservationsBySpaceIdBetween("Room B",gcalStart,gcalStart)).thenReturn(reservationsB);
        when(dbs.getEmployee(123)).thenReturn(new Employee(123, "Janitor", false, "password"));
        when(dbs.getEmployee(77)).thenReturn(null);



        // general variables
        roomID = "aSampleRoomID" ;
        theDate.setTimeZone(tz);

        // times for res1
        GregorianCalendar startTime1 = new GregorianCalendar(2019, 3, 20, 1, 30, 0) ;
        GregorianCalendar endTime1 = new GregorianCalendar(2019, 3, 20, 4, 0, 0) ;
        // times for res2
        GregorianCalendar startTime2 = new GregorianCalendar(2019, 3, 20, 5, 0, 0) ;
        GregorianCalendar endTime2 = new GregorianCalendar(2019, 3, 20, 22, 30, 0) ;

        // create a list of Reservations passed from the database
        Reservation res1 = new Reservation(1, 2, 45, "Event1", "Room1", startTime1, endTime1) ;
        Reservation res2 = new Reservation(2, 2, 45, "Event2", "Room1", startTime2, endTime2) ;
        reservationReturns.add(res1) ;
        reservationReturns.add(res2) ;

        // set correct times zones
        startTime1.setTimeZone(tz);
        endTime1.setTimeZone(tz);
        startTime2.setTimeZone(tz);
        endTime2.setTimeZone(tz);


        // times for return list
        GregorianCalendar returnTime1 = new GregorianCalendar(2019, 3, 20, 0, 0, 0) ;
        GregorianCalendar returnTime2 = new GregorianCalendar(2019, 3, 20, 0, 30, 0) ;
        GregorianCalendar returnTime3 = new GregorianCalendar(2019, 3, 20, 1, 0, 0) ;
        GregorianCalendar returnTime4 = new GregorianCalendar(2019, 3, 20, 4, 0, 0) ;
        GregorianCalendar returnTime5 = new GregorianCalendar(2019, 3, 20, 4, 30, 0) ;
        GregorianCalendar returnTime6 = new GregorianCalendar(2019, 3, 20, 22, 30, 0) ;
        GregorianCalendar returnTime7 = new GregorianCalendar(2019, 3, 20, 23, 0, 0) ;
        GregorianCalendar returnTime8 = new GregorianCalendar(2019, 3, 20, 23, 30, 0) ;

        returnList.add(returnTime1) ;
        returnList.add(returnTime2) ;
        returnList.add(returnTime3) ;
        returnList.add(returnTime4) ;
        returnList.add(returnTime5) ;
        returnList.add(returnTime6) ;
        returnList.add(returnTime7) ;
        returnList.add(returnTime8) ;


        returnTime1.setTimeZone(tz);
        returnTime2.setTimeZone(tz);
        returnTime3.setTimeZone(tz);
        returnTime4.setTimeZone(tz);
        returnTime5.setTimeZone(tz);
        returnTime6.setTimeZone(tz);
        returnTime7.setTimeZone(tz);
        returnTime8.setTimeZone(tz);


        // to print expect values
        /*
        System.out.println("Expected Values - Return List: ");
        for (int i = 0 ; i < returnList.size() ; i++){
            //System.out.println(returnTime1);
            returnList.get(i).setTimeZone(tz) ;
            System.out.println(returnList.get(i).toInstant()) ;
            //System.out.println("ZONE: " + returnList.get(i).getTimeZone());
            System.out.println();
        }
        */


        // what database should do
        when(dbs.getReservationsBySpaceId(roomID)).thenReturn(reservationReturns) ;


        DatabaseService.setDatabaseForMocking(dbs);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(ResourceLoader.scheduler);
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        stage.sizeToScene();
        stage.setFullScreen(true);
    }

    @Ignore
    @Test
    @Category({UiTest.class, FastTest.class})
    public void showHome() throws InterruptedException {
        clickOn(homeBtn);
        Thread.sleep(200);
        //TODO: need a valid home screen w/ something to ID
        HBox pane = (HBox) GuiTest.find("#top_nav");
        assertTrue(pane.isVisible());
    }

    @Ignore
    @Test
    @Category({UiTest.class, FastTest.class})
    public void showRoomSchedule() throws InterruptedException {
        clickOn("#makeReservationBtn");
        Thread.sleep(200);
        //checks to see if the button is visible, and available to be clicked on
        assertFalse(sc.makeReservationBtn.isDisable());
        //Checking both lists to see if the correct people have been initialized
        Label l = (Label)sc.schedule.getChildren().get(0);
        assertTrue(l != null);
        assertTrue(l.getText().equals("10:00"));
        Label l2 = (Label)sc.schedule.getChildren().get(1);
        assertTrue(l2 != null);
        assertTrue(l2.getText().equals("5:30"));
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void makeReservation() {

    }

    @Ignore
    @Test
    @Category({UiTest.class, FastTest.class})
    public void createReservation() {
        sc.datePicker.setValue(LocalDate.now());
        sc.startTimePicker.setValue(LocalTime.of(9,0));
        sc.endTimePicker.setValue(LocalTime.of(10, 0));
        GregorianCalendar gcalStart = new GregorianCalendar(2019, 3, 20, 9, 0, 0) ;
        GregorianCalendar gcalEnd = new GregorianCalendar(2019, 3, 20, 10, 0, 0) ;


        Reservation r = new Reservation(-1, Integer.parseInt(sc.privacyLvlBox.getValue()),Integer.parseInt(sc.employeeID.getText()),
                sc.eventName.getText(),sc.currentSelection.getLocationNodeID(),gcalStart,gcalEnd);
        sc.createReservation();
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getEventID() == r.getEventID());
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getPrivacyLevel() == r.getPrivacyLevel());
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getEmployeeId() == r.getEmployeeId());
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getEventName().equals(r.getEventName()));
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getLocationID() == r.getLocationID());
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getStartTime().equals(r.getStartTime()));
        assertTrue(DatabaseService.getDatabaseService(true).getReservation(0).getEndTime().equals(r.getEndTime()));

    }

//    @Test
//    @Category({FastTest.class})
//    public void validTimes() {
//        // TODO need database
//    }
//
//    @Test
//    @Category({FastTest.class})
//    public void makeTimesValid() {
//        // TODO need database
//    }

    @After
    public void clear(){
        rooms.clear();
        DatabaseService.getDatabaseService().wipeTables();
    }


//    @Test
//    @Category({FastTest.class})
//    // TODO: Move to non-UI class
//    public void getAvailTimesTest() {
//        // check returns available times
//        assertThat(sc.getAvailableTimes(roomID, theDate), equalTo(returnList)) ;
//    }
}
