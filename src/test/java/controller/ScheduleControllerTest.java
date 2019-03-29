package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

public class ScheduleControllerTest {
    private ScheduleController sc;
    private ArrayList<String> rooms = new ArrayList<>();

    @Before
    public void initRooms() {
        rooms.add(0, "ROOM1");
        rooms.add(1, "ROOM2");
    }

    @After
    public void clearRooms(){
        rooms.clear();
    }


    @Test
    public void testGetMaxPeopleContent(){//has all the right rooms
        assertThat(sc.getMaxPeople(20), containsInAnyOrder(rooms));
    }

    @Test
    public void testGetMaxPeopleSize(){//does not have more rooms than expected(test limitation from maxPeopleContent
        assertThat(sc.getMaxPeople(20).size(), equalTo(5));
    }

    @Test
    public void getDay(){
        assertThat(sc.getDay(), equalTo("12122019"));
    }

    @Test
    public void getRoom() {
        assertThat(sc.getRoom(), equalTo("ROOM1"));
    }

    @Test
    public void getRoomSched(){
        assertThat(sc.getRoomSched("ROOM1", "12122019"), equalTo("10:30-13:30;14:30-15:30"));
    }

    @Test
    public void bookRoom(){//probs needs more test cases involving the db
        assertThat(sc.bookRoom("ROOM1", "12122019", "10:30-12:30"), equalTo(true));
    }

    @Test
    public void a(){
        assertThat(sc.getWorkStation(), equalTo("WORK1"));
    }
}
