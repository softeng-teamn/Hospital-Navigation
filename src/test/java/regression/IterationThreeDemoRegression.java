package regression;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTimePicker;
import database.DatabaseService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import map.Node;
import net.kurobako.gesturefx.GesturePane;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import scheduler.controller.ScheduleController;
import service.ResourceLoader;
import service.StageManager;
import testclassifications.FastTest;
import testclassifications.SlowTest;
import testclassifications.UiTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.util.DebugUtils.informedErrorMessage;

@Category({SlowTest.class, UiTest.class})
public class IterationThreeDemoRegression extends ApplicationTest {
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
    public void iterationThreeDemoRegression() {
        // 1. Pathfind BTM Conference Center to Amphitheater - expect 6
        testPathfind("BTM Conference Center", "Flexible Amphitheater", 46);

        // 2. Login to staff
        login("staff", "staff");

        // 2.1. Switch to Best First, pathfind to Obstetrics
        switchPathfinding("BEST");
        testPathfind("Cafe", "Obstetrics", 62);

        // 2.2. Switch to Dijktra's, pathfind to Obstetrics
        switchPathfinding("DIJSKTRA");
        testPathfind("Cafe", "Obstetrics", 62);

        // 2.3. Switch to A*, pathfind to Obstetrics
        switchPathfinding("ASTAR");
        testPathfind("Cafe", "Obstetrics", 63);

        // 3. Set Dan's phone to (1234567890)
        clickOn("#fulfillBtn");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clickOn("#editEmployeeBtn");
        TableView employeeEditTable = GuiTest.find("#employee_table");
        Cell<HBox> danPhoneCell = from(employeeEditTable).lookup(".table-cell").nth(9).query();
        doubleClickOn(danPhoneCell).write("1234567890").type(KeyCode.ENTER);
        clickOn("#homeBtn");

        // 4. Logout
        logout();

        // 5. Login to dan (dduff, duff)
        login("dduff", "duff");
        assertThat(ApplicationState.getApplicationState().getEmployeeLoggedIn().getPhone(), is("1234567890"));

        // 6. Navigate BTM Conference Center to Amphitheater
        testPathfind("BTM Conference Center", "Flexible Amphitheater", 46);
        // 6.1. Text directions -- omitted - no way to test that texts are sent
        // 6.2. Show QR Code
        clickOn("#viewQRCodeBtn");
        verifyThat(GuiTest.find("#qrCodeVbox").isVisible(), is(true), informedErrorMessage(this));

        // 7. Logout, login to staff
        logout();
        login("staff", "staff");

        // 8. Open scheduler
        clickOn("#bookBtn");
        clickOn("#mapAvailRooms");
        clickOn("#listAvailRooms");
        clickOn("#weeklyScheduleTab");
        clickOn("#dailyScheduleTab");
        clickOn("#mapAvailRooms");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 8.1. Make birthday in classroom (public)
        clickOn("#classroom4");
        verifyThat(((Label)GuiTest.find("#schedLbl")).getText().contains("Classroom 1"), is(true), informedErrorMessage(this));
        JFXDatePicker datePicker = GuiTest.find("#datePicker");
        JFXTimePicker startTimePicker = GuiTest.find("#startTimePicker");
        JFXTimePicker endTimePicker = GuiTest.find("#endTimePicker");
        datePicker.setValue((LocalDate.of(2042, 4, 2)));
        startTimePicker.setValue(LocalTime.of(11, 0));
        endTimePicker.setValue(LocalTime.of(15, 0));
        clickOn("#makeReservationBtn");

        assertThat(((Label) GuiTest.find("#resInfoLbl")).getText(), is("Location:      " + "Classroom 1"
                + "\n\nDate:            " + "Wed Apr 02, 2042"
                + "\n\nStart Time:   " + "11" + ":" + "00"
                + "\n\nEnd Time:    " + "15" + ":" + "00"));

        clickOn("#eventName").write("Birthday");
        clickOn("#privacyLvlBox").type(KeyCode.DOWN); // Public
        clickOn("#makeReservationBtn");

        verifyThat(((SVGPath) GuiTest.find("#classroom4")).getFill(), is(ScheduleController.AVAILABLE_COLOR), informedErrorMessage(this));
        datePicker = GuiTest.find("#datePicker");
        startTimePicker = GuiTest.find("#startTimePicker");
        endTimePicker = GuiTest.find("#endTimePicker");
        datePicker.setValue((LocalDate.of(2042, 4, 2)));
        startTimePicker.setValue(LocalTime.of(12, 0));
        endTimePicker.setValue(LocalTime.of(14, 0));
        clickOn("#auditorium"); // Note: colors don't updated based on time set like above unless click is made
        verifyThat(((SVGPath) GuiTest.find("#classroom4")).getFill(), is(ScheduleController.UNAVAILABLE_COLOR), informedErrorMessage(this));

        // 8.2. Make other in amphitheater (private)
        // 8.1. Make birthday in classroom (public)
        clickOn("#auditorium");
        verifyThat(((Label)GuiTest.find("#schedLbl")).getText().contains("Amphitheater"), is(true), informedErrorMessage(this));
        datePicker = GuiTest.find("#datePicker");
        startTimePicker = GuiTest.find("#startTimePicker");
        endTimePicker = GuiTest.find("#endTimePicker");
        datePicker.setValue((LocalDate.of(2042, 4, 2)));
        startTimePicker.setValue(LocalTime.of(12, 0));
        endTimePicker.setValue(LocalTime.of(14, 0));
        clickOn("#makeReservationBtn");

        assertThat(((Label) GuiTest.find("#resInfoLbl")).getText(), is("Location:      " + "Amphitheater"
                + "\n\nDate:            " + "Wed Apr 02, 2042"
                + "\n\nStart Time:   " + "12" + ":" + "00"
                + "\n\nEnd Time:    " + "14" + ":" + "00"));

        clickOn("#eventName").write("Other Event");
        clickOn("#privacyLvlBox").type(KeyCode.DOWN).type(KeyCode.DOWN); // Private
        clickOn("#makeReservationBtn");

        verifyThat(((SVGPath) GuiTest.find("#auditorium")).getFill(), is(ScheduleController.SELECT_AVAILABLE_COLOR), informedErrorMessage(this));
        datePicker = GuiTest.find("#datePicker");
        startTimePicker = GuiTest.find("#startTimePicker");
        endTimePicker = GuiTest.find("#endTimePicker");
        datePicker.setValue((LocalDate.of(2042, 4, 2)));
        startTimePicker.setValue(LocalTime.of(12, 0));
        endTimePicker.setValue(LocalTime.of(14, 0));
        clickOn("#auditorium"); // Note: colors don't updated based on time set like above unless click is made
        verifyThat(((SVGPath) GuiTest.find("#auditorium")).getFill(), is(ScheduleController.SELECT_UNAVAILABLE_COLOR), informedErrorMessage(this));

        // 8.3. Return to pathfinding and pathfind to classroom
        clickOn("#homeBtn");
        JFXListView listView;
        GesturePane map_scrollpane = GuiTest.find("#gPane");
        Group mapContent = (Group) map_scrollpane.getContent();

        clickOn("#startNode_btn");

        doubleClickOn("#startSearch").write("Cafe").type(KeyCode.ENTER);
        listView = GuiTest.find("#list_view");

        Cell<HBox> startListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) startListItem.getItem().getChildren().get(0)).getText(), is("Cafe"), informedErrorMessage(this));
        clickOn(startListItem);
        assertThat(ApplicationState.getApplicationState().getStartNode(), is(notNullValue()));

        doubleClickOn("#search_bar").write("Birthday").type(KeyCode.ENTER);
        listView = GuiTest.find("#list_view");

        Cell<HBox> endListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) endListItem.getItem().getChildren().get(0)).getText(), is("Event: Birthday"), informedErrorMessage(this));

        // Click on the item
        clickOn(endListItem, MouseButton.PRIMARY);

        // Click on the navigation button
        clickOn("#navigate_btn");

        verifyThat(mapContent.getChildren(), hasSize(60));

        clickOn("#startNode_btn");
    }

    private void switchPathfinding(String strategy) {
        clickOn("#fulfillBtn");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (strategy) {
            case "BEST":
                clickOn("#bestFirstToggle");
                break;
            case "ASTAR":
                clickOn("#aStarToggle");
                break;
            case "DEPTH":
                clickOn("#depthFirstToggle");
                break;
            case "DIJSKTRA":
                clickOn("#dijsktraToggle");
                break;
            case "BREADTH":
                clickOn("#breadthFirstToggle");
                break;
            default:
                fail();
        }
    }

    private void logout() {
        clickOn("#auth_btn");
        assertThat(ApplicationState.getApplicationState().getEmployeeLoggedIn(), is(nullValue()));
    }

    private void login(String username, String password) {
        clickOn("#auth_btn");

        clickOn("#usernameText").write(username);
        clickOn("#passwordField").write(password);

        clickOn("#loginBtn");

        assertThat(ApplicationState.getApplicationState().getEmployeeLoggedIn().getUsername(), is(username));
    }

    private void testPathfind(String from, String to, int expectedChildrenInGroup) {
        JFXListView listView;
        GesturePane map_scrollpane = GuiTest.find("#gPane");
        Group mapContent = (Group) map_scrollpane.getContent();

        clickOn("#startNode_btn");

        doubleClickOn("#startSearch").write(from).type(KeyCode.ENTER);
        listView = GuiTest.find("#list_view");

        Cell<HBox> startListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) startListItem.getItem().getChildren().get(0)).getText(), is(from), informedErrorMessage(this));
        clickOn(startListItem);
        assertThat(ApplicationState.getApplicationState().getStartNode(), is(notNullValue()));

        doubleClickOn("#search_bar").write(to).type(KeyCode.ENTER);
        listView = GuiTest.find("#list_view");

        Cell<HBox> endListItem = from(listView).lookup(".list-cell").nth(0).query();
        verifyThat(((Label) endListItem.getItem().getChildren().get(0)).getText(), is(to), informedErrorMessage(this));

        // Click on the item
        clickOn(endListItem, MouseButton.PRIMARY);

        // Click on the navigation button
        clickOn("#navigate_btn");

        verifyThat(mapContent.getChildren(), hasSize(expectedChildrenInGroup));

        clickOn("#startNode_btn");
    }
}
