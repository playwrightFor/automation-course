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


@Execution(ExecutionMode.CONCURRENT)
public class AddRemoveElementsTest {
    private static Page page;
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        BrowserContext context = browser.newContext();
        page = context.newPage();
        System.out.println("Before All Tests");
    }


    @Test
    void testAddRemoveElements() {
        // Переход на страницу добавления/удаления элементов
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        // Добавление элемента
        page.click("button:text('Add Element')");
        Locator addedElement = page.locator("button.added-manually");
        assertTrue(addedElement.isVisible(), "Элемент не был добавлен");

        // Скриншот после добавления
        addedElement.screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("after_add.png")));

        // Удаление элемента
        addedElement.click();
        assertTrue(addedElement.isHidden(), "Элемент не был удален");

        // Скриншот после удаления
        page.locator("body").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("after_remove.png")));
    }

    private Path getTimestampPath(String filename) {
        // Путь к папке screenshots для сохранения в указанное место
        Path screenshotsDir = Paths.get("screenshots");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd_HH-mm-ss"));
        return screenshotsDir.resolve(timestamp + "_" + filename);
    }

    @Test
    void testOne() {
        System.out.println("Running Test One");
    }

    @Test
    void testTwo() {
        System.out.println("Running Test Two");
    }

    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}