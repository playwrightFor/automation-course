package screenshotandvideo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестовый класс для проверки добавления и удаления элементов на веб-странице.
 * Использует Playwright для автоматизации браузера с параллельным выполнением тестов.
 *
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Execution(ExecutionMode.CONCURRENT)
public class AddRemoveElementsTest {
    private static Page page;
    private static Playwright playwright;
    private static Browser browser;

    /**
     * Инициализация браузера и страницы перед всеми тестами.
     * Запускает Chromium в режиме с графическим интерфейсом и создает новый контекст.
     */
    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        BrowserContext context = browser.newContext();
        page = context.newPage();
        System.out.println("Инициализация завершена перед всеми тестами");
    }

    /**
     * Проверяет функционал добавления/удаления элементов:
     * 1. Переход на тестовую страницу
     * 2. Добавление элемента и проверка его видимости
     * 3. Создание скриншота после добавления
     * 4. Удаление элемента и проверка его отсутствия
     * 5. Создание скриншота после удаления
     */
    @Test
    void testAddRemoveElements() {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        page.click("button:text('Add Element')");
        Locator addedElement = page.locator("button.added-manually");
        assertTrue(addedElement.isVisible(), "Элемент не был добавлен");

        addedElement.screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("after_add.png")));

        addedElement.click();
        assertTrue(addedElement.isHidden(), "Элемент не был удален");

        page.locator("body").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("after_remove.png")));
    }

    /**
     * Генерирует путь для сохранения скриншотов с временной меткой.
     *
     * @param filename базовое имя файла (будет дополнено временной меткой)
     * @return Путь к файлу в формате: screenshots/ГГ-ММ-ДД_ЧЧ-мм-сс_filename
     */
    private Path getTimestampPath(String filename) {
        Path screenshotsDir = Paths.get("screenshots");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd_HH-mm-ss"));
        return screenshotsDir.resolve(timestamp + "_" + filename);
    }

    /**
     * Пример тестового метода для демонстрации работы
     */
    @Test
    void testOne() {
        System.out.println("Выполнение теста 1");
    }

    /**
     * Пример тестового метода для демонстрации работы
     */
    @Test
    void testTwo() {
        System.out.println("Выполнение теста 2");
    }

    /**
     * Закрывает браузер и освобождает ресурсы после выполнения всех тестов
     */
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}