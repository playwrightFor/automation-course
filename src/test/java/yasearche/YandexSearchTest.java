//package yasearche;
//
//import base.BaseTest;
//import com.microsoft.playwright.Page;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.nio.file.Paths;
//
//public class YandexSearchTest extends BaseTest {
//
//    @Test
//    void testSearch() {
//        page.navigate("https://ya.ru");
//
//        // Ввод текста в поле поиска
//        page.fill("input#text", "Playwright java stepik");
//
//        // Клик по кнопке "Найти"
//        page.click("button[type='submit']");
//
//        // Ожидание появления результатов
//        page.waitForSelector("li.serp-item");
//
//        // Проверка, что результаты содержат искомый текст
//        String firstResult = page.textContent("li.serp-item");
//        Assertions.assertTrue(firstResult.contains("Playwright"));
//    }
//
//    @Test
//    void testSearchWithScreenshot() {
//        page.navigate("https://ya.ru");
//
//        // Ввод текста в поле поиска
//        page.fill("input#text", "Playwright java stepik");
//
//        // Клик по кнопке "Найти"
//        page.click("button[type='submit']");
//
//        // Ожидание появления результатов
//        page.waitForSelector("li.serp-item");
//
//        // Скриншот результатов поиска
//        page.screenshot(new Page.ScreenshotOptions()
//                .setPath(Paths.get("search_results.png"))
//                .setFullPage(true));
//    }
//
//    @Test
//    void testPageTitle() {
//        page.navigate("https://ya.ru");
//
//        // Проверка заголовка страницы
//        String title = page.title();
//        Assertions.assertEquals("Яндекс", title);
//    }
//}