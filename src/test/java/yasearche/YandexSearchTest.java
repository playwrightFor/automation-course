//package yasearche;
//
//import base.BaseTest;
//import com.microsoft.playwright.Page;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.nio.file.Paths;
//
//
///**
// * Тестовый класс для проверки функционала поиска в Яндекс.
// * Проверяет основные сценарии работы поисковой системы, включая визуальную проверку через скриншоты.
// * Наследует базовые настройки тестового окружения из {@link BaseTest}.
// *
// * @author Oleg Todor
// * @since 2025-03-22
// */
//public class YandexSearchTest extends BaseTest {
//
//    /**
//     * Проверяет базовый сценарий поиска:
//     * 1. Переход на главную страницу Яндекса
//     * 2. Ввод поискового запроса
//     * 3. Запуск поиска
//     * 4. Проверка отображения результатов
//     * 5. Верификация содержания первого результата
//     */
//    @Test
//    void testSearch() {
//        page.navigate("https://ya.ru");
//
//        page.fill("input#text", "Playwright java stepik");
//        page.click("button[type='submit']");
//
//        page.waitForSelector("li.serp-item");
//        String firstResult = page.textContent("li.serp-item");
//
//        Assertions.assertTrue(firstResult.contains("Playwright"),
//                "Результаты поиска не содержат искомое ключевое слово");
//    }
//
//    /**
//     * Проверяет поиск с созданием скриншота страницы:
//     * 1. Выполняет стандартный поисковый запрос
//     * 2. Сохраняет полный скриншот страницы с результатами
//     * 3. Скриншот сохраняется в файл search_results.png
//     */
//    @Test
//    void testSearchWithScreenshot() {
//        page.navigate("https://ya.ru");
//
//        page.fill("input#text", "Playwright java stepik");
//        page.click("button[type='submit']");
//
//        page.waitForSelector("li.serp-item");
//        page.screenshot(new Page.ScreenshotOptions()
//                .setPath(Paths.get("search_results.png"))
//                .setFullPage(true));
//    }
//
//    /**
//     * Проверяет корректность заголовка главной страницы:
//     * 1. Переход на главную страницу
//     * 2. Получение и верификация заголовка страницы
//     */
//    @Test
//    void testPageTitle() {
//        page.navigate("https://ya.ru");
//        String title = page.title();
//
//        Assertions.assertEquals("Яндекс", title,
//                "Заголовок страницы не соответствует ожидаемому значению");
//    }
//}