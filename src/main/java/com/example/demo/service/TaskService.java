package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.dto.TaskDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TaskService {
    // Вся логика получения данных для Dashboard теперь здесь
    List<TaskDto> getTasksByTopic(String topic);
    List<TaskDto> getTasksByTitle(String title);
    List<Task> getByStatusSorted (Status status);
    List<TaskDto> getAllSortedByLevel();
    int calculateProgress(List<TaskDto> tasks);
    void addTask(String title, Integer level, String topic);
    void markAsDone(Long id);
    void deleteTask(Long id);
    TaskDto getTaskById(Long id);//метод для получения задачи перед правкой
    void updateTask (Long id, String title, Integer level, String topic, Difficulty difficulty);//сохранение изменений

}
