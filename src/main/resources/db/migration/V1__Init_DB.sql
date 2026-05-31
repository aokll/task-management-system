-- 1. Создаем таблицу пользователей
CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR (255) NOT NULL UNIQUE,
    password VARCHAR (255) NOT NULL,
    active BOOLEAN NOT NULL
);

-- 2. Создаем таблицу задач (связь Many-to-One с users)
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR (50) NOT NULL,
    level INTEGER,
    topic VARCHAR (255),
    status VARCHAR (255),
    difficulty VARCHAR (255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 3. Создаем таблицу ролей (коллекция ElementCollection для User)
CREATE TABLE user_role(
    user_id BIGINT NOT NULL,
    roles VARCHAR (255) NOT NULL,
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, roles)
);