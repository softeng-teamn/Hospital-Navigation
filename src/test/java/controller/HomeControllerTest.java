package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HomeControllerTest extends Application {
    final String schbtn = "#schedulerBtn";
    final String mapbtn = "#editBtn";
    final String reqbtn = "#serviceBtn";
    @Override
    public void start(Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(getClass().getResource("../home.fxml"));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Test
    @Category(UiTest.class)
    public void scheduleSceneTest() throws InterruptedException {
        clickOn(schbtn);
        press()
        Thread.sleep(200);
        Text text = (Text) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Schedule"));
    }

    @Test
    @Category(UiTest.class)
    public void mapEditSceneTest() throws InterruptedException {
        clickOn(mapbtn);
        Thread.sleep(200);
        Text text = (Text) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Mapedit"));
    }

    @Test
    @Category(UiTest.class)
    public void requestSceneTest() throws InterruptedException {
        clickOn(reqbtn);
        Thread.sleep(200);
        Text text = (Text) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Request"));
    }

    @Test
    public void showMapEditor() {

    }

    @Test
    public void showRequest() {

    }

    @Test
    public void showSchedule() {

    }

    @Test
    public void requestPath() {

    }

    @Test
    public void displayPath() {

    }
}