# Используем легкий образ с Java 21 (или твоей версией)
FROM eclipse-temurin:17-jdk-alpine

# Указываем рабочую папку внутри контейнера
WORKDIR /app

# Копируем наш скомпилированный JAR внутрь образа
COPY ./target/demo-0.0.1-SNAPSHOT.jar app.jar

# Команда, которая запустит приложение при старте контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]
