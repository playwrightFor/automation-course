package di.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;


// 1. Контекст для управления зависимостями
class TestContext {
    private final Playwright playwright;
    private final Browser browser;
    private final Page page;

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