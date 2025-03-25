package di.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;


// 2. Page Object с логикой работы с элементами
public class DynamicControlsPage {
    private final Page page;
    private final Locator checkbox;
    private final Locator removeButton;

    public DynamicControlsPage(Page page) {
        this.page = page;
        this.checkbox = page.locator("#checkbox");
        this.removeButton = page.locator("button:has-text('Remove')");
    }

    public void navigate() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    public void clickRemoveButton() {
        removeButton.click();
        page.waitForTimeout(5000);
    }

    public boolean isCheckboxVisible() {
        return checkbox.isVisible();
    }

    public void waitForCheckboxDisappear() {
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }
}