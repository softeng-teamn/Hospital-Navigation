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

import java.sql.SQLException;
import java.sql.SQLOutput;
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


    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init() throws SQLException, MismatchedDatabaseVersionException {

      //  sc = spy(new ScheduleController());
     //   when(sc.bookRoom())
        sc.dbs = DatabaseService.init("hospital-db-test");

        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
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
    public void showAvailTimesTest() {

    }


}
