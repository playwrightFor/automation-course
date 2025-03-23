package pom.pages;

import com.microsoft.playwright.Page;
import pom.components.DragDropArea;


/**
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;

    public DragDropPage(Page page) {
        super(page);
    }

    // Ленивая инициализация компонента DragDropArea
    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }
}
