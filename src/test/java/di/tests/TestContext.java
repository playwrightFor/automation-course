package di.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;


/**
 * Класс для управления зависимостями и ресурсами тестовой среды.
 * Инкапсулирует инициализацию и освобождение ресурсов Playwright.
 */
class TestContext {
    private final Playwright playwright;
    private final Browser browser;
    private final Page page;

    /**
     * Инициализирует тестовое окружение:
     * 1. Создает экземпляр Playwright
     * 2. Запускает браузер Chromium в режиме с графическим интерфейсом
     * 3. Создает новую страницу для тестирования
     */
    public TestContext() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    /**
     * Возвращает экземпляр страницы для взаимодействия с веб-контентом
     * @return Объект Page для работы с браузерной страницей
     */
    public Page getPage() {
        return page;
    }

    /**
     * Освобождает все ресурсы тестового окружения в обратном порядке создания:
     * 1. Закрывает страницу
     * 2. Останавливает браузер
     * 3. Завершает работу Playwright
     */
    public void close() {
        page.close();
        browser.close();
        playwright.close();
    }
}