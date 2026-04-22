package com.example.demo.repository;

import com.example.demo.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /*
    * Наследование от JpaRepository<Task, Long> дает готовый инструмент
    * для работы с базой. мы просто вызываешь taskRepository.save(myTask),
    * и данные улетают в PostgreSQL.
    * */
}
