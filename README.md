# Этап 1: Core CRUD Service

## Описание
Базовый микросервис для управления задачами обучения. Реализована основа архитектуры: работа с базой данных и REST-интерфейс.

## Технологический стек
* **Java 17**
* **Spring Boot 3** (Spring Data JPA, Web)
* **PostgreSQL** (In-memory для быстрой отладки)
* **Lombok**

## Что изучено и реализовано:
1. **Entity Mapping:** Создана сущность `Task` с автоматическим созданием даты (`@CreationTimestamp`).
2. **Persistence Layer:** Использование `JpaRepository` для базовых операций без написания SQL.
3. **REST Controller:**
    - `POST /api/tasks` — создание задачи (принимает JSON).
    - `GET /api/tasks` — получение списка всех задач.
    - `DELETE /api/tasks/{id}` — удаление по ID.
    - `PATCH /api/tasks/{id}/status` — частичное обновление статуса.

## Как запустить
Выполните команду: `./mvnw spring-boot:run`
API будет доступно по адресу: `http://localhost:8080/api/tasks`

## Тестирование
Использовал Postman для проверки работы программы.