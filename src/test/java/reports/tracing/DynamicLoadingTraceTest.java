package reports.tracing;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки динамической загрузки элементов с использованием трассировки.
 * Демонстрирует запись и сохранение трассировочных данных как при успешном выполнении, так и при ошибках.
 *
 * @author Oleg Todor
 * @since 2025-03-21
 */
public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настраивает тестовое окружение перед каждым тестом:
     * 1. Инициализирует движок Playwright
     * 2. Запускает браузер Chromium
     * 3. Создает новый контекст с включенной трассировкой
     * 4. Начинает запись трассировки с параметрами:
     *    - Сохранение скриншотов
     *    - Запись снимков состояний DOM
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
    }

    /**
     * Проверяет сценарий динамической загрузки контента:
     * 1. Переход на тестовую страницу
     * 2. Активация загрузки контента
     * 3. Ожидание появления результата
     * 4. Верификация текста элемента
     *
     * При возникновении ошибки сохраняет трассировочные данные в trace-failure.zip
     */
    @Test
    void testDynamicLoadingWithTrace() {
        try {
            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
            page.click("button:text('Start')");

            Locator finishText = page.locator("#finish >> text=Hello World!");
            finishText.waitFor(new Locator.WaitForOptions().setTimeout(10000));

            assertEquals("Hello World!", finishText.textContent());

        } catch (Exception e) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-failure.zip")));
            throw e;
        }
    }

    /**
     * Завершает тестовое окружение после каждого теста:
     * 1. Сохраняет трассировку в trace/trace-success.zip
     * 2. Закрывает контекст браузера
     * 3. Останавливает браузер
     * 4. Освобождает ресурсы Playwright
     */
    @AfterEach
    void tearDown() {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));

        context.close();
        browser.close();
        playwright.close();
    }
}