package pom.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pom.pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тестовый класс для проверки функционала перетаскивания элементов (Drag and Drop).
 * Использует паттерн Page Object для организации тестового кода и Playwright для автоматизации браузера.
 *
 * @author Oleg Todor
 * @since 2025-03-23
 */
public class DragDropTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    DragDropPage dragDropPage;

    /**
     * Инициализация браузера перед всеми тестами:
     * 1. Создание экземпляра Playwright
     * 2. Запуск браузера Chromium в режиме с графическим интерфейсом
     */
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright
                .chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    /**
     * Подготовка тестового окружения перед каждым тестом:
     * 1. Создание нового контекста браузера
     * 2. Открытие новой страницы
     * 3. Инициализация Page Object для работы с элементами страницы
     */
    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
        dragDropPage = new DragDropPage(page);
    }

    /**
     * Тест проверки функционала перетаскивания элементов:
     * 1. Переход на тестовую страницу
     * 2. Выполнение операции перетаскивания элемента A в зону B
     * 3. Проверка изменения текста в обеих зонах
     */
    @Test
    void testDragAndDrop() {
        dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop");

        dragDropPage.dragDropArea().dragAToB();

        assertEquals("A", dragDropPage.dragDropArea().getTextB(),
                "Текст в зоне 'B' не соответствует ожидаемому значению после перетаскивания");
        assertEquals("B", dragDropPage.dragDropArea().getTextA(),
                "Текст в зоне 'A' не соответствует ожидаемому значению после перетаскивания");
    }

    /**
     * Завершение работы с контекстом браузера после каждого теста
     */
    @AfterEach
    void closeContext() {
        context.close();
    }

    /**
     * Завершение работы с браузером после всех тестов
     */
    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }
}