package docker;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки функционала загрузки файлов через веб-интерфейс.
 * Использует Playwright для автоматизации браузера Chromium в headless-режиме.
 *
 * @author Oleg Todor
 * @since 2025-03-26
 */
public class FileUpTest {
    private static Playwright playwright;
    private static Browser browser;
    private static Page page;

    /**
     * Инициализация тестового окружения перед всеми тестами:
     * 1. Создание экземпляра Playwright
     * 2. Запуск браузера Chromium с графическим интерфейсом
     * 3. Создание новой страницы для тестирования
     */
    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    /**
     * Тест проверки загрузки файла:
     * 1. Переход на страницу загрузки (BASE_URL из переменных окружения или значение по умолчанию)
     * 2. Ожидание и выбор файла для загрузки
     * 3. Отправка формы
     * 4. Проверка сообщения об успешной загрузке с таймаутом 15 секунд
     */
    @Test
    void testFileUpload() {
        String baseUrl = System.getenv().getOrDefault("BASE_URL", "https://the-internet.herokuapp.com");
        page.navigate(baseUrl + "/upload");

        page.locator("#file-upload").waitFor(new Locator.WaitForOptions().setTimeout(10000));
        page.locator("#file-upload").setInputFiles(Paths.get("test.txt"));
        page.locator("#file-submit").click();

        String successText = page.locator("h3")
                .textContent(new Locator.TextContentOptions().setTimeout(15000));
        assertTrue(successText.contains("File Uploaded!"),
                "Отсутствует сообщение об успешной загрузке. Фактический текст: " + successText);
    }

    /**
     * Завершение работы тестового окружения:
     * 1. Закрытие браузера
     * 2. Освобождение ресурсов Playwright
     */
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}