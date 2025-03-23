package faker;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DynamicFakerTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Faker faker;
    String mockName;

    @BeforeEach
    void setUp() {
        // 1. Инициализация Faker
        faker = new Faker();
        mockName = faker.name().fullName();

        // 2. Настройка Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();

        // 3. Мокирование API
        page.route("**/dynamic_content", route -> {
            // 4. Формирование мок-ответа
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

    @Test
    void testDynamicContentWithMock() {
        // 5. Навигация на страницу
        page.navigate("https://the-internet.herokuapp.com/dynamic_content");

        // 6. Поиск и проверка элемента
        Locator content = page.locator(".large-10.columns:has-text('" + mockName + "')");
        content.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // 7. Проверка отображения имени
        assertTrue(content.isVisible(), "Имя не отображается на странице");
        assertEquals(mockName, content.textContent().trim(), "Текст не совпадает с мок-данными");
    }

    @AfterEach
    void tearDown() {
        // 8. Закрытие ресурсов
        page.close();
        browser.close();
        playwright.close();
    }
}