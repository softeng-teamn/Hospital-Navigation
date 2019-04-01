package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import testclassifications.FastTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;


public class HomeControllerTest extends ApplicationTest {
    final static String schbtn = "#schedulerBtn";
    final static String mapbtn = "#editBtn";
    final static String reqbtn = "#serviceBtn";


        @Override
    public void start(Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(getClass().getResource("../home.fxml"));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Test
    public void showMapEditor() {
        //ui do stuff!
    }

    @Test
    @Category(UiTest.class)
    public void scheduleSceneTest() throws InterruptedException {
        clickOn(schbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Book a Room"));
    }

    @Test
    @Category(UiTest.class)
    public void mapEditSceneTest() throws InterruptedException {
        clickOn(mapbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getPromptText(), is("Name:"));
    }

    @Test
    @Category(UiTest.class)
    public void requestSceneTest() throws InterruptedException {
        clickOn(reqbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Service Request"));
    }

    @Test
    public void displayPath() {
        // pathfinding/UI test
    }
}