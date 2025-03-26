package docker;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUploadTest {
    private static Playwright playwright;
    private static Browser browser;
    private static Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @Test
    void testFileUpload() {
        // Чтение BASE_URL из переменных окружения (значение по умолчанию: https://the-internet.herokuapp.com)
        String baseUrl = System.getenv().getOrDefault("BASE_URL", "https://the-internet.herokuapp.com");
        page.navigate(baseUrl + "/upload");

        // Загрузка файла
        page.locator("#file-upload").setInputFiles(Paths.get("test.txt"));
        page.locator("#file-submit").click();

        // Проверка успешной загрузки
        String successText = page.locator("h3").textContent();
        assertTrue(successText.contains("File Uploaded!"), "Файл не был загружен");
    }

    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}