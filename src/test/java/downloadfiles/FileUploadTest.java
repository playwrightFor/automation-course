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
 * Тестовый класс для проверки функционала загрузки и скачивания файлов.
 * Использует Playwright для автоматизации браузера и API-запросов.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class FileUploadTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Path testFile;
    APIRequestContext request;

    /**
     * Настройка тестового окружения перед каждым тестом:
     * 1. Инициализация Playwright
     * 2. Создание временного тестового PNG-файла
     * 3. Запуск браузера Chromium с графическим интерфейсом
     * 4. Создание контекста для API-запросов
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        testFile = createTestFile();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        request = playwright.request().newContext();
    }

    /**
     * Генерирует временный PNG-файл с минимально валидной структурой
     * @return Path созданного файла
     */
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
            throw new RuntimeException("Ошибка создания тестового файла", e);
        }
    }

    /**
     * Тест проверки загрузки и скачивания файла:
     * 1. Отправка файла через API
     * 2. Проверка корректности ответа
     * 3. Верификация содержимого файла
     * 4. Проверка скачивания PNG через API
     * 5. Валидация MIME-типа и сигнатуры файла
     */
    @Test
    void testFileUploadAndDownload() throws IOException {
        APIResponse uploadResponse = request.post(
                "https://httpbin.org/post",
                RequestOptions.create()
                        .setMultipart(
                                FormData.create()
                                        .set("file", testFile)
                        )
        );

        String responseBody = uploadResponse.text();
        assertTrue(responseBody.contains("data:image/png;base64"),
                "Отсутствует base64-представление файла в ответе");

        String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
        byte[] receivedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        assertArrayEquals(Files.readAllBytes(testFile), receivedBytes,
                "Загруженные данные не совпадают с исходным файлом");

        APIResponse downloadResponse = request.get("https://httpbin.org/image/png");
        assertEquals("image/png", downloadResponse.headers().get("content-type"),
                "Неверный MIME-тип скачанного файла");

        byte[] pngBytes = downloadResponse.body();
        assertTrue(pngBytes.length >= 8, "Некорректный размер файла");
        assertEquals(0x89, pngBytes[0] & 0xFF, "Неверная сигнатура PNG");
        assertEquals(0x50, pngBytes[1] & 0xFF, "Неверная сигнатура PNG");
        assertEquals(0x4E, pngBytes[2] & 0xFF, "Неверная сигнатура PNG");
        assertEquals(0x47, pngBytes[3] & 0xFF, "Неверная сигнатура PNG");
    }

    /**
     * Очистка тестового окружения:
     * 1. Удаление временного файла
     * 2. Закрытие API-контекста
     * 3. Закрытие страницы и браузера
     * 4. Освобождение ресурсов Playwright
     */
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