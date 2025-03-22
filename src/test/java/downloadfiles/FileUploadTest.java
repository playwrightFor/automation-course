package downloadfiles;


import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class FileUploadTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Path testFile;
    APIRequestContext request;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        testFile = createTestFile();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        request = playwright.request().newContext();
    }

    private Path createTestFile() {
        try {
            Path path = Files.createTempFile("image", ".png");
            byte[] pngData = new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                    0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53, (byte) 0xDE
            };
            Files.write(path, pngData);
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Error creating test file", e);
        }
    }

    @Test
    void testFileUploadAndDownload() throws IOException {
        // 1. Загрузка файла и проверка содержимого
        APIResponse uploadResponse = request.post(
                "https://httpbin.org/post",
                RequestOptions.create()
                        .setMultipart(
                                FormData.create()
                                        .set("file", testFile)
                        )
        );

        String responseBody = uploadResponse.text();

        // 2. Проверка, что файл был получен сервером
        assertTrue(responseBody.contains("data:image/png;base64"),
                "Файл не был корректно загружен");

        // 3. Декодируем и проверяем содержимое
        String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
        byte[] receivedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        assertArrayEquals(Files.readAllBytes(testFile), receivedBytes,
                "Содержимое файла не совпадает");

        // 4. Проверка скачивания PNG
        APIResponse downloadResponse = request.get("https://httpbin.org/image/png");

        // 5. Проверка MIME-типа
        assertEquals("image/png", downloadResponse.headers().get("content-type"));

        // 6. Проверка сигнатуры PNG
        byte[] pngBytes = downloadResponse.body();
        assertTrue(pngBytes.length >= 8);
        assertEquals(0x89, pngBytes[0] & 0xFF);
        assertEquals(0x50, pngBytes[1] & 0xFF);
        assertEquals(0x4E, pngBytes[2] & 0xFF);
        assertEquals(0x47, pngBytes[3] & 0xFF);
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(testFile);
        } catch (Exception ignored) {
        }

        request.dispose();
        page.close();
        browser.close();
        playwright.close();
    }
}