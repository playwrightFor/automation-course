package networkrequests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Execution(ExecutionMode.CONCURRENT)
public class RequestHandlerTest {
    static Playwright playwright;
    static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false));
    }

    @Test
    void testRequestLogging() {
        try (BrowserContext context = browser.newContext(); Page page = context.newPage()) {
            Consumer<Request> requestListener = request -> System.out.println("Request: " + request.url());
            page.onRequest(requestListener); // Добавляем обработчик для каждого теста

            // Выполняем действия
            page.navigate("https://the-internet.herokuapp.com/");
            page.click("a[href='/add_remove_elements/']");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testWithoutCleanup() {
        try (BrowserContext context = browser.newContext(); Page page = context.newPage()) {
            Consumer<Request> requestListener = request -> System.out.println("Request: " + request.url());
            page.onRequest(requestListener); // Добавляем обработчик для каждого теста

            // Переход на страницу с постами
            page.navigate("https://the-internet.herokuapp.com/");

            // Ожидание полной загрузки страницы
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            // Получение заголовка страницы
            String title = page.title();
            System.out.println("Фактический заголовок страницы: " + title);

            // Проверка, что заголовок содержит 'Welcome'
            assertTrue(title.contains("The Internet"), "Заголовок страницы не содержит 'Welcome'!");

            // Проверка содержимого страницы
            String content = page.content();
            System.out.println("Содержимое страницы: " + content);

            // Проверка наличия элемента <h1> или другого заголовка
            String headerText = page.locator("h1").innerText();
            System.out.println("Текст заголовка: " + headerText);
            assertTrue(headerText.contains("Welcome"), "Текст заголовка не содержит 'Welcome'!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void cleanUp() {
        browser.close();
        playwright.close();
    }
}

