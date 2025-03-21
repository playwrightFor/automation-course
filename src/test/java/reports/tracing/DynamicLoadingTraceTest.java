package reports.tracing;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();

        // Старт трассировки ДО начала теста
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
    }

    @Test
    void testDynamicLoadingWithTrace() {
        try {
            // 1. Переход на страницу
            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

            // 2. Клик по кнопке "Start"
            page.click("button:text('Start')");

            // 3. Ожидание элемента с таймаутом
            Locator finishText = page.locator("#finish >> text=Hello World!");
            finishText.waitFor(new Locator.WaitForOptions().setTimeout(10000));

            // 4. Проверка через JUnit Assertions
            assertEquals("Hello World!", finishText.textContent());

        } catch (Exception e) {
            // Сохраняем трассировку даже при ошибке
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-failure.zip")));
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        // Сохранение трассировки при успешном выполнении
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));

        context.close();
        browser.close();
        playwright.close();
    }
}