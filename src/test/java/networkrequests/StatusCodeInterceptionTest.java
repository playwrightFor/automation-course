package networkrequests;


import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тестовый класс для проверки перехвата и модификации HTTP-запросов.
 * Демонстрирует использование функционала маршрутизации запросов в Playwright для тестирования сценариев с мокированием ответов.
 *
 * @author Oleg Todor
 * @since 2025-03-21
 */
public class StatusCodeInterceptionTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настройка тестового окружения перед каждым тестом:
     * 1. Инициализация Playwright и браузера
     * 2. Создание нового контекста браузера
     * 3. Регистрация обработчика для перехвата запросов к /status_codes/404
     *    - Подмена статус-кода 404 на 200
     *    - Возвращение кастомного HTML-контента
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });
    }

    /**
     * Тест проверки подмены статус-кода:
     * 1. Переход на страницу статус-кодов
     * 2. Клик по ссылке с кодом 404
     * 3. Верификация подмененного контента
     */
    @Test
    void testMockedStatusCode() {
        page.navigate("https://the-internet.herokuapp.com/status_codes");

        page.click("a[href='status_codes/404']");

        Locator responseText = page.locator("h3");
        assertEquals("Mocked Success Response", responseText.textContent(),
                "Не отображается ожидаемый моковый текст");
    }

    /**
     * Очистка ресурсов после каждого теста:
     * 1. Закрытие браузера
     * 2. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}