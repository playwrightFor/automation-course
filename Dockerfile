#FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy
#
#WORKDIR /app
#COPY . .
#RUN apt-get update && apt-get install -y maven && mvn clean install

# Используем официальный образ Playwright для Java
FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только POM-файл сначала (для кеширования зависимостей)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и тестовые файлы
COPY src ./src
COPY test.txt /app/test.txt

# Собираем проект (без запуска тестов)
RUN mvn clean package -DskipTests