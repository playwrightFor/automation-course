package pom.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pom.pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.*;

public class DragDropTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    DragDropPage dragDropPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright
                .chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
        dragDropPage = new DragDropPage(page);
    }

    @Test
    void testDragAndDrop() {
        dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop");

        // Выполнение перетаскивания элемента "A" в зону "B"
        dragDropPage.dragDropArea().dragAToB();

        // Проверка, что текст в зоне "B" изменился на "A"
        assertEquals("A", dragDropPage.dragDropArea().getTextB(), "Текст в зоне 'B' не соответствует ожидаемому значению.");

        // Проверка, что текст в зоне "A" изменился на "B"
        assertEquals("B", dragDropPage.dragDropArea().getTextA(), "Текст в зоне 'A' не соответствует ожидаемому значению.");
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }
}
