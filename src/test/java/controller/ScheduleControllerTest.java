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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import service.DatabaseService;
import org.mockito.Mock;


public class ScheduleControllerTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<String> rooms = new ArrayList<>();

    @Mock private DatabaseService dbs;
    @Before
    public void init() {
        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.bookRoom("random", "random2", "random3")).thenReturn(true) ;
        ScheduleController.dbs=dbs ;
    }

    @After
    public void clear(){
        rooms.clear();
    }



    @Category({FastTest.class})
    @Test
    public void bookRoom(){//probs needs more test cases involving the db
        // assert that an available room can be booked
        assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(true));
        // assert that a booked room cannot be double-booked
        assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(false));
        // assert that a non-existant room cannot be booked
        assertThat(sc.bookRoom("ROOM-1", "12122019", "10:30-12:30"), equalTo(false));


    }


}
