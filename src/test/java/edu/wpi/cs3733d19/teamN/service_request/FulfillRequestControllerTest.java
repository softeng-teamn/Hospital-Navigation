package edu.wpi.cs3733d19.teamN.service_request;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.wpi.cs3733d19.teamN.map.Node;
import edu.wpi.cs3733d19.teamN.service_request.FulfillRequestController;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.ITRequest;
import org.junit.Before;

import edu.wpi.cs3733d19.teamN.database.DatabaseService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FulfillRequestControllerTest {
    /*
    UI TESTS HAVE NOT BEEN COMPLETED
     */


    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init(){
        DatabaseService dbs = mock(DatabaseService.class);
        when(dbs.insertITRequest(itR)).thenReturn(true);
        when(dbs.updateITRequest(itR)).thenReturn(true);
        when(dbs.getITRequest(1337)).thenReturn(itR);

        FulfillRequestController.myDBS = dbs;
    }
    Node a = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;

    ITRequest itR = new ITRequest(1337, "Lost Laptop Battery",a, false, ITRequest.ITRequestType.Assistance);
    // TODO: rewrite,
//    @Test
//    @Category(FastTest.class)
//    public void fulfillRequestTest(){
//        DatabaseService.getDatabaseService(true).insertITRequest(itR);
//        itR.setCompleted(true);
//        DatabaseService.getDatabaseService().updateITRequest(itR);
//        assertEquals(DatabaseService.getDatabaseService().getITRequest(1337).isCompleted(), true);
//    }

}
