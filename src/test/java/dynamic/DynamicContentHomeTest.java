package dynamic;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Тестовый класс для проверки различных сценариев работы с веб-интерфейсом:
 * - Авторизация в системе
 * - Взаимодействие с фреймами
 * - Управление вкладками браузера
 * Использует Playwright для автоматизации браузера Chromium.
 *
 * @author Oleg Todor
 * @since 2025-03-19
 */
public class DynamicContentHomeTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настройка тестового окружения перед каждым тестом:
     * 1. Инициализация Playwright
     * 2. Запуск браузера Chromium в режиме с графическим интерфейсом
     * 3. Создание нового контекста и страницы
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Тест проверки процесса авторизации:
     * 1. Переход на страницу входа
     * 2. Заполнение полей учетных данных
     * 3. Отправка формы
     * 4. Проверка сообщения об успешной авторизации
     */
    @Test
    void testFormSubmission() {
        page.navigate("https://the-internet.herokuapp.com/login");

        page.waitForNavigation(() -> {
            page.fill("#username", "tomsmith");
            page.fill("#password", "SuperSecretPassword!");
            page.click("button[type='submit']");
        });

        page.waitForSelector("text=You logged into a secure area!");
    }

    /**
     * Тест работы с вложенными фреймами и вкладками:
     * 1. Переход на страницу с фреймами
     * 2. Проверка содержимого фреймов
     * 3. Создание и закрытие новой вкладки
     */
    @Test
    void testNestedFrames() {
        page.navigate("https://the-internet.herokuapp.com/nested_frames");

        Frame leftFrame = page.frame("frame-left");
        String leftText = leftFrame.locator("body").textContent();
        Assertions.assertTrue(leftText.contains("LEFT"), "Текст во фрейме LEFT не найден");

        Frame middleFrame = page.frame("frame-middle");
        String middleText = middleFrame.locator("body").textContent();
        Assertions.assertTrue(middleText.contains("MIDDLE"), "Текст во фрейме MIDDLE не найден");

        Page newPage = page.context().newPage();
        newPage.navigate("https://the-internet.herokuapp.com");
        newPage.close();
    }

    /**
     * Завершение работы тестового окружения:
     * Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        playwright.close();
    }
}