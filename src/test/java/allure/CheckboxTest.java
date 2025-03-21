package allure;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
@ExtendWith(CheckboxTest.ScreenshotWatcher.class)
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    static class ScreenshotWatcher implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            CheckboxTest testInstance = (CheckboxTest) context.getRequiredTestInstance();
            testInstance.captureScreenshotOnFailure();
        }
    }

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора/снятия чекбоксов")
    @Severity(SeverityLevel.CRITICAL)
    void testCheckboxes() {
        navigateToCheckboxesPage();
        verifyInitialState();
        toggleCheckboxes();
        verifyToggledState();
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
    }

    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        Locator checkboxes = page.locator("input[type='checkbox']");
        assertFalse(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть не выбран");
        assertTrue(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть выбран");
    }

    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        page.locator("input[type='checkbox']").nth(0).check();
        page.locator("input[type='checkbox']").nth(1).uncheck();
    }

    @Step("Проверка измененного состояния")
    private void verifyToggledState() {
        Locator checkboxes = page.locator("input[type='checkbox']");
        assertTrue(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть выбран");
        assertFalse(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть не выбран");
    }

    @Step("Создание скриншота при ошибке")
    void captureScreenshotOnFailure() {
        try {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/failure_" + System.currentTimeMillis() + ".png")));
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}