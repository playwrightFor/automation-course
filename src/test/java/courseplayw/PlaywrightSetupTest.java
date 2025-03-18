package courseplayw;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class PlaywrightSetupTest extends BaseTest {

    @Test
    void testPlaywrightSetup() {
        page.navigate("https://example.com");
        String title = page.title();
        Assertions.assertEquals("Example Domain", title);
    }
}