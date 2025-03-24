package di.tests;


import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// 1. Контекст для управления зависимостями
class TestContext {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    public TestContext() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    public Page getPage() {
        return page;
    }

    public void close() {
        page.close();
        browser.close();
        playwright.close();
    }
}