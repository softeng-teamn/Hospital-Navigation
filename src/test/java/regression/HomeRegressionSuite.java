package regression;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Cell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Node;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;
import testclassifications.RegressionTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.util.DebugUtils.informedErrorMessage;

@Category({RegressionTest.class})
public class HomeRegressionSuite extends ApplicationTest {
    Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("start");

        try {
            Parent root = FXMLLoader.load(ResourceLoader.home);
            StageManager.changeWindow(stage, root, "Home");
            stage.setMaximized(true);
        } catch (LoadException e) {

        }
        this.stage = stage;
    }

    @After
    public void cleanup() {
        System.out.println("Cleanup!");
        DatabaseService.getDatabaseService().wipeTables();
    }


    @Test
    public void testHomeAfterLogin() {
        // General concept: verify correct features show up on the home screen after the user logs in

        verifyThat(stage.getScene().lookup("#edit_VBox"), is(nullValue()), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#edit_btn"), is(nullValue()), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#newRoom_btn"), is(nullValue()), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn"), is(nullValue()), informedErrorMessage(this));

        clickOn("#auth_btn");

        clickOn("#idText").write("1234");
        clickOn("#passwordField").write("test");

        clickOn("#loginBtn");
        verifyThat(stage.getScene().lookup("#edit_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#newRoom_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#edit_VBox"), is(nullValue()), informedErrorMessage(this));

        ListView listView = GuiTest.find("#list_view");

        verifyThat(listView, hasItems(605));

        Cell<Node> listItem = from(listView).lookup(".list-cell").nth(0).query();
        clickOn(listItem);

        verifyThat(stage.getScene().lookup("#edit_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#newRoom_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#edit_VBox"), is(nullValue()), informedErrorMessage(this));

        clickOn("#edit_btn");
        // TODO: determine how to verify edit pane comes up
        // verifyThat(stage.getScene().lookup("#edit_btn"), is(false), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#newRoom_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#edit_VBox").isVisible(), is(true), informedErrorMessage(this));
    }

    @Test
    public void testBasicNavigation() {
        // Verify fuzzy search
        clickOn("#search_bar").write("shatuk");
        type(KeyCode.ENTER);
        verifyThat("#list_view", hasItems(2), informedErrorMessage(this)); // Shattuck street entrance and Shattuck street vending machines

        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.ENTER);

        verifyThat("#list_view", hasItems(198), informedErrorMessage(this)); // TOTAL nodes - HALLS - STAIRS

        JFXListView listView = GuiTest.find("#list_view");
        ScrollPane map_scrollpane = GuiTest.find("#map_scrollpane");

        Group mapContent = (Group) map_scrollpane.getContent();
        // Nodes and paths
        Group drawnContent = (Group) mapContent.getChildren().get(0);

        // Verify that the initial node is drawn correctly (hard coded)
        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterX(), is(1748), informedErrorMessage(this));
        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterY(), is(1321), informedErrorMessage(this));

        // Verify that the navigation button is not initially visible
        boolean navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
        verifyThat(navigateBtnVisible, is(false), informedErrorMessage(this));

        // Verify that the first list item is BTM Conference room
        Cell<Node> firstListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(firstListItem.getItem().getLongName(), is("BTM Conference Center"), informedErrorMessage(this));

        // Search for, select and verify list item Shattuck Street Lobby ATM
        clickOn("#search_bar").write("Shattuck Street Lobby ATM");
        type(KeyCode.ENTER);
        Cell<Node> targetListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(targetListItem.getItem().getLongName(), is("Shattuck Street Lobby ATM"), informedErrorMessage(this));

        // Click on the item
        clickOn(targetListItem, MouseButton.PRIMARY);

        // Verify that the destination node is drawn correctly (based on cell item)
        verifyThat((int) ((Circle) drawnContent.getChildren().get(1)).getCenterX(), is(targetListItem.getItem().getXcoord()), informedErrorMessage(this));
        verifyThat((int) ((Circle) drawnContent.getChildren().get(1)).getCenterY(), is(targetListItem.getItem().getYcoord()), informedErrorMessage(this));

        // Verify that the navigation button is now visible
        navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
        verifyThat(navigateBtnVisible, is(true), informedErrorMessage(this));

        // Click on the navigation button
        clickOn("#navigate_btn");

        // 1 base pane, 2 node circles, and 12 segments
        verifyThat(drawnContent.getChildren(), hasSize(1 + 2 + 12));
    }
}
