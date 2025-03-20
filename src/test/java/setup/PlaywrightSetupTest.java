package setup;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class PlaywrightSetupTest extends BaseTest {

    @Execution(ExecutionMode.CONCURRENT)
    @Test
    void testPlaywrightSetup() {
        page.navigate("https://example.com");
        String title = page.title();
        Assertions.assertEquals("Example Domain", title);
    }
}