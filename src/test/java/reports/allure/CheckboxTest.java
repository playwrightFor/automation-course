package reports.allure;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки функционала работы с чекбоксами.
 * Интегрирует Allure-отчетность и автоматическое создание скриншотов при ошибках.
 *
 * @Epic Веб-интерфейс тестов
 * @Feature Операции с чекбоксами
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

    /**
     * Вложенный класс для обработки провалившихся тестов.
     * Автоматически создает скриншот страницы при обнаружении ошибки.
     */
    static class ScreenshotWatcher implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            CheckboxTest testInstance = (CheckboxTest) context.getRequiredTestInstance();
            testInstance.captureScreenshotOnFailure();
        }
    }

    /**
     * Инициализация тестового окружения перед каждым тестом:
     * 1. Создание экземпляра Playwright
     * 2. Запуск браузера Chromium в headless-режиме
     * 3. Создание нового контекста браузера
     * 4. Открытие новой страницы
     */
    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Основной тест проверки работы с чекбоксами:
     * 1. Переход на тестовую страницу
     * 2. Верификация начального состояния
     * 3. Изменение состояния чекбоксов
     * 4. Проверка измененного состояния
     *
     * @Story Проверка работы чекбоксов
     * @DisplayName Тестирование выбора/снятия чекбоксов
     * @Severity Уровень важности: CRITICAL
     */
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

    /**
     * Переход на страницу с чекбоксами
     */
    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
    }

    /**
     * Проверка исходного состояния чекбоксов:
     * - Первый чекбокс не выбран
     * - Второй чекбокс выбран по умолчанию
     */
    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        Locator checkboxes = page.locator("input[type='checkbox']");
        assertFalse(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть не выбран");
        assertTrue(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть выбран");
    }

    /**
     * Изменение состояния чекбоксов:
     * - Выбор первого чекбокса
     * - Снятие выбора со второго чекбокса
     */
    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        page.locator("input[type='checkbox']").nth(0).check();
        page.locator("input[type='checkbox']").nth(1).uncheck();
    }

    /**
     * Проверка измененного состояния:
     * - Первый чекбокс должен быть выбран
     * - Второй чекбокс должен быть не выбран
     */
    @Step("Проверка измененного состояния")
    private void verifyToggledState() {
        Locator checkboxes = page.locator("input[type='checkbox']");
        assertTrue(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть выбран");
        assertFalse(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть не выбран");
    }

    /**
     * Создание скриншота при возникновении ошибки.
     * Сохраняет снимок экрана в папку screenshots с уникальным именем файла.
     */
    @Step("Создание скриншота при ошибке")
    void captureScreenshotOnFailure() {
        try {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/failure_" + System.currentTimeMillis() + ".png")));
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие контекста браузера
     * 2. Остановка браузера
     * 3. Освобождение ресурсов Playwright
     */
    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}