package pom.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED;

public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    // Метод для навигации на заданный URL
    public void navigateTo(String url) {
        page.navigate(url);
        page.waitForLoadState(DOMCONTENTLOADED);
    }
}
