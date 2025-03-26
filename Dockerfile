# Базовый образ с предустановленными Playwright, Java и системными зависимостями
FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy
# Задаем рабочую директорию внутри контейнера
WORKDIR /app
# Копируем только файл зависимостей для оптимизации кэширования
COPY pom.xml .
# Скачиваем зависимости Maven и кэшируем их (~/.m2/repository)
RUN mvn dependency:go-offline
# Копируем исходный код
COPY src ./src
# Собираем проект
RUN mvn clean package -DskipTests
## Запускаем тесты
CMD ["mvn", "test", "-Dtest=TodoApiDockerTest", "-Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG"]