package pom.pages;

import com.microsoft.playwright.Page;
import pom.components.DragDropArea;

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
