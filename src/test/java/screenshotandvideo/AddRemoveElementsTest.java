package screenshotandvideo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
    private static BrowserContext context;
    private static Page page;
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        context = browser.newContext();
        page = context.newPage();
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
        return Paths.get(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"))
                        + "_" + filename);
    }

    @AfterAll
    static void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}