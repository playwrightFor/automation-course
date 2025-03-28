package api;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки работы REST API сервиса.
 * Осуществляет проверку корректности структуры и данных JSON-ответа.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class TodoApiTest {
    private Playwright playwright;
    private APIRequestContext requestContext;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализация тестового окружения перед каждым тестом:
     * 1. Создание экземпляра Playwright
     * 2. Настройка контекста API-запросов с базовым URL
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    /**
     * Тест проверки конечной точки /todos:
     * 1. Выполнение GET-запроса для получения элемента
     * 2. Проверка кода статуса HTTP-ответа
     * 3. Валидация структуры и содержимого JSON
     *
     * @throws Exception при ошибках парсинга JSON
     */
    @Test
    void testTodoApi() throws Exception {
        APIResponse response = requestContext.get("/todos/1");

        assertEquals(200, response.status(), "Неверный статус код ответа");

        JsonNode json = objectMapper.readTree(response.text());

        assertAll("Проверка структуры ответа",
                () -> assertEquals(1, json.get("id").asInt(), "Неверный ID элемента"),
                () -> assertEquals(1, json.get("userId").asInt(), "Неверный ID пользователя"),
                () -> assertEquals("delectus aut autem", json.get("title").asText(), "Неверный заголовок"),
                () -> assertFalse(json.get("completed").asBoolean(), "Некорректный статус выполнения")
        );
    }

    /**
     * Освобождение ресурсов после выполнения теста:
     * 1. Закрытие контекста API-запросов
     * 2. Завершение работы Playwright
     */
    @AfterEach
    void tearDown() {
        if (requestContext != null) {
            requestContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}