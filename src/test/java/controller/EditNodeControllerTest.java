package controller;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;

//public class EditNodeControllerTest extends ApplicationTest {
//
//    @Override
//    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification="Must be able to write the mocked DBS to the static field")
//    public void start(Stage stage) throws Exception {
//
//        Parent mainNode = FXMLLoader.load(ResourceLoader.editNode);
//        stage.setScene(new Scene(mainNode));
//        stage.show();
//        stage.toFront();
//        stage.sizeToScene();
//        stage.setFullScreen(true);
//    }
//
//
//    @Test
//    @Category(UiTest.class)
//    public void changeLongName() {
//        Node beforeModify = DatabaseService.getDatabaseService().getNode("BHALL01402");
//        assertEquals(beforeModify.getLongName(), is("Hallway Intersection 14 Level 2"));
//        clickOn("#long_field").write("super_duper_GREAT_LongField");
//        clickOn("#edit_save_btn");
//        Node afterModify = DatabaseService.getDatabaseService().getNode("BHALL01402");
//        assertEquals(afterModify.getLongName(), is("super_duper_GREAT_LongField"));
//    }
//
//}
