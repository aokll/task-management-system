# Используем легкий образ с Java 21 (или твоей версией)
FROM eclipse-temurin:21-jdk-alpine

# Указываем рабочую папку внутри контейнера
WORKDIR /app

# Копируем наш скомпилированный JAR внутрь образа
COPY target/*.jar app.jar

# Команда, которая запустит приложение при старте контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]
