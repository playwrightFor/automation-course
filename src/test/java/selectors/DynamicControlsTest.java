package selectors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @Test
    void testDynamicCheckbox() {

        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");


        Locator checkbox = page.locator("input[type='checkbox']");
        Locator removeButton = page.locator("button:has-text('Remove')");
        Locator addButton = page.locator("button:has-text('Add')");

        // 3. Проверка начального состояния
        assertTrue(checkbox.isVisible(), "Чекбокс должен быть видимым изначально");

        // 4. Удаление чекбокса
        removeButton.click();

        // 5. Ожидание исчезновения
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertFalse(checkbox.isVisible(), "Чекбокс должен исчезнуть");

        // 6. Проверка сообщения
        Locator goneMessage = page.locator("text=It's gone!");
        goneMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertTrue(goneMessage.isVisible(), "Сообщение 'It's gone!' не отображается");

        // 7. Восстановление чекбокса
        addButton.click();

        // 8. Ожидание появления
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // 9. Проверка восстановления
        assertTrue(checkbox.isVisible(), "Чекбокс должен снова появиться");

        // 10. Дополнительная проверка состояния
        assertFalse(checkbox.isChecked(), "Чекбокс должен быть неактивным после восстановления");
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}