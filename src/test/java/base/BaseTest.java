package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Базовый класс для настройки тестового окружения с использованием Playwright.
 * Обеспечивает общую конфигурацию браузера и страницы для всех наследующих тестовых классов.
 *
 * @author Oleg Todor
 * @since 2025-03-16
 */
public class BaseTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    public Page page;

    /**
     * Инициализация тестового окружения перед каждым тестом:
     * 1. Создание экземпляра Playwright
     * 2. Запуск браузера Chromium в режиме с графическим интерфейсом
     * 3. Создание нового контекста браузера
     * 4. Открытие новой страницы для тестирования
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Завершение работы тестового окружения после каждого теста:
     * Освобождение ресурсов Playwright и связанных компонентов
     */
    @AfterEach
    void tearDown() {
        playwright.close();
    }
}