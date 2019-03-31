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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestControllerTest {

    @Mock private DatabaseService dbs;

    Node n = new Node(1, 1 , "","","","","","");

    RequestController reqCrl1 ;
    Request request;
    MedicineRequest mReq;
    MedicineRequest medReqR = new MedicineRequest("A1001","Fast",n,false,"Ritalin");
    MedicineRequest medReqM = new MedicineRequest("A1002","take your time", n, false, "Morphine");
    ITRequest ITReqA = new ITRequest("A1001","3/31/2019 1:21 PM",n,false,"Hard Drive Broke When I peed on it");
    ITRequest ITReqB = new ITRequest("A1002","4/1/2019 2:30 PM", n, false, "Can't wifi");

    MedicineRequest c;
    ITRequest itReq;

    ArrayList<MedicineRequest> medList = new ArrayList<MedicineRequest>();
    ArrayList<ITRequest> ITList = new ArrayList<ITRequest>();

    @Before
    public void setUp() throws Exception {
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.getAllIncompleteMedicineRequests()).thenReturn(medList);
        when(dbs.getAllIncompleteITRequests()).thenReturn(ITList);
        reqCrl1.dbs = dbs;
        medList.add(medReqM);
        medList.add(medReqR);
        ITList.add(ITReqA);
        ITList.add(ITReqB);
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
