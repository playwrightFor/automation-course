package pom.components;


import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;


/**
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DragDropArea {
    private final Page page;
    private Locator elementA;
    private Locator elementB;

    public DragDropArea(Page page) {
        this.page = page;
        initElements();
    }

    private void initElements() {
        elementA = page.locator("#column-a");
        elementB = page.locator("#column-b");
    }

    public DragDropArea dragAToB() {
        elementA.dragTo(elementB);
        return this;
    }

    public String getTextB() {
        return elementB.textContent().trim();
    }

    public String getTextA() {
        return elementA.textContent().trim();
    }
}