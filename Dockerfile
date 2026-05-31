# Шаг 1: Сборка приложения внутри контейнера
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Копируем настройки и исходный код
COPY pom.xml .
COPY src ./src
# Собираем чистый JAR без запуска тестов
RUN mvn clean package -DskipTests

# Шаг 2: Запуск готового приложения
FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app
# Забираем собранный JAR из первого шага
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
