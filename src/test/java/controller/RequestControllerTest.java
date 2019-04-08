package controller;

import model.Node;
import model.request.ITRequest;
import model.request.MedicineRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import service.DatabaseService;
import testclassifications.FastTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class RequestControllerTest {
    private Node n = new Node(1, 1 , "ABCD","","","","","");
    private RequestController RC = new RequestController();
    private MedicineRequest medReqR = new MedicineRequest(2,"Fast",n,false);
    private MedicineRequest medReqM = new MedicineRequest(2,"take your time", n, false, "Morphine", 200.0);
    private ITRequest ITReqA = new ITRequest(2,"3/31/2019 1:21 PM",n,false,ITRequest.ITRequestType.New_Computer);
    private ITRequest ITReqB = new ITRequest(3,"4/1/2019 2:30 PM", n, false, ITRequest.ITRequestType.Assistance);

    @Before
    public void setupTest() {
        RequestController.myDBS.insertNode(n);

        medList.add(medReqM);
        medList.add(medReqR);
        ITList.add(ITReqA);
        ITList.add(ITReqB);

        RC = new RequestController();

        RequestController.myDBS.insertMedicineRequest(medReqM);
        RequestController.myDBS.insertMedicineRequest(medReqR);
        RequestController.myDBS.insertITRequest(ITReqA);
        RequestController.myDBS.insertITRequest(ITReqB);
        RequestController.myDBS.insertNode(n);
    }

    @AfterClass
    public static void cleanup() {
        RequestController.myDBS.wipeTables();
    }

    private ArrayList<MedicineRequest> medList = new ArrayList<>();
    private ArrayList<ITRequest> ITList = new ArrayList<>();

    // test makeRequest()
    @Test
    @Category(FastTest.class)
    public void makeITRequestTest () {
        List<ITRequest> requests = RequestController.myDBS.getAllIncompleteITRequests();

        assertThat(requests, is(notNullValue()));

        requests.add(ITReqA);

        //RC.makeRequest(ITReqA);
        List<ITRequest> requests1 = RequestController.myDBS.getAllIncompleteITRequests();

        assertThat(requests1, is(notNullValue()));

        assertThat(requests.containsAll(requests1), equalTo(true));
        requests = requests1;
        //don't make two of the same exact req
        //RC.makeRequest(ITReqA);
        requests1 = RequestController.myDBS.getAllIncompleteITRequests();

        assertThat(requests.size(), is(requests1.size()));
    }

    @Test
    @Category(FastTest.class)
    public void makeMedRequestTest () {
        List<MedicineRequest> requests = RequestController.myDBS.getAllIncompleteMedicineRequests();
        requests.add(medReqM);
        //RC.makeRequest(medReqM);
        List<MedicineRequest> requests1 = RequestController.myDBS.getAllIncompleteMedicineRequests();

        System.out.println(requests);

        System.out.println(requests1);
        assertThat(requests.containsAll(requests1), equalTo(true));

        requests = requests1;
        //don't make two of the same exact req
        //RC.makeRequest(ITReqA);
        requests1 = RequestController.myDBS.getAllIncompleteMedicineRequests();
        assertThat(requests.size() == requests1.size(), equalTo(true));

    }

/*
    @Test
    @Category(FastTest.class)
    public void fulfillITRequestTest () {
        RC.makeRequest(ITReqB);
        //make sure it's not still on the incomplete list
        List<ITRequest> unfilledRequests = RC.dbs.getAllIncompleteITRequests();
        //make sure in unfilled reqs
        assertThat(unfilledRequests.contains(ITReqB), equalTo(true));
        //after filled, not in unfilled reqs, but still in all
        RC.fufillRequest(ITReqB, "Jane");
        ITRequest filledITReqR = ITReqB;
        filledITReqR.setCompleted(true);
        filledITReqR.setCompletedBy("Jane");
        unfilledRequests = RC.dbs.getAllIncompleteITRequests();
        assertThat(unfilledRequests.contains(ITReqB), equalTo(false));
        assertThat(unfilledRequests.contains(filledITReqR), equalTo(false));
        assertThat(RC.dbs.getAllMedicineRequests().contains(filledITReqR), equalTo(true));
    }

    @Test
    @Category(FastTest.class)
    public void fulfillMedicineRequestTest () {
        RC.makeRequest(medReqR);
        //make sure it's not still on the incomplete list
        List<MedicineRequest> unfilledRequests = RC.dbs.getAllIncompleteMedicineRequests();
        //make sure in unfilled reqs
        assertThat(unfilledRequests.contains(medReqR), equalTo(true));
        //after filled, not in unfilled reqs, but still in all
        RC.fufillRequest(medReqR, "Shawn");
        MedicineRequest filledMedReqR = medReqR;
        filledMedReqR.setCompleted(true);
        filledMedReqR.setCompletedBy("Shawn");
        unfilledRequests = RC.dbs.getAllIncompleteMedicineRequests();
        assertThat(unfilledRequests.contains(medReqR), equalTo(false));
        assertThat(unfilledRequests.contains(filledMedReqR), equalTo(false));
        assertThat(RC.dbs.getAllMedicineRequests().contains(filledMedReqR), equalTo(true));
    }

*/
    @After
    public void tearDown() throws Exception { }

}
