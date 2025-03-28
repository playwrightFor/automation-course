package pom.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;


/**
 * Класс-компонент для работы с областью перетаскивания элементов (Drag and Drop).
 * Реализует логику взаимодействия с элементами перетаскивания в рамках паттерна Page Object.
 *
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DragDropArea {
    private final Page page;
    private Locator elementA;
    private Locator elementB;

    /**
     * Конструктор инициализирует область перетаскивания:
     * 1. Сохраняет ссылку на объект страницы Playwright
     * 2. Выполняет первичную инициализацию элементов
     *
     * @param page экземпляр Playwright Page для работы с браузером
     */
    public DragDropArea(Page page) {
        this.page = page;
        initElements();
    }

    /**
     * Инициализирует элементы DOM:
     * 1. Локатор для элемента "A" (#column-a)
     * 2. Локатор для элемента "B" (#column-b)
     */
    private void initElements() {
        elementA = page.locator("#column-a");
        elementB = page.locator("#column-b");
    }

    /**
     * Выполняет операцию перетаскивания элемента A на элемент B.
     *
     * @return текущий экземпляр DragDropArea для fluent-интерфейса
     */
    public DragDropArea dragAToB() {
        elementA.dragTo(elementB);
        return this;
    }

    /**
     * Возвращает текстовое содержимое элемента B после обработки.
     *
     * @return очищенный от пробелов текст элемента B
     */
    public String getTextB() {
        return elementB.textContent().trim();
    }

    /**
     * Возвращает текстовое содержимое элемента A после обработки.
     *
     * @return очищенный от пробелов текст элемента A
     */
    public String getTextA() {
        return elementA.textContent().trim();
    }
}