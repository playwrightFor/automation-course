package di.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;


/**
 * Page Object для работы со страницей динамических элементов управления.
 * Инкапсулирует логику взаимодействия с элементами страницы.
 *
 * @author Oleg Todor
 * @since 2025-03-24
 */
public class DynamicControlsPage {
    private final Page page;
    private final Locator checkbox;
    private final Locator removeButton;

    /**
     * Инициализирует элементы страницы:
     * @param page Экземпляр Playwright Page для взаимодействия с браузером
     */
    public DynamicControlsPage(Page page) {
        this.page = page;
        this.checkbox = page.locator("#checkbox");
        this.removeButton = page.locator("button:has-text('Remove')");
    }

    /**
     * Переход на тестовую страницу динамических элементов
     */
    public void navigate() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    /**
     * Выполняет клик по кнопке удаления с ожиданием обработки операции
     */
    public void clickRemoveButton() {
        removeButton.click();
        page.waitForTimeout(5000);
    }

    /**
     * Проверяет видимость чекбокса на странице
     * @return true если элемент отображается, иначе false
     */
    public boolean isCheckboxVisible() {
        return checkbox.isVisible();
    }

    /**
     * Ожидает исчезновения чекбокса с использованием явного ожидания
     */
    public void waitForCheckboxDisappear() {
        checkbox.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(10000));
    }
}