package mobile;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)); // Установите headless на false для визуализации
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        Locator input = page.locator("input[type='text']");
        Locator enableButton = page.locator("button:has-text('Enable')");

        assertFalse(input.isEnabled(), "Начальное состояние: поле заблокировано");

        enableButton.click();

        // Ждем исчезновения индикатора загрузки
        page.waitForSelector("#loading",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(10000));

        // Явное ожидание активации поля
        page.waitForCondition(() -> input.isEnabled(),
                new Page.WaitForConditionOptions().setTimeout(10000));

        assertTrue(input.isEnabled(), "Поле должно стать активным после клика");
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
