package networkrequests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки обработки асинхронных запросов и динамической загрузки контента.
 * Демонстрирует использование обработчиков сетевых запросов и ожидания элементов.
 *
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class AsyncRequestTest {
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
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Тест проверки асинхронной загрузки контента:
     * 1. Переход на тестовую страницу
     * 2. Регистрация обработчика сетевых запросов
     * 3. Запуск процесса загрузки
     * 4. Ожидание и верификация результатов
     */
    @Test
    void testAsyncRequest() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/2");

        page.onRequest(request -> {
            if (request.url().contains("dynamic_loading/2")) {
                System.out.println("Запрос к: " + request.url());
            }
        });

        page.click("button:has-text('Start')");

        Locator helloText = page.locator("#finish >> text=Hello World!");
        helloText.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        assertTrue(helloText.isVisible(), "Текст не отображается после загрузки");
        assertEquals("Hello World!", helloText.textContent(),
                "Содержимое элемента не соответствует ожидаемому");
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие контекста браузера
     * 2. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        context.close();
        playwright.close();
    }
}