package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;

import java.util.List;

public interface TaskService {
    // Вся логика получения данных для Dashboard теперь здесь

    //посмотреть все задачи
    List<Task> allTask();
    //сортировка по уровню
    List<Task> getAllSortedByLevel();
    int calculateProgress(List<Task> tasks);
    //добавить задачу
    void addTask(String title, Integer level, String topic);
    void markAsDone(Long id);
    //удалить задачу
    String deleteTask(Long id);
    String UpdateStatus (Long id, Status newStatus);
    String UpdateDifficulty (Long id, Difficulty newDifficulty);
    List<Task> searchTasksByTitle(String title);
    List<Task> searchTasksByTopic(String topic);
    List<Task> sortByTopicAndStatus(String topic, Status status);
    List<Task> byStatusSorted(Status status);
}
