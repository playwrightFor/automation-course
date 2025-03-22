package api;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws Exception {
        // 1. Выполнение GET-запроса напрямую через API
        APIResponse response = requestContext.get("/todos/1");

        // 2. Проверка статуса
        assertEquals(200, response.status());

        // 3. Парсинг JSON
        JsonNode json = objectMapper.readTree(response.text());

        // 4. Проверка структуры
        assertAll("JSON Validation",
                () -> assertEquals(1, json.get("id").asInt()),
                () -> assertEquals(1, json.get("userId").asInt()),
                () -> assertEquals("delectus aut autem", json.get("title").asText()),
                () -> assertFalse(json.get("completed").asBoolean())
        );
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}