package selectors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class HoverTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://the-internet.herokuapp.com/hovers");
    }

    @Test
    void testHoverProfiles() {
        // 1. Находим все элементы с классом .figure
        Locator figures = page.locator(".figure");
        int figureCount = figures.count();

        // 2. Проверяем для каждого элемента
        for (int i = 0; i < figureCount; i++) {
            Locator figure = figures.nth(i);

            // 3. Наводим курсор
            figure.hover();

            // 4. Находим и проверяем текст профиля
            Locator profileLink = figure.locator("text=View profile");
            assertTrue(profileLink.isVisible(), "Текст 'View profile' не отображается для элемента " + i);

            // 5. Кликаем и проверяем URL
            page.waitForLoadState(LoadState.LOAD);
            profileLink.click();

            // 6. Проверка URL
            String expectedUrlPart = "/users/" + (i + 1);
            assertTrue(page.url().contains(expectedUrlPart),
                    "URL не содержит " + expectedUrlPart + ". Фактический URL: " + page.url());


            page.goBack();
            figures = page.locator(".figure");
        }
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}