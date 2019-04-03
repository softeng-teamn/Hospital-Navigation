package controller;

import com.jfoenix.controls.JFXTextField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Node;
import model.request.ITRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.loadui.testfx.GuiTest;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import service.DatabaseService;
import testclassifications.UiTest;

import static controller.Controller.dbs;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import testclassifications.FastTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FulfillRequestControllerTest {

    @Mock DatabaseService dbs;

    /*
    UI TESTS HAVE NOT BEEN COMPLETED
     */


    @Before
    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
    public void init(){
        dbs = mock(DatabaseService.class);
        when(dbs.insertITRequest(itR)).thenReturn(true);
        when(dbs.updateITRequest(itR)).thenReturn(true);
        when(dbs.getITRequest(1337)).thenReturn(itR);
    }
    Node a = new Node (0, 0, "nodeID", "Floor1", "building", "hallway", "longName", "shortName") ;

    ITRequest itR = new ITRequest(1337, "Lost Laptop Battery",a, false);
    @Test
    @Category(FastTest.class)
    public void fulfillRequestTest(){
        dbs.insertITRequest(itR);
        itR.setCompleted(true);
        dbs.updateITRequest(itR);
        assertEquals(dbs.getITRequest(1337).isCompleted(), true);
    }

}
