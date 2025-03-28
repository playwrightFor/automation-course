package faker;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Тестовый класс для проверки отображения динамического контента с использованием мок-данных.
 * Интегрирует библиотеку Faker для генерации тестовых данных и Playwright для мокирования API.
 *
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DynamicFakerTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Faker faker;
    String mockName;

    /**
     * Подготовка тестового окружения:
     * 1. Инициализация генератора тестовых данных Faker
     * 2. Настройка браузера Chromium
     * 3. Конфигурация мокированного ответа для эндпоинта /dynamic_content
     */
    @BeforeEach
    void setUp() {
        faker = new Faker();
        mockName = faker.name().fullName();

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();

        page.route("**/dynamic_content", route -> {
            String mockResponse = "<html><body>" +
                    "<div class='row'>" +
                    "  <div class='large-2 columns'>" +
                    "    <img src='/img/avatars/Original-Facebook-Geek-Profile-Avatar-1.jpg'>" +
                    "  </div>" +
                    "  <div class='large-10 columns'>" + mockName + "</div>" +
                    "</div>" +
                    "</body></html>";

            route.fulfill(new Route.FulfillOptions()
                    .setContentType("text/html")
                    .setBody(mockResponse));
        });
    }

    /**
     * Тест проверки отображения сгенерированных данных:
     * 1. Переход на тестовую страницу
     * 2. Ожидание появления элемента с мок-данными
     * 3. Проверка видимости и соответствия текста
     */
    @Test
    void testDynamicContentWithMock() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_content");

        Locator content = page.locator(".large-10.columns:has-text('" + mockName + "')");
        content.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        assertTrue(content.isVisible(), "Сгенерированное имя не отображается");
        assertEquals(mockName, content.textContent().trim(),
                "Отображаемый текст не совпадает с сгенерированными данными");
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие страницы
     * 2. Остановка браузера
     * 3. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}