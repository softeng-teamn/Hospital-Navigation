package controller;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import service.DatabaseService;
import org.mockito.Mock;

public class MapEditControllerTest {

    private MapEditController mec = new MapEditController();
    @Mock private DatabaseService dbs;

    @Before
    public void setup() {
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.bookRoom("random", "random2", "random3")).thenReturn(true);
        ScheduleController.dbs = dbs;
    }

    @Test
    @Category(FastTest.class)
    public void insertNodeTest(){

    }
}
