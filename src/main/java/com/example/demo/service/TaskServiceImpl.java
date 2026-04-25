package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
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
    public List<Task> allTask(){
        List<Task> tasks = taskRepository.findAll();
        return tasks;
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
    public String deleteTask(Long id) {
        try {
            if (taskRepository.existsById(id)){
                taskRepository.deleteById(id);
                return "успех! задача #" + id + "удалена.";
            }else {
                return "задача с таким ID не найдена в базе";
            }
        }catch (Exception e){
            return "ошибка при удалении: " + e.getMessage();
        }
    }

    @Override
    public String UpdateStatus(Long id, Status newStatus) {
        // 1. Пытаемся найти задачу в базе по ID
        return taskRepository.findById(id).map(task -> {
            // 2. Если нашли — меняем статус
            task.setStatus(newStatus);
            // 3. Сохраняем обновленный объект
            taskRepository.save(task);
            return "Статус задачи #" + id + " успешно изменен на " + newStatus;
        }).orElse("Ошибка: Задача с таким ID не найдена.");
    }

    @Override
    public String UpdateDifficulty(Long id, Difficulty newDifficulty) {
        // 1. Пытаемся найти задачу в базе по ID
        return taskRepository.findById(id).map(task -> {
            // 2. Если нашли — меняем сложность
            task.setDifficulty(newDifficulty);
            // 3. Сохраняем обновленный объект
            taskRepository.save(task);
            return "Сложность задачи #" + id + " успешно изменен на " + newDifficulty;
        }).orElse("Ошибка: Задача с таким ID не найдена.");
    }

    @Override
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Task> searchTasksByTopic(String topic) {
        return taskRepository.findByTopicIgnoreCase(topic);
    }

    @Override
    public List<Task> sortByTopicAndStatus(String topic, Status status) {
        return taskRepository.findByTopicIgnoreCaseAndStatus(topic, status);
    }

    @Override
    public List<Task> byStatusSorted(Status status) {
        return taskRepository.findByStatusOrderByLevelDesc(status);
    }

    // Вся логика получения данных для Dashboard теперь здесь

}
