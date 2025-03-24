package di.tests;


import di.components.DynamicControlsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Тестовый класс с DI
 *
 * @author Oleg Todor
 * @since 2025-03-24
 */
public class DynamicControlsTest {
    private TestContext context;
    private DynamicControlsPage controlsPage;

    @BeforeEach
    void setup() {
        // 4. Инициализация контекста и внедрение зависимости
        context = new TestContext();
        controlsPage = new DynamicControlsPage(context.getPage());
        controlsPage.navigate();
    }

    @Test
    void testCheckboxRemoval() {
        // 5. Выполнение действий и проверки
        controlsPage.clickRemoveButton();
        controlsPage.waitForCheckboxDisappear();

        assertFalse(controlsPage.isCheckboxVisible(),
                "Чекбокс должен быть скрыт после нажатия кнопки");
    }

    @AfterEach
    void teardown() {
        // 6. Очистка ресурсов
        context.close();
    }
}