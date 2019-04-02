package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

//import org.loadui.testfx.GuiTest;
//import org.testfx.framework.junit.ApplicationTest;
import testclassifications.UiTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import testclassifications.FastTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class AdminControllerTest{
    final static String loginbtn = "#logInBtn";
    final static String cancelbtn = "#cancelBtn";
    final static String editmapbtn = "#editMapBtn";
    final static String fulfillrequestbtn = "#fulfillRequestBtn";
//    private Employee emp1 = new Employee(12345, doctor, true);
//    private Employee emp2 = new Employee(6789, nurse, false);
//    private


//    @Mock private DatabaseEmployee dbs;
    @Before
//    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
//    public void setup() throws Exception{
//
//        dbs.insertEmployee(emp1);
//        dbs.insertEmployee(emp2);
//
//    }

    @Test
    @Category(UiTest.class)
    public void editMapSceneTest() throws InterruptedException {
        clickOn(editmapbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Map Editor"));
    }

    @Test
    @Category(UiTest.class)
    public void fulfillRequestSceneTest() throws InterruptedException {
        clickOn(fulfillrequestbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Fulfill Request"));
    }

    @Test
    @Category(UiTest.class)
    public void homeSceneTest() throws InterruptedException {
        clickOn(cancelbtn);
        Thread.sleep(200);
        JFXTextField text = (JFXTextField) GuiTest.find("#SceneID");
        assertThat(text.getText(), is("Home (Path Finder)"));
    }

    /*@Test
    @Category(UiTest.class){
        public void logInTest() {

        }
    }*/

}