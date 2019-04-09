package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.request.ITRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import service.DatabaseService;
import service.ResourceLoader;
import testclassifications.FastTest;
import testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.loadui.testfx.Assertions.verifyThat;

@Category(UiTest.class)
public class ServiceRequestUITest extends ApplicationTest {
    DatabaseService myDBS;

    @Override
    public void start(Stage stage) throws Exception {

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
    public void itTest() {
        assertThat(myDBS.getAllITRequests().size(), is(0));

        JFXListView<Node> listView = GuiTest.find("#list_view");
        clickOn((Node) from(listView).lookup(".list-cell").nth(2).query());

        Pane subSceneHolder = GuiTest.find("#subSceneHolder");
        verifyThat(subSceneHolder.getChildren().size(), is(0));

        Node tgNode = GuiTest.find("#itNode");
        clickOn(tgNode);

        verifyThat(subSceneHolder.getChildren().size(), is(1));

        JFXTextArea description = GuiTest.find("#description");
        JFXComboBox type = GuiTest.find("#type");
        JFXButton submit = GuiTest.find("#submit");

        clickOn(description).write("A description here...");
        clickOn(type).type(KeyCode.DOWN).type(KeyCode.ENTER);
        clickOn(submit);

        ITRequest req = myDBS.getITRequest(0);
        verifyThat(req.getItRequestType(), is(ITRequest.ITRequestType.Accessories));
        verifyThat(req.getNotes(), is("A description here..."));
    }
}
