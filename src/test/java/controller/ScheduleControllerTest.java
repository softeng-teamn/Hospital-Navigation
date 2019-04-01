package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.*;

import java.sql.SQLOutput;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.JUNE;
import static java.util.Calendar.MINUTE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;
import org.mockito.Mock;




public class ScheduleControllerTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<ReservableSpace> rooms = new ArrayList<>();
    private ArrayList<Reservation> reservationsA = new ArrayList<Reservation>();
    private ArrayList<Reservation> reservationsB = new ArrayList<Reservation>();

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
        when(sc.initialize()).thenReturn();
    }

    @Mock private DatabaseService dbs;
    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init() {

        //  sc = spy(new ScheduleController());
        //   when(sc.bookRoom())

        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.insertReservation(reservA)).thenReturn(true).thenReturn(false) ;
        when(dbs.insertReservation(reservB)).thenReturn(false) ;
        when(dbs.insertReservation(reservC)).thenReturn(false) ;

        ScheduleController.dbs=dbs ;
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showHome() {
        // TODO
    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showRoomSchedule() {
        // TODO
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
    public void showInstructions() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeInstructions() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeError() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void closeConf() {

    }

    @Test
    @Category({UiTest.class, FastTest.class})
    public void showConf() {

    }

    @Test
    @Category({FastTest.class})
    public void validTimes() {

    }

    @Test
    @Category({FastTest.class})
    public void makeTimesValid() {

    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init() {

      //  sc = spy(new ScheduleController());
     //   when(sc.bookRoom())

        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.insertReservation(reservA)).thenReturn(true).thenReturn(false) ;
        when(dbs.insertReservation(reservB)).thenReturn(false) ;
        when(dbs.insertReservation(reservC)).thenReturn(false) ;

        ScheduleController.dbs=dbs ;
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
    public void insertReservationTest(){//probs needs more test cases involving the db
        // assert that an available room can be booked
        assertThat(sc.insertReservation(reservA), equalTo(true));
        // assert that a booked room cannot be double-booked
        assertThat(sc.insertReservation(reservA), equalTo(false));
        // assert that a non-existant room cannot be booked
        assertThat(sc.insertReservation(reservC), equalTo(false));
    }

    @Test
    @Category({FastTest.class})
    public void showAvailTimesTest() {

    }


}
