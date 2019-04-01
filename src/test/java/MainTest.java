import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.testfx.api.FxRobot;
import testclassifications.*;

import javafx.stage.Window;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(UiTest.class)
public class MainTest {

    private static final int EXPECTED_INITIAL_WINDOWS = 1;
    private static final int ALLOWABLE_STARTUP_DELAY = 10000; // milliseconds

    @Test
    @Category({SlowTest.class})
    public void testMain() {

        // This test serves two purposes:
        // 1) It verifies that the correct number of windows initialize when main is run and,
        // 2) It tests that the application startup time is adequate

        // Must run Main in a separate thread as it blocks
        Thread thread = new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                // This is to catch InterruptedExceptions, which it doesn't technically throw
            }
        });
        thread.start();

        // Create a robot
        // Note: this is not standard for UI tests. Normally the test class extends ApplicationTest, which provides
        // a robot
        FxRobot robot = new FxRobot();
        List<Window> windows;

        // Precondition: grab windows and verify that none exist yet
        // Must catch the assertion so that we can kill the application thread, then manually fail.
        try {
            windows = robot.listWindows();
            assertThat(windows.size(), is(0));
        } catch (AssertionError ae) {
            ae.printStackTrace();
            thread.interrupt();
            fail();
        }

        // Give the application a bit to startup
        try {
            Thread.sleep(ALLOWABLE_STARTUP_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test: verify that there are the current number of windows
        try {
            windows = robot.listWindows();
            assertThat(windows.size(), is(EXPECTED_INITIAL_WINDOWS));
        } catch (AssertionError ae) {
            ae.printStackTrace();
            thread.interrupt();
            fail();
        }

        // Kill the application thread
        thread.interrupt();
    }
}