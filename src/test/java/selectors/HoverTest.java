package selectors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функционала наведения курсора на элементы.
 * Проверяет отображение скрытых элементов и корректность переходов при взаимодействии.
 *
 * @author Oleg Todor
 * @since 2025-03-22
 */
public class HoverTest {
    Playwright playwright;
    Browser browser;
    Page page;

    /**
     * Подготавливает окружение перед каждым тестом:
     * 1. Инициализирует движок Playwright
     * 2. Запускает браузер Chromium с графическим интерфейсом
     * 3. Открывает тестовую страницу с элементами для наведения
     */
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://the-internet.herokuapp.com/hovers");
    }

    /**
     * Проверяет сценарий взаимодействия с элементами при наведении:
     * 1. Находит все тестируемые элементы на странице
     * 2. Для каждого элемента:
     *    - Наводит курсор и проверяет появление ссылки
     *    - Выполняет переход по ссылке и проверяет URL
     *    - Возвращается назад для повторения теста со следующим элементом
     */
    @Test
    void testHoverProfiles() {
        Locator figures = page.locator(".figure");
        int figureCount = figures.count();

        for (int i = 0; i < figureCount; i++) {
            Locator figure = figures.nth(i);

            figure.hover();
            Locator profileLink = figure.locator("text=View profile");
            assertTrue(profileLink.isVisible(), "Текст 'View profile' не отображается для элемента " + i);

            page.waitForLoadState(LoadState.LOAD);
            profileLink.click();

            String expectedUrlPart = "/users/" + (i + 1);
            assertTrue(page.url().contains(expectedUrlPart),
                    "URL не содержит " + expectedUrlPart + ". Фактический URL: " + page.url());

            page.goBack();
            figures = page.locator(".figure");
        }
    }

    /**
     * Очищает ресурсы после выполнения теста:
     * 1. Закрывает текущую страницу
     * 2. Останавливает браузер
     * 3. Освобождает ресурсы Playwright
     */
    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}