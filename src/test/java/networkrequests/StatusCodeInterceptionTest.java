package networkrequests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatusCodeInterceptionTest {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        // Перехват запроса к /status_codes/404
        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });
    }

    @Execution(ExecutionMode.CONCURRENT)
    @Test
    void testMockedStatusCode() {
        page.navigate("https://the-internet.herokuapp.com/status_codes");

        // Клик по ссылке "404"
        page.click("a[href='status_codes/404']");

        // Проверка мок-текста
        Locator responseText = page.locator("h3");
        assertEquals("Mocked Success Response", responseText.textContent());
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }
}