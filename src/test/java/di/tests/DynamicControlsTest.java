package di.tests;


import di.components.DynamicControlsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * Тестовый класс для проверки функционала динамических элементов управления.
 * Демонстрирует использование внедрения зависимостей (DI) для управления ресурсами.
 *
 * @author Oleg Todor
 * @since 2025-03-24
 */
public class DynamicControlsTest {
    private TestContext context;
    private DynamicControlsPage controlsPage;

    /**
     * Инициализация тестового окружения перед каждым тестом:
     * 1. Создание контекста с ресурсами
     * 2. Инициализация Page Object через внедрение зависимости
     * 3. Переход на тестовую страницу
     */
    @BeforeEach
    void setup() {
        context = new TestContext();
        controlsPage = new DynamicControlsPage(context.getPage());
        controlsPage.navigate();
    }

    /**
     * Тест проверки удаления чекбокса:
     * 1. Клик по кнопке удаления
     * 2. Ожидание исчезновения элемента
     * 3. Проверка отсутствия чекбокса на странице
     */
    @Test
    void testCheckboxRemoval() {
        controlsPage.clickRemoveButton();
        controlsPage.waitForCheckboxDisappear();

        assertFalse(controlsPage.isCheckboxVisible(),
                "Чекбокс должен быть скрыт после выполнения операции удаления");
    }

    /**
     * Завершение работы тестового окружения:
     * Освобождение всех связанных ресурсов через контекст
     */
    @AfterEach
    void teardown() {
        context.close();
    }
}