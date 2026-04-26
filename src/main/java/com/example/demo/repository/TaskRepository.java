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
    /*
    // Магия: Spring поймет, что нужно искать по полю title,
    // которое СОДЕРЖИТ (Containing) текст, игнорируя регистр (IgnoreCase)
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByTopicIgnoreCase (String topic);

    List<Task> findByTopicIgnoreCaseAndStatus (String topic, Status status);

    //найти все задачи у которых статус соответствует заданному и отсортировать
    // их по уровню от самого сложного к самому легкому
    List<Task> findByStatusOrderByLevelDesc(Status status);


}
