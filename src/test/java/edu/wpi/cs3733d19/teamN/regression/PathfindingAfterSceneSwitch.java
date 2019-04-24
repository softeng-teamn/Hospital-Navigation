package edu.wpi.cs3733d19.teamN.regression;

import org.junit.FixMethodOrder;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import edu.wpi.cs3733d19.teamN.testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.DebugUtils.informedErrorMessage;

@Category(UiTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PathfindingAfterSceneSwitch extends ApplicationTest {
//    private DatabaseService myDBS;
//    private Stage stage;
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        setup();
//        myDBS = DatabaseService.getDatabaseService();
//        this.stage = stage;
//        Parent mainNode = FXMLLoader.load(ResourceLoader.home);
//        stage.setScene(new Scene(mainNode));
//        stage.show();
//        stage.toFront();
//        stage.setFullScreen(true);
//    }
//
////    @Before
//    public void setup() {
//        HashMap<String, ImageView> imageCache = new HashMap<>();
//        try {
//            imageCache.put("3", new ImageView(new Image(ResourceLoader.thirdFloor.openStream())));
//            imageCache.put("2", new ImageView(new Image(ResourceLoader.secondFloor.openStream())));
//            imageCache.put("1", new ImageView(new Image(ResourceLoader.firstFloor.openStream())));
//            imageCache.put("L1", new ImageView(new Image(ResourceLoader.firstLowerFloor.openStream())));
//            imageCache.put("L2", new ImageView(new Image(ResourceLoader.secondLowerFloor.openStream())));
//            imageCache.put("G", new ImageView(new Image(ResourceLoader.groundFloor.openStream())));
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
//        ApplicationState.getApplicationState().setImageCache(imageCache);
//        myDBS = DatabaseService.getDatabaseService();
//        myDBS.wipeTables();
//        myDBS.createFlag = true;
//        myDBS.loadFromCSVsIfNecessary();
//    }
//
//    @Test
//    @Category(FastTest.class)
//    public void aTest() {
//        // This is a test that must run first because the listview doesn't properly load for the first test
//
//        // I don't know why it doesn't work
//        assertThat(true, is(true));
//    }

//    @Test
//    @Ignore
//    @Category(SlowTest.class)
//    public void pathFindAfterSceneSwitch() {
//        JFXListView listView = GuiTest.find("#list_view");
//        ScrollPane map_scrollpane = GuiTest.find("#map_scrollpane");
//        Group mapContent = (Group) map_scrollpane.getContent();
//        // Nodes and paths
//        Group drawnContent = (Group) mapContent.getChildren().get(0);
//
//        // Verify that the initial node is drawn correctly (hard coded)
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(1)).getCenterX(), is(1619), informedErrorMessage(this));
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(1)).getCenterY(), is(2522), informedErrorMessage(this));
//
//        Cell<Node> targetListItem = from(listView).lookup(".list-cell").nth(0).query();
//        // Click on the item
//        clickOn(targetListItem);
//
//        // Verify that the destination node is drawn correctly (based on cell item)
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterX(), is(targetListItem.getItem().getXcoord()), informedErrorMessage(this));
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterY(), is(targetListItem.getItem().getYcoord()), informedErrorMessage(this));
//
//        // Verify that the navigation button is now visible
//        boolean navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
//        verifyThat(navigateBtnVisible, is(true), informedErrorMessage(this));
//
//        // Click on the navigation button
//        clickOn("#navigate_btn");
//
//        // 1 base pane, 2 node circles, and 62 segments
//        System.out.println(drawnContent.getChildren());
//        verifyThat(drawnContent.getChildren(), hasSize(1 + 2 + 62));
//
//        clickOn("#auth_btn");
//        clickOn("#cancelBtn");
//
//
//        // Reretrieve stuff
//        listView = GuiTest.find("#list_view");
//        map_scrollpane = GuiTest.find("#map_scrollpane");
//        mapContent = (Group) map_scrollpane.getContent();
//        // Nodes and paths
//        drawnContent = (Group) mapContent.getChildren().get(0);
//
//
//        targetListItem = from(listView).lookup(".list-cell").nth(0).query();
//        // Click on the item
//        clickOn(targetListItem);
//
//        // Verify that the destination node is drawn correctly (based on cell item)
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterX(), is(targetListItem.getItem().getXcoord()), informedErrorMessage(this));
//        verifyThat((int) ((Circle) drawnContent.getChildren().get(2)).getCenterY(), is(targetListItem.getItem().getYcoord()), informedErrorMessage(this));
//
//        // Verify that the navigation button is now visible
//        navigateBtnVisible = stage.getScene().lookup("#navigate_btn").isVisible();
//        verifyThat(navigateBtnVisible, is(true), informedErrorMessage(this));
//
//        // Click on the navigation button
//        clickOn("#navigate_btn");
//
//        // 1 base pane, 2 node circles, and 62 segments
//        System.out.println(drawnContent.getChildren());
//        verifyThat(drawnContent.getChildren(), hasSize(1 + 2 + 62));
//    }
}
