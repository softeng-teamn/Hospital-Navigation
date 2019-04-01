package controller;

import model.ReservableSpace;
import model.Reservation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import service.DatabaseService;
import testclassifications.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.spy;

public class ScheduleControllerTest {
    private ScheduleController sc = new ScheduleController();
    // I think what I want is a fake database? TODO
    private ArrayList<ReservableSpace> rooms = new ArrayList<ReservableSpace>();
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

    }

    @After
    public void clearRooms(){
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

    @Category(FastTest.class)
    @Test
    public void getDay(){
        //assertThat(sc.getDay(), equalTo("12122019"));
    }

    @Category(FastTest.class)
    @Test
    public void getRoom() {
        //assertThat(sc.getRoom(), equalTo("ROOM1"));
    }

    @Category(FastTest.class)
    @Test
    public void getRoomSched(){
        //assertThat(sc.getRoomSched("ROOM1", "12122019"), equalTo("10:30-13:30;14:30-15:30"));
    }

    @Category({FastTest.class})
    @Test
    public void bookRoom(){//probs needs more test cases involving the db
        //assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(true));
    }

    @Category(FastTest.class)
    @Test
    public void getWorkStationTest(){
        //assertThat(sc.getWorkStation(), equalTo("WORK1"));
    }
}
