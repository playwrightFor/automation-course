package api;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки динамической загрузки контента с использованием трассировки.
 * Демонстрирует интеграцию проверки сетевых запросов и создание диагностических артефактов.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class DynamicLoadingApiTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    /**
     * Тест проверки динамического контента:
     * 1. Инициализация браузера с включенной трассировкой
     * 2. Переход на тестовую страницу
     * 3. Мониторинг сетевых ответов с валидацией статусов
     * 4. Взаимодействие с элементами интерфейса
     * 5. Сохранение трассировочных данных при успешном выполнении
     */
    @Test
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true));

        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        page.onResponse(response -> {
            if (response.url().contains("/dynamic_loading")) {
                assertEquals(200, response.status(),
                        "Неверный статус ответа для URL: " + response.url());
            }
        });

        page.click("button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));

        assertEquals("Hello World!", finishText.textContent().trim(),
                "Текст элемента не соответствует ожидаемому");

        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));
    }

    /**
     * Освобождение ресурсов после теста:
     * 1. Закрытие страницы
     * 2. Остановка браузера
     * 3. Завершение работы Playwright
     */
    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}