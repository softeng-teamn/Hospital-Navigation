package edu.wpi.cs3733d19.teamN.controller;

import com.jfoenix.controls.*;
import edu.wpi.cs3733d19.teamN.service_request.model.sub_model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.testclassifications.FastTest;
import edu.wpi.cs3733d19.teamN.testclassifications.SlowTest;
import edu.wpi.cs3733d19.teamN.testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.DebugUtils.informedErrorMessage;

@Category(UiTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceRequestUITest extends ApplicationTest {
    DatabaseService myDBS;

    @Override
    public void start(Stage stage) throws Exception {
        myDBS = DatabaseService.getDatabaseService();
        Parent mainNode = FXMLLoader.load(ResourceLoader.request, ResourceLoader.dfBundle);
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        stage.sizeToScene();
        stage.setFullScreen(true);
    }

    @Before
    public void setup() {
        myDBS = DatabaseService.getDatabaseService();
        myDBS.wipeTables();
        myDBS.createFlag = true;
        myDBS.loadFromCSVsIfNecessary();
    }

    @Test
    @Category(FastTest.class)
    public void aTest() {
        // This is a test that must run first because the listview doesn't properly load for the first test

        // I don't know why it doesn't work
        assertThat(true, is(true));
    }

    @Test
    @Category(SlowTest.class)
    public void itTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllITRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#itSelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#description");
        JFXComboBox type = GuiTest.find("#type");
        JFXButton submit = GuiTest.find("#submit");
        clickOn(description).write("A description here...");
        clickOn(type).type(KeyCode.DOWN).type(KeyCode.ENTER);

        // Submit
        clickOn(submit);

        // Verify submission in database
        ITRequest req = myDBS.getITRequest(0);
        verifyThat(req, is(notNullValue()), informedErrorMessage(this));
        verifyThat(req.getItRequestType(), is(ITRequest.ITRequestType.New_Computer));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(SlowTest.class)
    public void toyTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllToyRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#toySelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#description");
        JFXTextField toy = GuiTest.find("#toy");
        JFXButton submit = GuiTest.find("#submit");
        clickOn(description).write("A description here...");
        clickOn(toy).write("Monopoly");

        // Submit
        clickOn(submit);

        // Verify submission in database
        ToyRequest req = myDBS.getToyRequest(0);
        verifyThat(req.getToyName(), is("Monopoly"));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(SlowTest.class)
    public void securityTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllSecurityRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#securitySelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#description");
        JFXToggleNode med = GuiTest.find("#urgency_high");
        JFXButton submit = GuiTest.find("#submit");
        clickOn(description).write("A description here...");
        clickOn(med);

        // Submit
        clickOn(submit);

        // Verify submission in database
        SecurityRequest req = myDBS.getSecurityRequest(0);
        verifyThat(req, is(notNullValue()), informedErrorMessage(this));
        verifyThat(req.getUrgency(), is(SecurityRequest.Urgency.VERY));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Ignore
    @Category(SlowTest.class)
    public void sanitationTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllSanitationRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#sanitationSelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#notes");
        JFXComboBox urgency = GuiTest.find("#urgencyBox");
        JFXComboBox material = GuiTest.find("#materialBox");
        JFXButton submit = GuiTest.find("#submitBtn");
        clickOn(description).write("A description here...");
        clickOn(urgency).type(KeyCode.DOWN).type(KeyCode.ENTER);
        clickOn(material).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);

        // Submit
        clickOn(submit);

        // Verify submission in database
        SanitationRequest req = myDBS.getSanitationRequest(0);
        verifyThat(req, is(notNullValue()), informedErrorMessage(this));
        verifyThat(req.getUrgency(), is("Medium"));
        verifyThat(req.getMaterialState(), is("Mixture"));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(SlowTest.class)
    public void patientInfoTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllPatientInfoRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#patientSelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#descriptionArea");
        JFXTextField firstName = GuiTest.find("#firstNameField");
        JFXTextField lastName = GuiTest.find("#lastNameField");
        JFXTextField year = GuiTest.find("#birthYField");
        JFXTextField month = GuiTest.find("#birthMField");
        JFXTextField day = GuiTest.find("#birthDField");
        JFXButton submit = GuiTest.find("#submit");
        clickOn(description).write("A description here...");
        clickOn(firstName).write("John");
        clickOn(lastName).write("Doe");
        clickOn(year).write("2019");
        clickOn(month).write("04");
        clickOn(day).write("09");

        // Submit
        clickOn(submit);

        // Verify submission in database
        PatientInfoRequest req = myDBS.getPatientInfoRequest(0);
        verifyThat(req, is(notNullValue()), informedErrorMessage(this));
        verifyThat(req.getFirstName(), is("John"));
        verifyThat(req.getLastName(), is("Doe"));
        verifyThat(req.getBirthDay(), is("04/09/2019"));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(SlowTest.class)
    public void medicineRequestTest() {
        // Verify no service_request of this getType exist
        assertThat(myDBS.getAllMedicineRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Get the subscene
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");

        // Click on the service_request getType
        Node tgNode = GuiTest.find("#medicineSelectNode");
        clickOn(tgNode);

        // Verify subscene appears
        verifyThat(subSceneHolder.getChildren().size(), is(1));

        // Get and Populate fields
        JFXTextArea description = GuiTest.find("#description");
        JFXTextField type = GuiTest.find("#medicineType");
        JFXTextField quantity = GuiTest.find("#quantity");
        JFXButton submit = GuiTest.find("#submit");
        clickOn(description).write("A description here...");
        clickOn(quantity).write("12");
        clickOn(type).write("Something");

        // Submit
        clickOn(submit);

        // Verify submission in database
        MedicineRequest req = myDBS.getMedicineRequest(0);
        verifyThat(req, is(notNullValue()), informedErrorMessage(this));
        verifyThat(req.getMedicineType(), is("Something"));
        verifyThat(req.getNotes(), is("A description here..."));
    }


//    moveBy(500, 20);
//    scroll(40, VerticalDirection.DOWN);
}
