package controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.request.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import service.DatabaseService;
import service.ResourceLoader;
import testclassifications.FastTest;
import testclassifications.UiTest;

import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
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
        Parent mainNode = FXMLLoader.load(ResourceLoader.request);
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
        assertThat(true, is(true));
    }

    @Test
    @Category(FastTest.class)
    public void itTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllITRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
        verifyThat(req.getItRequestType(), is(ITRequest.ITRequestType.Accessories));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(FastTest.class)
    public void toyTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllToyRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
    @Category(FastTest.class)
    public void securityTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllSecurityRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
        verifyThat(req.getUrgency(), is(SecurityRequest.Urgency.VERY));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(FastTest.class)
    public void sanitationTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllSanitationRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
        verifyThat(req.getUrgency(), is("Low"));
        verifyThat(req.getMaterialState(), is("Solid"));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(FastTest.class)
    public void patientInfoTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllPatientInfoRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
        verifyThat(req.getFirstName(), is("John"));
        verifyThat(req.getLastName(), is("Doe"));
        verifyThat(req.getBirthDay(), is("20190409"));
        verifyThat(req.getNotes(), is("A description here..."));
    }

    @Test
    @Category(FastTest.class)
    public void medicineRequestTest() {
        // Verify no requests of this type exist
        assertThat(myDBS.getAllMedicineRequests().size(), is(0));

        // Get and click on a location
        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        // Verify no subscene is present
        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        // Click on the request type
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
        verifyThat(req.getMedicineType(), is("Something"));
        verifyThat(req.getNotes(), is("A description here..."));
    }


//    moveBy(500, 20);
//    scroll(40, VerticalDirection.DOWN);
}
