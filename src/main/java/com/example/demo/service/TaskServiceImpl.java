package com.example.demo.service;

import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service// Говорим Спрингу, что это компонент для бизнес-логики
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Вспомогательный метод: кто сейчас залогинен?
    private User getCurrentUser(){
        String username = org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    @Override
    public List<Task> getAllSortedByLevel() {
        // берем не всё (findAll), а только задачи ТЕКУЩЕГО юзера
        String username = org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> tasks = taskRepository.findByOwnerUsername(username);
        tasks.sort(Comparator.comparing(Task::getLevel));
        return tasks;
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
    public int calculateProgress(List<Task> tasks) {
        if(tasks.isEmpty()) return 0;
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
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

}
