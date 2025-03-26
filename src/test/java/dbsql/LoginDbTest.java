//package dbsql;
//
//import com.microsoft.playwright.*;
//import org.aeonbits.owner.ConfigFactory;
//import org.junit.jupiter.api.*;
//import java.sql.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//// Конфигурация БД
//interface DbConfig extends org.aeonbits.owner.Config {
//    @Key("db.url")
//    @DefaultValue("jdbc:postgresql://localhost:5432/test_db")
//    String dbUrl();
//
//    @Key("db.user")
//    @DefaultValue("admin")
//    String dbUser();
//
//    @Key("db.password")
//    @DefaultValue("secret")
//    String dbPassword();
//}
//
//public class LoginDbTest {
//    private Connection connection;
//    private Page page;
//    private Browser browser;
//    private static DbConfig dbConfig;
//
//    @BeforeAll
//    static void loadConfig() {
//        dbConfig = ConfigFactory.create(DbConfig.class, System.getProperties());
//    }
//
//    @BeforeEach
//    void setup() throws SQLException {
//        // Создание пользователя в БД
//        connection = DriverManager.getConnection(
//                dbConfig.dbUrl(),
//                dbConfig.dbUser(),
//                dbConfig.dbPassword()
//        );
//
//        try (Statement stmt = connection.createStatement()) {
//            stmt.executeUpdate(
//                    "INSERT INTO users (username, password) VALUES ('test_user', 'test_pass')"
//            );
//        }
//
//        // Инициализация Playwright
//        Playwright playwright = Playwright.create();
//        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
//        page = browser.newPage();
//    }
//
//    @Test
//    void testLoginWithDbUser() throws SQLException {
//        // Получение данных из БД
//        String username = null;
//        String password = null;
//
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(
//                     "SELECT username, password FROM users WHERE username = 'test_user'")) {
//
//            if (rs.next()) {
//                username = rs.getString("username");
//                password = rs.getString("password");
//            }
//        }
//
//        assertNotNull(username, "Username not found in DB");
//        assertNotNull(password, "Password not found in DB");
//
//        // Выполнение логина
//        page.navigate("https://the-internet.herokuapp.com/login");
//        page.locator("#username").fill(username);
//        page.locator("#password").fill(password);
//        page.locator("button[type='submit']").click();
//
//        // Проверка успешной авторизации
//        assertTrue(page.locator(".flash.success").isVisible());
//        assertTrue(page.url().endsWith("/secure"));
//    }
//
//    @AfterEach
//    void teardown() throws SQLException {
//        // Удаление тестового пользователя
//        try (Statement stmt = connection.createStatement()) {
//            stmt.executeUpdate(
//                    "DELETE FROM users WHERE username = 'test_user'"
//            );
//        }
//
//        // Закрытие ресурсов
//        if (connection != null) connection.close();
//        if (page != null) page.close();
//        if (browser != null) browser.close();
//    }
//}