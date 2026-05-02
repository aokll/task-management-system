package com.example.demo.repository;

import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
 * Наследование от JpaRepository<Task, Long> дает готовый инструмент
 * для работы с базой. мы просто вызываешь taskRepository.save(myTask),
 * и данные улетают в PostgreSQL.
 * */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    //Поиск задач конкретного пользователя по части заголовка
    List<Task> findByOwnerUsernameAndTitleContainingIgnoreCase(String username, String title);

    //Поиск по теме только для конкретного пользователя
    List<Task> findByOwnerUsernameAndTopicIgnoreCase(String username, String topic);

    //find...By - Сделай запрос
    //Owner - Найди в классе Task поле owner
    //Username - Внутри объекта Owner возьми поле username
    //And - Добавь в SQL условие AND
    //Topic - Сравни с полем topic в таблице задач
    //IgnoreCase - Применяй функцию LOWER() к теме, чтобы 'JAVA' и 'java' были одним и тем же
    //And - Добавь еще одно условие AND
    //Status: «Сравни с полем status
    // - Дай задачи по теме topic и статусу status, но ТОЛЬКО те, которые принадлежат пользователю username
    List<Task> findByOwnerUsernameAndTopicIgnoreCaseAndStatus (String username, String topic, Status status);

    //найти все задачи у которых статус соответствует заданному и отсортировать
    // их по уровню от самого сложного к самому легкому
    List<Task> findByOwnerUsernameAndStatusOrderByLevelDesc(String username, Status status);

    // Просто все задачи текущего пользователя (самый частый запрос для главного экрана)
    List<Task> findByOwnerUsername (String username);


}
