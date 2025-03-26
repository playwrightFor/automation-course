FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y maven && mvn clean install