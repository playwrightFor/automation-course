package pom.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED;


/**
 * Базовый класс!
 *
 * @author Oleg Todor
 * @since 2025-03-23
 */
public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void navigateTo(String url) {
        page.navigate(url);
        page.waitForLoadState(DOMCONTENTLOADED);
    }
}
