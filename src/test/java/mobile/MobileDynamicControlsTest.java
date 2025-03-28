package mobile;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки динамических элементов управления в мобильной версии.
 * Эмулирует поведение на устройстве iPad Pro 11 с использованием Playwright.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    /**
     * Настройка мобильного окружения перед каждым тестом:
     * 1. Инициализация Playwright
     * 2. Конфигурация параметров эмуляции iPad Pro 11:
     *    - User-Agent мобильного устройства
     *    - Разрешение экрана 834x1194
     *    - Масштабирование 2x
     *    - Мобильный режим и сенсорный ввод
     * 3. Запуск браузера Chromium с графическим интерфейсом
     * 4. Создание контекста и страницы
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    /**
     * Тест активации текстового поля:
     * 1. Переход на страницу динамических элементов
     * 2. Проверка начального состояния поля ввода
     * 3. Клик по кнопке активации
     * 4. Ожидание завершения загрузки
     * 5. Проверка активации поля
     */
    @Test
    void testInputEnabling() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        Locator input = page.locator("input[type='text']");
        Locator enableButton = page.locator("button:has-text('Enable')");

        assertFalse(input.isEnabled(), "Поле должно быть заблокировано изначально");

        enableButton.click();

        page.waitForSelector("#loading",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(10000));

        page.waitForCondition(() -> input.isEnabled(),
                new Page.WaitForConditionOptions().setTimeout(10000));

        assertTrue(input.isEnabled(), "Поле не активировалось после нажатия кнопки");
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
