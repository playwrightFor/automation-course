package api;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingApiTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @Test
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true));

        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Перехват запросов
        page.onResponse(response -> {
            if (response.url().contains("/dynamic_loading")) {
                assertEquals(200, response.status());
            }
        });

        page.click("button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertEquals("Hello World!", finishText.textContent().trim());

        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace/trace-success.zip")));
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}

