package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.dto.TaskDto;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service// Говорим Спрингу, что это компонент для бизнес-логики
@RequiredArgsConstructor// Lombok сам создаст конструктор для маппера и репозитория
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    // Вспомогательный метод: кто сейчас залогинен?
    private User getCurrentUser(){
        String username = org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    @Override
    public List<TaskDto> getAllSortedByLevel() {
        // берем не всё (findAll), а только задачи ТЕКУЩЕГО юзера
        String username = org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> tasks = taskRepository.findByOwnerUsername(username);
        tasks.sort(Comparator.comparing(Task::getLevel));

        if (tasks == null) return new ArrayList<>();

        return tasks.stream()
                .map(taskMapper::toDto)// Используем маппер для каждой задачи
                .collect(Collectors.toList());
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
        if(tasks == null || tasks.isEmpty()) return 0;
        long completed = tasks.stream()
                .filter(t -> "DONE".equals(t.getStatus()))
                .count();
        return (int) ((completed * 100) / tasks.size());
    }

    @Override
    public void markAsDone(Long id) {
        taskRepository.findById(id).ifPresent(task -> {

            // Проверка безопасности: менять статус можно только СВОЕЙ задаче
            if (task.getOwner().getUsername().equals(getCurrentUser().getUsername())){
                task.setStatus(Status.DONE);
                taskRepository.save(task);
            }
        });
    }

    @Override
    public void deleteTask(Long id) {
        // Здесь тоже стоит добавить проверку владения перед удалением
        taskRepository.findById(id).ifPresent(task -> {
            if (task.getOwner().getUsername().equals(getCurrentUser().getUsername())){
                taskRepository.delete(task);
            }
        });
    }

    @Override
    public TaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .filter(task -> task.getOwner().getUsername().equals(getCurrentUser().getUsername()))
                .map(taskMapper::toDto).orElseThrow(()-> new RuntimeException("Задача не найдена или доступ запрещен"));
    }

    @Override
    public void updateTask(Long id, String title, Integer level, String topic, Difficulty difficulty) {
taskRepository.findById(id).ifPresent(task -> {
    if (task.getOwner().getUsername().equals(getCurrentUser().getUsername())){
        task.setTitle(title);
        task.setLevel(level);
        task.setTopic(topic);
        task.setDifficulty(difficulty);
        taskRepository.save(task);
    }
});
    }

/**    public List<TaskDto> findAllTasksDto(){
//        return taskRepository.findAll().stream()
//                .map(taskMapper::toDto)// Магия: превращаем каждую Task в TaskDto
//                .collect(Collectors.toList());
//    }
//
//    // Аналогично для задач конкретного пользователя
//    public List<TaskDto> findTasksByOwner(User owner){
//        return taskRepository.findByOwner(owner).stream()
//                .map(taskMapper::toDto)
//                .collect(Collectors.toList());
**/
}
