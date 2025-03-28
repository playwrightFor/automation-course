package parallelnavigat;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Тестовый класс для параллельной проверки навигации на различных страницах.
 * Выполняет кросс-браузерное тестирование с использованием параметризованных тестов.
 *
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    /**
     * Параметризованный тест для проверки навигации:
     * 1. Запускает указанный браузер (Chromium/Firefox)
     * 2. Переходит на указанную страницу
     * 3. Проверяет соответствие заголовка ожидаемому значению
     *
     * @param browserType тип браузера (chromium/firefox)
     * @param path путь тестируемой страницы
     */
    @ParameterizedTest
    @CsvSource({
            "chromium, /",
            "chromium, /login",
            "chromium, /dropdown",
            "chromium, /javascript_alerts",
            "firefox, /checkboxes",
            "firefox, /hover",
            "firefox, /status_codes"
    })
    void testAllPages(String browserType, String path) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType type = switch (browserType.toLowerCase()) {
                case "chromium" -> playwright.chromium();
                case "firefox" -> playwright.firefox();
                default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
            };

            try (Browser browser = type.launch(new BrowserType.LaunchOptions().setHeadless(true))) {
                try (BrowserContext context = browser.newContext()) {
                    Page page = context.newPage();

                    page.navigate(BASE_URL + path, new Page.NavigateOptions()
                            .setTimeout(30_000)
                            .setWaitUntil(WaitUntilState.LOAD));

                    assertThat(page.title())
                            .as("Проверка пути: %s", path)
                            .isEqualTo(getExpectedTitle(path));
                }
            }
        }
    }

    /**
     * Возвращает ожидаемый заголовок страницы в зависимости от пути.
     *
     * @param path путь к странице
     * @return ожидаемое значение заголовка
     * @throws IllegalArgumentException при передаче неизвестного пути
     */
    private String getExpectedTitle(String path) {
        return switch (path) {
            case "/", "/login", "/dropdown", "/javascript_alerts", "/checkboxes", "/status_codes" -> "The Internet";
            case "/hover" -> "";
            default -> throw new IllegalArgumentException("Unknown path: " + path);
        };
    }
}