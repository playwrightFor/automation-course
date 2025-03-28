package networkrequests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тестовый класс для проверки перехвата сетевых запросов и модификации ответов.
 * Демонстрирует использование функционала маршрутизации Playwright для подмены контента.
 *
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class NetworkInterceptionTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настройка тестового окружения перед каждым тестом:
     * 1. Инициализация Playwright и браузера Chromium
     * 2. Создание нового контекста и страницы
     * 3. Регистрация обработчика для перехвата запросов
     *    - Подмена ответа для URL /dynamic_loading/2
     *    - Возвращение кастомного HTML-контента
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        context.route("**/dynamic_loading/2", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("""
                            <div id="start">
                                <button>Start</button>
                            </div>
                            <div id="finish" style="display: block;">
                                <h4>Mocked Title</h4>
                            </div>
                            """)
            );
        });
    }

    /**
     * Тест проверки подмены контента:
     * 1. Переход на тестовую страницу
     * 2. Клик по кнопке "Start"
     * 3. Верификация отображения мокового заголовка
     */
    @Test
    void testMockedContent() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/2");
        page.click("button:has-text('Start')");

        Locator title = page.locator("#finish h4");
        assertEquals("Mocked Title", title.textContent());
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие браузера
     * 2. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}