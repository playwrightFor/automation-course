package dynamic;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;


/**
 * Тестовый класс для проверки динамической загрузки контента и взаимодействия с элементами страницы.
 * Демонстрирует работу с ожиданием элементов, обработку вкладок и создание скриншотов при ошибках.
 *
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class DynamicContentTest {

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
     * Тест проверки динамической загрузки контента:
     * - Переход на тестовую страницу
     * - Взаимодействие с элементами интерфейса
     * - Ожидание появления динамического контента
     * - Проверка работы с несколькими вкладками
     * - Создание скриншота при возникновении ошибки
     */
    @Test
    void testDynamicLoading() {
        try {
            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

            Locator startButton = page.locator("button:has-text('Start')");
            startButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            startButton.click();

            Locator helloWorldText = page.locator("#finish >> text=Hello World!");
            helloWorldText.waitFor(new Locator.WaitForOptions().setTimeout(45000));

            Locator seleniumLink = page.locator("text=Elemental Selenium");
            seleniumLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            Assertions.assertTrue(seleniumLink.isVisible(), "Ссылка на Elemental Selenium не отображается");

            Page newPage = context.waitForPage(() -> seleniumLink.click());
            newPage.waitForLoadState(LoadState.LOAD);

            Assertions.assertTrue(
                    newPage.url().startsWith("https://elementalselenium.com/"),
                    "Некорректный URL после перехода: " + newPage.url()
            );
            Assertions.assertTrue(
                    newPage.isVisible("h1 >> text='Elemental Selenium'"),
                    "Заголовок страницы не найден"
            );

            newPage.close();

        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/dynamic-loading-error.png")));
            throw e;
        }
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        playwright.close();
    }
}