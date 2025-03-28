package networkrequests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки обработки сетевых запросов и анализа содержимого страниц.
 * Демонстрирует различные подходы к работе с обработчиками событий и проверке контента.
 *
 * @Execution(ExecutionMode.CONCURRENT) - позволяет параллельное выполнение тестов
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Execution(ExecutionMode.CONCURRENT)
public class RequestHandlerTest {
    static Playwright playwright;
    static Browser browser;

    /**
     * Инициализация тестового окружения перед всеми тестами:
     * 1. Создание экземпляра Playwright
     * 2. Запуск браузера Chromium в headful-режиме
     */
    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false));
    }

    /**
     * Тест логирования сетевых запросов:
     * 1. Создание контекста и страницы
     * 2. Регистрация обработчика запросов
     * 3. Выполнение навигации по страницам
     * 4. Автоматическое закрытие ресурсов
     */
    @Test
    void testRequestLogging() {
        try (BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            Consumer<Request> requestListener = request ->
                    System.out.println("Зарегистрирован запрос: " + request.url());

            page.onRequest(requestListener);

            page.navigate("https://the-internet.herokuapp.com/");
            page.click("a[href='/add_remove_elements/']");
        }
    }

    /**
     * Тест проверки содержимого страницы:
     * 1. Проверка заголовка страницы
     * 2. Анализ HTML-контента
     * 3. Проверка текста элементов
     * 4. Верификация состояния загрузки
     */
    @Test
    void testPageContentVerification() {
        try (BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            page.onRequest(request ->
                    System.out.println("Обработан запрос: " + request.url()));

            page.navigate("https://the-internet.herokuapp.com/");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            String title = page.title();
            assertTrue(title.contains("The Internet"),
                    "Фактический заголовок: " + title);

            String headerText = page.locator("h1.heading").innerText();
            assertTrue(headerText.contains("Welcome to the-internet"),
                    "Текст заголовка: " + headerText);

            Locator examplesSection = page.locator("div#content ul");
            assertTrue(examplesSection.isVisible(),
                    "Секция примеров не отображается");
        }
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие браузера
     * 2. Освобождение ресурсов Playwright
     */
    @AfterAll
    static void cleanUp() {
        browser.close();
        playwright.close();
    }
}