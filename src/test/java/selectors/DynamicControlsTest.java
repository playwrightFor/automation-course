package selectors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки динамических элементов управления на веб-странице.
 * Проверяет функционал удаления/восстановления чекбокса и изменения его состояния.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    /**
     * Настраивает окружение перед каждым тестом:
     * 1. Создает экземпляр Playwright
     * 2. Запускает браузер Chromium в режиме с графическим интерфейсом
     * 3. Создает новую страницу для тестирования
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    /**
     * Проверяет сценарий работы с динамическим чекбоксом:
     * 1. Переход на тестовую страницу
     * 2. Проверка начального состояния чекбокса
     * 3. Удаление чекбокса с проверкой исчезновения
     * 4. Проверка сообщения об успешном удалении
     * 5. Восстановление чекбокса с проверкой появления
     * 6. Проверка состояния восстановленного чекбокса
     */
    @Test
    void testDynamicCheckbox() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        Locator checkbox = page.locator("input[type='checkbox']");
        Locator removeButton = page.locator("button:has-text('Remove')");
        Locator addButton = page.locator("button:has-text('Add')");

        assertTrue(checkbox.isVisible(), "Чекбокс должен быть видимым изначально");

        removeButton.click();
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertFalse(checkbox.isVisible(), "Чекбокс должен исчезнуть");

        Locator goneMessage = page.locator("text=It's gone!");
        goneMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertTrue(goneMessage.isVisible(), "Сообщение 'It's gone!' не отображается");

        addButton.click();
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertTrue(checkbox.isVisible(), "Чекбокс должен снова появиться");
        assertFalse(checkbox.isChecked(), "Чекбокс должен быть неактивным после восстановления");
    }

    /**
     * Выполняет очистку ресурсов после каждого теста:
     * 1. Закрывает страницу
     * 2. Закрывает браузер
     * 3. Освобождает ресурсы Playwright
     */
    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}