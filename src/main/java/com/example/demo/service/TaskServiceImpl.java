package com.example.demo.service;

import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service// Говорим Спрингу, что это компонент для бизнес-логики
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllSortedByLevel() {
        List<Task> tasks = taskRepository.findAll();
        tasks.sort(Comparator.comparing(Task::getLevel));
        return tasks;
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
    public void addTask(String title, Integer level, String topic) {
        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setLevel(level);
        newTask.setTopic(topic);
        newTask.setStatus(Status.NEEDS_TO_BE_DECIDED);
        taskRepository.save(newTask);
    }

    @Override
    public void markAsDone(Long id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(Status.DONE);
            taskRepository.save(task);
        });
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Вся логика получения данных для Dashboard теперь здесь

}
