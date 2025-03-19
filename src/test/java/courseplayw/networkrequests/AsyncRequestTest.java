package courseplayw.networkrequests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class AsyncRequestTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        context = browser.newContext();
        page = context.newPage();
    }


    @Test
    void testAsyncRequest() {
        // Переходим на страницу с примером асинхронной загрузки
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/2");

        // Создаем хук для перехвата сетевых запросов
        page.onRequest(request -> {
            if (request.url().contains("dynamic_loading/2")) {
                System.out.println("Запрос к: " + request.url());
            }
        });

        // Нажимаем кнопку Start
        page.click("button:has-text('Start')");

        // Ожидаем появления текста после загрузки
        Locator helloText = page.locator("#finish >> text=Hello World!");
        helloText.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        // Проверяем результат
        assertTrue(helloText.isVisible(), "Текст не отображается");
        assertEquals("Hello World!", helloText.textContent());
    }

    @AfterEach
    void tearDown() {
        context.close();
        playwright.close();
    }
}