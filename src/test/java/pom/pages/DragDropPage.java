package pom.pages;

import com.microsoft.playwright.Page;
import pom.components.DragDropArea;


/**
 * Класс страницы для работы с функционалом перетаскивания элементов (Drag and Drop).
 * Наследует базовую функциональность из {@link BasePage} и предоставляет доступ к области перетаскивания.
 *
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;

    /**
     * Конструктор инициализирует страницу через базовый класс.
     *
     * @param page экземпляр Playwright Page для взаимодействия с браузером
     */
    public DragDropPage(Page page) {
        super(page);
    }

    /**
     * Возвращает объект для работы с областью перетаскивания.
     * Реализует ленивую инициализацию (создание объекта при первом обращении).
     *
     * @return экземпляр {@link DragDropArea} для взаимодействия с элементами перетаскивания
     */
    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }
}
