package networkrequests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class NetworkInterceptionTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        // Перехват запроса и подмена ответа
        context.route("**/dynamic_loading/2", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("""
                                <div id="start">
                                    <button>Start</button>
                                </div>
                                <div id="finish" style="display: block;">
                                    <h4>Mocked Title</h4>
                                </div>
                            """)
            );
        });
    }


    @Test
    void testMockedContent() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/2");
        page.click("button:has-text('Start')");

        Locator title = page.locator("#finish h4");
        assertEquals("Mocked Title", title.textContent());
    }


    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}