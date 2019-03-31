package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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

    @Mock private DatabaseService dbs;
    @Before
    public void init() {


      //  sc = spy(new ScheduleController());
     //   when(sc.bookRoom())


        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.bookRoom("ROOM2", "12122019", "10:30-12:30")).thenReturn(true) ;
        when(dbs.bookRoom("ROOM1", "12122019", "10:30-12:30")).thenReturn(false) ;
        when(dbs.bookRoom("ROOM-1", "12122019", "10:30-12:30")).thenReturn(false) ;

        ScheduleController.dbs=dbs ;
    }

    @After
    public void clear(){
        rooms.clear();
    }


    @Test
    @Category({FastTest.class})
    public void bookRoom(){//probs needs more test cases involving the db
        // assert that an available room can be booked
        assertThat(sc.bookRoom("ROOM2", "12122019", "10:30-12:30"), equalTo(true));
        // assert that a booked room cannot be double-booked
        assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(false));
        // assert that a non-existant room cannot be booked
        assertThat(sc.bookRoom("ROOM-1", "12122019", "10:30-12:30"), equalTo(false));


    }


}
