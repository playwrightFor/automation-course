package apiandui;


import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для комплексной проверки HTTP-статус кодов через API и веб-интерфейс.
 * Использует параметризованные тесты для проверки различных кодов ответа.
 * Конфигурация тестового окружения загружается из внешних свойств.
 *
 * @author Oleg Todor
 * @since 2025-03-25
 */
public class StatusCodeCombinedTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;
    private static EnvConfig config;

    /**
     * Загрузка конфигурации перед всеми тестами из системных свойств
     */
    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class, System.getProperties());
    }

    /**
     * Инициализация тестового окружения перед каждым тестом:
     * 1. Создание контекста для API-запросов с базовым URL из конфигурации
     * 2. Запуск браузера Chromium с графическим интерфейсом и задержкой действий
     * 3. Настройка страницы с увеличенным таймаутом
     */
    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.baseUrl())
        );

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(100)
        );

        page = browser.newPage();
        page.setDefaultTimeout(40000);
    }

    /**
     * Параметризованный тест для проверки статус-кодов:
     * 1. Получение кода через API
     * 2. Получение кода через UI
     * 3. Сравнение результатов между API и UI
     *
     * @param statusCode проверяемый HTTP-статус код (200, 404)
     */
    @ParameterizedTest
    @ValueSource(ints = {200, 404})
    void testStatusCodeCombined(int statusCode) {
        int apiStatus = getApiStatusCode(statusCode);
        int uiStatus = getUiStatusCode(statusCode);

        assertEquals(apiStatus, uiStatus,
                String.format("Расхождение статусов: API=%d UI=%d для кода %d",
                        apiStatus, uiStatus, statusCode));
    }

    /**
     * Получает статус-код через API-запрос
     *
     * @param code ожидаемый HTTP-статус код
     * @return фактический статус-код ответа
     * @throws AssertionError если полученный код не соответствует ожидаемому
     */
    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        assertEquals(code, response.status(), "API: Несоответствие статус-кода");
        return response.status();
    }

    /**
     * Получает статус-код через взаимодействие с UI:
     * 1. Навигация на страницу статус-кодов
     * 2. Клик по соответствующей ссылке
     * 3. Перехват сетевого ответа
     * 4. Возврат на исходную страницу
     *
     * @param code проверяемый HTTP-статус код
     * @return фактический статус-код ответа
     * @throws RuntimeException с сохранением скриншота при ошибках взаимодействия
     */
    private int getUiStatusCode(int code) {
        try {
            page.navigate(config.baseUrl() + "/status_codes");
            page.waitForSelector("div.example");

            Locator link = page.locator(
                            String.format("a[href*='status_codes/%d']", code))
                    .first();

            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(10000))
            );

            return response.status();
        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/error-%d-%d.png".formatted(code, System.currentTimeMillis()))));
            throw new RuntimeException("Ошибка UI проверки для кода " + code, e);
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