package networkrequests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;


/**
 * @author Oleg Todor
 * @since 2025-03-18
 */
public class RequestHandlerTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    Consumer<Request> requestListener; // Храним ссылку на обработчик

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }



    @Test
    void testRequestLogging() {
        // Создаем обработчик для логирования запросов
        requestListener = request ->
                System.out.println("Request: " + request.url());

        // Добавляем обработчик
        page.onRequest(requestListener);

        // Выполняем действия
        page.navigate("https://the-internet.herokuapp.com/");
        page.click("a[href='/add_remove_elements/']");
    }

    @Test
    void testWithoutCleanup() {
        page.onRequest(request -> System.out.println("Запрос: " + request.url()));

        // Переход на страницу с постами
        page.navigate("https://the-internet.herokuapp.com/");

        // Ожидание полной загрузки страницы
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Получение заголовка страницы
        String title = page.title();
        System.out.println("Фактический заголовок страницы: " + title);

        // Проверка, что заголовок содержит 'Welcome'
        Assertions.assertTrue(title.contains("The Internet"), "Заголовок страницы не содержит 'Welcome'!");

        // Проверка содержимого страницы
        String content = page.content();
        System.out.println("Содержимое страницы: " + content);

        // Проверка наличия элемента <h1> или другого заголовка
        String headerText = page.locator("h1").innerText();
        System.out.println("Текст заголовка: " + headerText);
        Assertions.assertTrue(headerText.contains("Welcome"), "Текст заголовка не содержит 'Welcome'!");
    }

    @AfterEach
    void tearDown() {
        // Удаляем обработчик после теста
        if (requestListener != null) {
            page.offRequest(requestListener);
        }
        page.close();
    }
}
