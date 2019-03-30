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

import org.mockito.Mockito;

public class ScheduleControllerTest {
    private ScheduleController sc = new ScheduleController();
    private ArrayList<String> rooms = new ArrayList<>();

    @Mock private DatabaseService dbs;
    @Before
    public void initRooms() {
        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
    }

    @After
    public void clearRooms(){
        rooms.clear();
    }

    @Category(FastTest.class)
    @Test
    public void testGetMaxPeopleSize(){//does not have more rooms than expected(test limitation from maxPeopleContent
        //assertThat(sc.getRoomByCap(20).size(), equalTo(5));
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
        assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(true));
    }


    @Category(FastTest.class)
    @Test
    public void getWorkStationTest(){
        //assertThat(sc.getWorkStation(), equalTo("WORK1"));
    }
}
