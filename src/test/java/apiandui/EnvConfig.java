package apiandui;

/**
 * Конфигурационный интерфейс для определения параметров тестового окружения.
 * Использует библиотеку Owner для связывания с внешними свойствами.
 *
 * Пример использования в системных свойствах или .properties файлах.
 */
public interface EnvConfig extends org.aeonbits.owner.Config {

    /**
     * Возвращает базовый URL тестового окружения.
     *
     * @return базовый URL приложения
     *
     * @Key("base.url") связывает метод с property-ключом
     * @DefaultValue значение по умолчанию, если свойство не задано
     */
    @Key("base.url")
    @DefaultValue("https://the-internet.herokuapp.com")
    String baseUrl();
}