package dynamic;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @author Oleg Todor
 * @since 2025-03-19
 */
public class DynamicContentHomeTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }


    @Test
    void testFormSubmission() {

        page.navigate("https://the-internet.herokuapp.com/login");

        page.waitForNavigation(() -> {
            page.fill("#username", "tomsmith");
            page.fill("#password", "SuperSecretPassword!");
            page.click("button[type='submit']");
        });

        page.waitForSelector("text=You logged into a secure area!");
    }

    @Test
    void testNestedFrames() {
        page.navigate("https://the-internet.herokuapp.com/nested_frames");

        // Переключение на фрейм left
        Frame leftFrame = page.frame("frame-left");
        String leftText = leftFrame.locator("body").textContent();
        Assertions.assertTrue(leftText.contains("LEFT"));

        // Переключение на фрейм middle
        Frame middleFrame = page.frame("frame-middle");
        String middleText = middleFrame.locator("body").textContent();
        Assertions.assertTrue(middleText.contains("MIDDLE"));

        // Открытие новой вкладки
        Page newPage = page.context().newPage();
        newPage.navigate("https://the-internet.herokuapp.com");

        // Закрытие новой вкладки
        newPage.close();
    }

    @AfterEach
    void tearDown() {
        playwright.close();
    }
}