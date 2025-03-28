package apiandui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Тестовый класс для комплексной проверки статус-кодов через API и веб-интерфейс.
 * Сравнивает результаты, полученные разными способами, обеспечивая консистентность данных.
 *
 * @author Oleg Todor
 * @since 2025-03-25
 */
public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;

    /**
     * Инициализация тестового окружения:
     * 1. Создание контекста для API-запросов с базовым URL
     * 2. Запуск браузера Chromium с задержкой действий (slowMo) и графическим интерфейсом
     * 3. Настройка страницы с увеличенным таймаутом по умолчанию
     * 4. Навигация на стартовую страницу статус-кодов
     */
    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        page = browser.newPage();
        page.setDefaultTimeout(60000);
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    /**
     * Комплексный тест проверки статус-кодов:
     * 1. Получение кодов 200 и 404 через API
     * 2. Получение кодов 200 и 404 через UI
     * 3. Валидация соответствия ожидаемым значениям
     * 4. Сравнение результатов API и UI между собой
     */
    @Test
    void testStatusCodesCombined() {
        int apiStatus200 = getApiStatusCode(200);
        int apiStatus404 = getApiStatusCode(404);

        int uiStatus200 = getUiStatusCode(200);
        int uiStatus404 = getUiStatusCode(404);

        assertAll(
                () -> assertEquals(200, apiStatus200, "API: Неверный статус для 200"),
                () -> assertEquals(404, apiStatus404, "API: Неверный статус для 404"),
                () -> assertEquals(200, uiStatus200, "UI: Неверный статус для 200"),
                () -> assertEquals(404, uiStatus404, "UI: Неверный статус для 404"),
                () -> assertEquals(apiStatus200, uiStatus200, "Расхождение статусов для 200"),
                () -> assertEquals(apiStatus404, uiStatus404, "Расхождение статусов для 404")
        );
    }

    /**
     * Получает статус-код через API-запрос
     * @param code ожидаемый HTTP-статус код
     * @return фактический статус-код ответа
     */
    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        return response.status();
    }

    /**
     * Получает статус-код через взаимодействие с веб-интерфейсом
     * @param code проверяемый HTTP-статус код
     * @return фактический статус-код ответа
     * @throws RuntimeException при ошибках взаимодействия с UI со скриншотом
     */
    private int getUiStatusCode(int code) {
        try {
            Locator link = page.locator("text=" + code).first();
            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(15000))
            );
            page.goBack();
            return response.status();
        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/error-" + code + ".png")));
            throw new RuntimeException("Ошибка при проверке UI для кода " + code, e);
        }
    }

    /**
     * Освобождение ресурсов после теста:
     * 1. Закрытие страницы
     * 2. Остановка браузера
     * 3. Удаление API-контекста
     * 4. Завершение работы Playwright
     */
    @AfterEach
    void teardown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (apiRequest != null) apiRequest.dispose();
        if (playwright != null) playwright.close();
    }
}