package apiandui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Oleg Todor
 * @since 2025-03-25
 */
public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        page = browser.newPage();
        page.setDefaultTimeout(60000);

        // Навигация на страницу статус кодов один раз
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    @Test
    void testStatusCodesCombined() {
        // Получаем статус коды через API
        int apiStatus200 = getApiStatusCode(200);
        int apiStatus404 = getApiStatusCode(404);

        // Получаем статус коды через UI
        int uiStatus200 = getUiStatusCode(200);
        int uiStatus404 = getUiStatusCode(404);

        // Проверяем соответствие
        assertAll(
                // Проверка API
                () -> assertEquals(200, apiStatus200, "API: Неверный статус для 200"),
                () -> assertEquals(404, apiStatus404, "API: Неверный статус для 404"),

                // Проверка UI
                () -> assertEquals(200, uiStatus200, "UI: Неверный статус для 200"),
                () -> assertEquals(404, uiStatus404, "UI: Неверный статус для 404"),

                // Сравнение API и UI
                () -> assertEquals(apiStatus200, uiStatus200, "Расхождение статусов для 200"),
                () -> assertEquals(apiStatus404, uiStatus404, "Расхождение статусов для 404")
        );
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        try {
            Locator link = page.locator("text=" + code).first();

            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(15000))
            );

            page.goBack(); // Возврат на исходную страницу после клика
            return response.status();

        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/error-" + code + ".png")));
            throw new RuntimeException("Ошибка при проверке UI для кода " + code, e);
        }
    }

    @AfterEach
    void teardown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (apiRequest != null) apiRequest.dispose();
        if (playwright != null) playwright.close();
    }
}