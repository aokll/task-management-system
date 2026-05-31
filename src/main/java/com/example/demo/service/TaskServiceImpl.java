package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.dto.TaskDto;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service// Говорим Спрингу, что это компонент для бизнес-логики
@RequiredArgsConstructor// Lombok сам создаст конструктор для маппера и репозитория
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskAnalysisService analysisService;

    // 1. Безопасность (DRY): Единая точка получения имени пользователя
    private String getCurrentUsername() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
    }

    // 2. Универсальный конвертер списка (Entity -> DTO)
    private List<TaskDto> mapListToDto(List<Task> tasks) {
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод: кто сейчас залогинен?
    private User getCurrentUser() {
        return userRepository.findByUsername(getCurrentUsername());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.findById(id).ifPresentOrElse(task -> {
            // Проверяем владельца
            if (task.getOwner().getUsername().equals(getCurrentUsername())) {
                taskRepository.delete(task);
            } else {
                throw new RuntimeException("Доступ запрещен: это не ваша задача");
            }
        }, () -> {
            throw new RuntimeException("Задача с ID " + id + " не найдена");
        });
    }

    @Override
    public List<TaskDto> getTasksByTitle(String title) {
        return mapListToDto(taskRepository.findByOwnerUsernameAndTitleContainingIgnoreCase(getCurrentUsername(), title));
    }

    @Override
    public List<TaskDto> getTasksByTopic(String topic) {
        return mapListToDto(taskRepository.findByOwnerUsernameAndTopicIgnoreCase(getCurrentUsername(), topic));
    }

//    @Override
//    public List<Task> getTaskByTopicAndStatus(String topic, Status status) {
//        return taskRepository.findByOwnerUsernameAndTopicIgnoreCaseAndStatus(getCurrentUser().getUsername(), topic, status);
//    }

    @Override
    public List<Task> getByStatusSorted(Status status) {
        return taskRepository.findByOwnerUsernameAndStatusOrderByLevelDesc(getCurrentUser().getUsername(), status);
    }

    @Override
    public List<TaskDto> getAllSortedByLevel() {
        List<Task> tasks = taskRepository.findByOwnerUsername(getCurrentUsername());
        tasks.sort(Comparator.comparing(Task::getLevel));
        return mapListToDto(tasks);
    }

    @Override
    public void addTask(String title, Integer level, String topic) {
        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setLevel(level);
        newTask.setTopic(topic);
        newTask.setStatus(Status.NEEDS_TO_BE_DECIDED);
        newTask.setOwner(getCurrentUser());
        taskRepository.save(newTask);
    }

    @Override
    public int calculateProgress(List<TaskDto> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0;
        long completed = tasks.stream()
                .filter(t -> "DONE".equals(t.getStatus()))
                .count();
        return (int) ((completed * 100) / tasks.size());
    }

    @Override
    public synchronized void markAsDone(Long id) {
        taskRepository.findById(id).ifPresent(task -> {
            // Проверка безопасности через наш метод
            if (task.getOwner().getUsername().equals(getCurrentUsername())
                    && task.getStatus() != Status.IN_PROGRESS) {

                task.setStatus(Status.ANALYZING);
                taskRepository.saveAndFlush(task);
                analysisService.analyzeTask(task.getId());
            }
        });
    }


    @Override
    public TaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .filter(task -> task.getOwner().getUsername().equals(getCurrentUser().getUsername()))
                .map(taskMapper::toDto).orElseThrow(() -> new RuntimeException("Задача не найдена или доступ запрещен"));
    }

    @Override
    public void updateTask(Long id, String title, Integer level, String topic, Difficulty difficulty) {
        taskRepository.findById(id).ifPresent(task -> {
            if (task.getOwner().getUsername().equals(getCurrentUsername())) {
                task.setTitle(title);
                task.setLevel(level);
                task.setTopic(topic);
                task.setDifficulty(difficulty);
                taskRepository.save(task);
            }
        });
    }
}