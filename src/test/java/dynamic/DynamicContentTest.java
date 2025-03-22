package dynamic;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class DynamicContentTest {

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
    void testDynamicLoading() {
        try {
            // 1. Переход на страницу
            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

            // 2. Ожидание и клик по кнопке Start
            Locator startButton = page.locator("button:has-text('Start')");
            startButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            startButton.click();

            // 3. Ожидание появления текста с увеличенным таймаутом
            Locator helloWorldText = page.locator("#finish >> text=Hello World!");
            helloWorldText.waitFor(new Locator.WaitForOptions().setTimeout(45000));

            // 4. Проверка видимости ссылки
            Locator seleniumLink = page.locator("text=Elemental Selenium");
            seleniumLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            Assertions.assertTrue(seleniumLink.isVisible(), "Ссылка не отображается");

            // 5. Открытие новой вкладки
            Page newPage = context.waitForPage(() -> {
                seleniumLink.click();
            });

            // 6. Ожидание полной загрузки новой страницы
            newPage.waitForLoadState(LoadState.LOAD);

            // 7. Проверка URL и контента
            Assertions.assertTrue(
                    newPage.url().startsWith("https://elementalselenium.com/"), "Фактический URL: " + newPage.url()
            );
            Assertions.assertTrue(newPage.isVisible("h1 >> text='Elemental Selenium'"), "Заголовок не найден"
            );

            // 8. Закрытие вкладки
            newPage.close();

        } catch (Exception e) {
            // 9. Скриншот при ошибке
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/dynamic-loading-error.png")));
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        playwright.close();
    }
}