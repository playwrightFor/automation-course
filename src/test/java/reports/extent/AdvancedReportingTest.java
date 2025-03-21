package reports.extent;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Oleg Todor
 * @since 2025-03-21
 */
@Epic("Тесты для the-internet.herokuapp.com")
@Feature("Работа с JavaScript-алертами")
public class AdvancedReportingTest {
    private static ExtentReports extent;
    private ExtentTest extentTest;
    private Page page;

    @BeforeAll
    static void setupExtent() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @Test
    @Story("Проверка алертов")
    @Description("Тест взаимодействия с JS-алертами")
    @Severity(SeverityLevel.NORMAL)
    void testJavaScriptAlerts() {
        extentTest = extent.createTest("Тест JS-алертов");

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch()) {

            extentTest.log(Status.INFO, "Запущен браузер Chromium");
            BrowserContext context = browser.newContext();
            page = context.newPage();

            Allure.step("Открыть страницу с алертами", () -> {
                page.navigate("https://the-internet.herokuapp.com/javascript_alerts");
                extentTest.pass("Страница загружена");
            });

            // Переменная для хранения текста алерта
            final String[] alertText = new String[1];

            page.onDialog(dialog -> {
                alertText[0] = dialog.message(); // Сохраняем текст алерта
                dialog.accept();
            });

            // Клик на кнопку, которая вызывает алерт
            Allure.step("Кликнуть на кнопку Alert", () -> {
                page.click("button[onclick='jsAlert()']");
                extentTest.pass("Кнопка Alert нажата");
            });


            page.waitForTimeout(500);

            Allure.step("Проверить текст алерта", () -> {
                assertThat(alertText[0]).isEqualTo("I am a JS Alert");
                extentTest.pass("Текст алерта корректен: " + alertText[0]);
            });

            // Скриншот при успешном выполнении - указвам папку
            String screenshotPath = "screenshots/alert-success.png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
            extentTest.addScreenCaptureFromPath(screenshotPath);

        } catch (Exception e) {
            if (page != null) {
                String errorScreenshotPath = "alert-error.png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(errorScreenshotPath)));
                Allure.addAttachment("Ошибка", "image/png", Paths.get(errorScreenshotPath).toUri().toString(), "png");
            }
            extentTest.fail("Тест упал: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void tearDown() {
        extent.flush();
    }
}
