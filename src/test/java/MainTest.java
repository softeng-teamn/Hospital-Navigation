import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.testfx.api.FxRobot;
import database.DatabaseService;
import testclassifications.*;

import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(UiTest.class)
public class MainTest {

    private static final int EXPECTED_INITIAL_WINDOWS = 1;
    private static final int ALLOWABLE_STARTUP_DELAY = 240_000; // seconds
    private static final int CHECK_INTERVAL = 10_000; // seconds

    @Before
    public void setup() throws IOException {
        DatabaseService.getDatabaseService().wipeTables();
    }

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
                e.printStackTrace();
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

        for (int i = 0; i < ALLOWABLE_STARTUP_DELAY/CHECK_INTERVAL; i++) {
            // Give the application a bit to startup
            System.err.println("Waiting for Check: " + i);
            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.err.println("Executing Check: " + i);

            // Test: verify that there are the current number of windows
            try {
                windows = robot.listWindows();
                assertThat(windows.size(), is(EXPECTED_INITIAL_WINDOWS));
                break;
            } catch (AssertionError ae) {
                ae.printStackTrace();
                if (i >= (ALLOWABLE_STARTUP_DELAY/CHECK_INTERVAL - 1)) {
                    thread.interrupt();
                    fail();
                }
            }
        }
        // Kill the application thread
        thread.interrupt();
    }
}