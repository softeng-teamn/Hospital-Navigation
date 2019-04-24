package edu.wpi.cs3733d19.teamN.regression;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import edu.wpi.cs3733d19.teamN.map.Node;
import net.kurobako.gesturefx.GesturePane;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;
import edu.wpi.cs3733d19.teamN.testclassifications.SlowTest;
import edu.wpi.cs3733d19.teamN.testclassifications.UiTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.util.DebugUtils.informedErrorMessage;

@Category({SlowTest.class, UiTest.class})
public class HomeRegressionSuite extends ApplicationTest {
    Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("start");

        HashMap<String, ImageView> imageCache = new HashMap<>();
        try {
            imageCache.put("4", new ImageView(new Image(ResourceLoader.fourthFloor.openStream())));
            imageCache.put("3", new ImageView(new Image(ResourceLoader.thirdFloor.openStream())));
            imageCache.put("2", new ImageView(new Image(ResourceLoader.secondFloor.openStream())));
            imageCache.put("1", new ImageView(new Image(ResourceLoader.firstFloor.openStream())));
            imageCache.put("G", new ImageView(new Image(ResourceLoader.groundFloor.openStream())));
            imageCache.put("L1", new ImageView(new Image(ResourceLoader.firstLowerFloor.openStream())));
            imageCache.put("L2", new ImageView(new Image(ResourceLoader.secondLowerFloor.openStream())));
        } catch(IOException e) {
            e.printStackTrace();
        }
        ApplicationState.getApplicationState().setImageCache(imageCache);

        DatabaseService.getDatabaseService().wipeTables();
        DatabaseService.getDatabaseService().createFlag = true;
        DatabaseService.getDatabaseService().loadFromCSVsIfNecessary();


        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeWindow(stage, root, "Home");
        stage.setMaximized(true);

        this.stage = stage;
    }


    @Test
    public void testHomeAfterLogin() {
        // General concept: verify correct features show up on the home screen after the user logs in

        verifyThat(stage.getScene().lookup("#edit_btn").isVisible(), is(false), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(false), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#showAdminScene"), is(nullValue()), informedErrorMessage(this));

        clickOn("#auth_btn");

        clickOn("#usernameText").write("staff");
        clickOn("#passwordField").write("staff");

        clickOn("#loginBtn");
        verifyThat(stage.getScene().lookup("#edit_btn"), is(notNullValue()), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#edit_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn"), is(notNullValue()), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(true), informedErrorMessage(this));

        verifyThat(ApplicationState.getApplicationState().getEmployeeLoggedIn(), is(notNullValue()), informedErrorMessage(this));
        verifyThat(ApplicationState.getApplicationState().getEmployeeLoggedIn().getUsername(), is("staff"), informedErrorMessage(this));

        clickOn("#search_bar");

        ListView listView = GuiTest.find("#list_view");

        verifyThat(listView, hasItems(664));

        Cell<Node> listItem = from(listView).lookup(".list-cell").nth(0).query();
        clickOn(listItem);

        verifyThat(stage.getScene().lookup("#edit_btn").isVisible(), is(true), informedErrorMessage(this));
        verifyThat(stage.getScene().lookup("#fulfillBtn").isVisible(), is(true), informedErrorMessage(this));
    }

    @Test
    public void testBasicNavigation() {
        // Verify fuzzy search
        clickOn("#search_bar").write("shatuk");
        verifyThat("#list_view", hasItems(2), informedErrorMessage(this)); // Shattuck street entrance and Shattuck street vending machines

        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);
        type(KeyCode.BACK_SPACE);

        verifyThat("#list_view", hasItems(236), informedErrorMessage(this)); // TOTAL nodes - HALLS - STAIRS

        JFXListView listView = GuiTest.find("#list_view");
        GesturePane map_scrollpane = GuiTest.find("#gPane");

        Group mapContent = (Group) map_scrollpane.getContent();

        // Verify that the initial node is drawn correctly (hard coded)
        verifyThat((int) ((Circle) mapContent.getChildren().get(1)).getCenterX(), is(ApplicationState.getApplicationState().getStartNode().getXcoord()), informedErrorMessage(this));
        verifyThat((int) ((Circle) mapContent.getChildren().get(1)).getCenterY(), is(ApplicationState.getApplicationState().getStartNode().getYcoord()), informedErrorMessage(this));

        // Verify that the navigation button is not initially visible
        boolean navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
        verifyThat(navigateBtnVisible, is(false), informedErrorMessage(this));

        // Verify that the first list item is BTM Conference room
        Cell<HBox> firstListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) firstListItem.getItem().getChildren().get(0)).getText(), is("15 Francis Security Desk"), informedErrorMessage(this));

        // Search for, select and verify list item Shattuck Street Lobby ATM
        clickOn("#search_bar").write("Shattuck Street Lobby ATM").type(KeyCode.ENTER);
        listView = GuiTest.find("#list_view");
        Cell<HBox> targetListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) targetListItem.getItem().getChildren().get(0)).getText(), is("Shattuck Street Lobby ATM"), informedErrorMessage(this));

        // Click on the item
        clickOn(targetListItem, MouseButton.PRIMARY);

        // Verify that the destination node is drawn correctly (based on cell item)
        System.out.println(Arrays.toString(mapContent.getChildren().toArray()));
        verifyThat(((Text) mapContent.getChildren().get(56)).getText(), is("\uE0C8"), informedErrorMessage(this));
        verifyThat((int) ((Text) mapContent.getChildren().get(56)).getTranslateX(), is(ApplicationState.getApplicationState().getEndNode().getXcoord() - 50), informedErrorMessage(this));
        verifyThat((int) ((Text) mapContent.getChildren().get(56)).getTranslateY(), is(ApplicationState.getApplicationState().getEndNode().getYcoord()), informedErrorMessage(this));

        // Verify that the navigation button is now visible
        navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
        verifyThat(navigateBtnVisible, is(true), informedErrorMessage(this));

        // Click on the navigation button
        clickOn("#navigate_btn");

        verifyThat(mapContent.getChildren(), hasSize(63));
    }
}
