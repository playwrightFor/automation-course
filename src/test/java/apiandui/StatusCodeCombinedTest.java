package apiandui;


import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oleg Todor
 * @since 2025-03-25
 */
public class StatusCodeCombinedTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;
    private static EnvConfig config;

    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class, System.getProperties());
    }

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // API контекст с базовым URL из конфига
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.baseUrl())
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(100)
        );

        page = browser.newPage();
        page.setDefaultTimeout(40000);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 404})
    void testStatusCodeCombined(int statusCode) {
        // API проверка
        int apiStatus = getApiStatusCode(statusCode);

        // UI проверка
        int uiStatus = getUiStatusCode(statusCode);

        // Сравнение результатов
        assertEquals(apiStatus, uiStatus,
                String.format("API (%d) и UI (%d) статусы не совпадают для кода %d",
                        apiStatus, uiStatus, statusCode));
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        assertEquals(code, response.status(),
                "API: Неверный статус код для " + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        try {
            // Навигация на страницу статус кодов
            page.navigate(config.baseUrl() + "/status_codes");
            page.waitForSelector("div.example");

            // Улучшенный локатор
            Locator link = page.locator(
                    String.format("a[href*='status_codes/%d']", code)
            ).first();

            // Перехват ответа перед кликом
            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(10000))
            );

            return response.status();

        } catch (Exception e) {
            // Скриншот с именем, включающим код ошибки
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/error-" + code + "-" + System.currentTimeMillis() + ".png")));
            throw new RuntimeException("UI проверка упала для кода " + code, e);
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