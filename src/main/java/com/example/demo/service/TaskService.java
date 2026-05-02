package com.example.demo.service;

import com.example.demo.Entity.Task;

import java.util.List;

public interface TaskService {
    // Вся логика получения данных для Dashboard теперь здесь
    List<Task> getAllSortedByLevel();
    int calculateProgress(List<Task> tasks);
    void addTask(String title, Integer level, String topic);
    void markAsDone(Long id);
    void deleteTask(Long id);
}
