package docker;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки работы API сервиса.
 * Использует Playwright для выполнения HTTP-запросов и проверки ответов.
 * Взаимодействует с внешним API по адресу https://jsonplaceholder.typicode.com.
 *
 * @author Oleg Todor
 * @since 2025-03-26
 */
public class TodoApiDockerTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Настройка тестового окружения перед каждым тестом:
     * 1. Инициализация Playwright
     * 2. Создание контекста для API-запросов с базовым URL
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
     * Тест проверки работы конечной точки /todos:
     * 1. Выполнение GET-запроса для получения элемента
     * 2. Проверка кода статуса ответа
     * 3. Парсинг и валидация структуры JSON
     * 4. Проверка значений полей ответа
     *
     * @throws Exception при ошибках парсинга JSON
     */
    @Test
    void testTodoApi() throws Exception {
        APIResponse response = requestContext.get("/todos/1");

        assertEquals(200, response.status(), "Неверный статус код ответа");

        JsonNode json = objectMapper.readTree(response.text());

        assertAll("Проверка структуры JSON",
                () -> assertEquals(1, json.get("id").asInt(), "Неверный ID"),
                () -> assertEquals(1, json.get("userId").asInt(), "Неверный User ID"),
                () -> assertEquals("delectus aut autem", json.get("title").asText(), "Неверный заголовок"),
                () -> assertFalse(json.get("completed").asBoolean(), "Некорректный статус выполнения")
        );
    }

    /**
     * Очистка ресурсов после выполнения теста:
     * 1. Закрытие контекста запросов
     * 2. Освобождение ресурсов Playwright
     */
    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}