package mobile;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функционала перетаскивания элементов в мобильном режиме.
 * Эмулирует поведение устройства Samsung Galaxy S22 Ultra с использованием Playwright.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class MobileDragAndDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настройка мобильного окружения перед каждым тестом:
     * 1. Инициализация Playwright
     * 2. Конфигурация параметров эмуляции:
     *    - User-Agent Samsung Galaxy S22 Ultra
     *    - Разрешение экрана 384x873
     *    - Масштабирование 3.5x
     *    - Мобильный режим и сенсорный ввод
     * 3. Запуск браузера Chromium с графическим интерфейсом
     * 4. Создание контекста и страницы
     */
    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
                .setViewportSize(384, 873)
                .setDeviceScaleFactor(3.5)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    /**
     * Тест проверки перетаскивания элементов:
     * 1. Переход на тестовую страницу
     * 2. Проверка начального состояния колонок
     * 3. Эмуляция перетаскивания через JavaScript
     * 4. Ожидание обновления интерфейса
     * 5. Проверка изменения состояния колонок
     */
    @Test
    void testDragAndDropMobile() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator columnA = page.locator("#column-a");
        Locator columnB = page.locator("#column-b");

        assertEquals("A", columnA.textContent().trim(), "Начальный текст колонки A неверный");
        assertEquals("B", columnB.textContent().trim(), "Начальный текст колонки B неверный");

        page.evaluate("() => {\n" +
                "  const dataTransfer = new DataTransfer();\n" +
                "  const event = new DragEvent('drop', { dataTransfer });\n" +
                "  document.querySelector('#column-a').dispatchEvent(new DragEvent('dragstart', { dataTransfer }));\n" +
                "  document.querySelector('#column-b').dispatchEvent(event);\n" +
                "}");

        page.waitForTimeout(1000);
        assertEquals("A", columnB.textContent().trim(), "Текст в колонке B не соответствует ожидаемому после перетаскивания");
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие контекста браузера
     * 2. Остановка браузера
     * 3. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}