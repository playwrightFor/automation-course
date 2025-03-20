package parallelnavigat;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

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

                    // Навигация с таймаутом 30 секунд
                    page.navigate(BASE_URL + path, new Page.NavigateOptions()
                            .setTimeout(30_000)
                            .setWaitUntil(WaitUntilState.LOAD));

                    // Проверка заголовка
                    assertThat(page.title())
                            .as("Проверка пути: %s", path)
                            .isEqualTo(getExpectedTitle(path));
                }
            }
        }
    }

    private String getExpectedTitle(String path) {
        return switch (path) {
            case "/" -> "The Internet";
            case "/login" -> "The Internet";
            case "/dropdown" -> "The Internet";
            case "/javascript_alerts" -> "The Internet";
            case "/checkboxes" -> "The Internet";
            case "/hover" -> "";
            case "/status_codes" -> "The Internet";
            default -> throw new IllegalArgumentException("Unknown path: " + path);
        };
    }
}