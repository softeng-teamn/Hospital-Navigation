package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.Reservation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import service.MismatchedDatabaseVersionException;
import testclassifications.*;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Calendar.JUNE;
import static java.util.Calendar.MINUTE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import service.DatabaseService;
import org.mockito.Mock;

import java.util.TimeZone ;




public class ScheduleControllerTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<String> rooms = new ArrayList<>();

    private GregorianCalendar gc = new GregorianCalendar();

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
    public void init() throws SQLException, MismatchedDatabaseVersionException {

        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.insertReservation(reservA)).thenReturn(true).thenReturn(false) ;
        when(dbs.insertReservation(reservB)).thenReturn(false) ;
        when(dbs.insertReservation(reservC)).thenReturn(false) ;



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


        ScheduleController.dbs=dbs ;

    }

    @After
    public void clear(){
        rooms.clear();
        sc.dbs.wipeTables();
    }


    @Test
    @Category({FastTest.class})
    public void insertReservationTest(){//probs needs more test cases involving the db
        // assert that an available room can be booked
        assertThat(sc.insertReservation(reservA), equalTo(true));
        // assert that a booked room cannot be double-booked
        assertThat(sc.insertReservation(reservA), equalTo(false));
        // assert that a non-existant room cannot be booked
        //assertThat(sc.insertReservation(reservC), equalTo(false));


    }


    @Test
    @Category({FastTest.class})
    public void getAvailTimesTest() {
        // check returns available times
        assertThat(sc.getAvailableTimes(roomID, theDate), equalTo(returnList)) ;
    }


}