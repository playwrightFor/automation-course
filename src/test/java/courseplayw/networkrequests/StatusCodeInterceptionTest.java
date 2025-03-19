package courseplayw.networkrequests;


import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeInterceptionTest {
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

        // Перехват запроса к /status_codes/404
        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });
    }

    @Test
    void testMockedStatusCode() {
        page.navigate("https://the-internet.herokuapp.com/status_codes");

        // Клик по ссылке "404"
        page.click("a[href='status_codes/404']");

        // Проверка мок-текста
        Locator responseText = page.locator("h3");
        assertEquals("Mocked Success Response", responseText.textContent());
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}