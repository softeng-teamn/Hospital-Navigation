package controller;

import model.Node;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import service.DatabaseService;
import testclassifications.FastTest;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestControllerTest {

    @Mock private DatabaseService dbs;

    RequestController reqCrl1 ;
    Request request;
    MedicineRequest mReq;
    ITRequest itReq;


    @Before
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.insertITRequest(itReq)).thenReturn(true);
        when(dbs.insertMedicineRequest(mReq)).thenReturn(true);
        reqCrl1.dbs = dbs;
    }


    // test showHome()
    @Test
    @Category(FastTest.class)
    public void showHomeTest () {
        // test if switching screens
        // will do research, UI help?!
    }

    // test makeRequest()
    @Test
    @Category(FastTest.class)
    public void makeRequestTest () {
        reqCrl1.makeRequest(request);
        assertThat(reqCrl1.getPendingRequests().contains(mReq), equalTo(true));
    }


    // test fufillRequest ()
    @Test
    @Category(FastTest.class)
    public void fufillRequestTest () {
//        reqCrl2.getPendingRequests().add(newReq) ;
//        reqCrl2.fufillRequest("ID", "Someone");
//        assertFalse(reqCrl2.getPendingRequests().contains(newReq));
    }

    @After
    public void tearDown() throws Exception { }
}
