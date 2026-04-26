package com.example.demo.controller;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@Validated // ЭТА СТРОЧКА ВКЛЮЧАЕТ ПРОВЕРКУ @RequestParam
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    // Сюда мы скоро добавим методы для создания и просмотра твоих задач

    @GetMapping("/hello")
    public String sayHello() {
        return "Привет! Наш трекер JavaRush скоро оживет!";
    }

    @GetMapping("/add")
    public String addTask(
            @RequestParam @NotBlank @Size(min = 3) String title,
            @RequestParam @Min(0) @Max(40) Integer level,

            // Делаем эти параметры необязательными
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Difficulty difficulty
    ){
        Task task = new Task();
        task.setTitle(title);
        task.setTopic(topic);
        task.setLevel(level);
            // Умная проверка: если пользователь передал статус/сложность — ставим их.
            // Если НЕ передал — сработают дефолтные значения из класса Task.
        if (status != null) task.setStatus(status);
        if (difficulty != null)task.setDifficulty(difficulty);

        taskRepository.save(task);

        return "Задача" + title + " для" + level + " уровня сохранена";
    }

    @GetMapping("/all")
    public List<Task> getAllTask(){
        // Используем встроенную сортировку Spring Data JPA
        return taskRepository.findAll();
    }

    @GetMapping("/delete")
    public String deleteTask (@RequestParam Long id){
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
@GetMapping("/update-status")
public String updateStatus(@RequestParam Long id, @RequestParam Status newStatus) {
    // 1. Пытаемся найти задачу в базе по ID
    return taskRepository.findById(id).map(task -> {
        // 2. Если нашли — меняем статус
        task.setStatus(newStatus);
        // 3. Сохраняем обновленный объект
        taskRepository.save(task);
        return "Статус задачи #" + id + " успешно изменен на " + newStatus;
    }).orElse("Ошибка: Задача с таким ID не найдена.");
}
@GetMapping("/update-difficulty")
    public String updateDifficulty(@RequestParam Long id, @RequestParam Difficulty newDifficulty){
    // 1. Пытаемся найти задачу в базе по ID
    return taskRepository.findById(id).map(task -> {
        // 2. Если нашли — меняем сложность
        task.setDifficulty(newDifficulty);
        // 3. Сохраняем обновленный объект
        taskRepository.save(task);
        return "Сложность задачи #" + id + " успешно изменен на " + newDifficulty;
    }).orElse("Ошибка: Задача с таким ID не найдена.");
}
    //поиск задачи по введенной title
    //1) добавили в TaskRepository - List<Task> findByTitleContainingIgnoreCase(String title);
    //2) ...
@GetMapping("/search")
    public List<Task> searchTasks (@RequestParam String title){
        //Просто вызываем наш новый метод из репозитория
    return taskRepository.findByTitleContainingIgnoreCase(title);
}
    //поиск задачи по введенной topic
    //1) добавили в TaskRepository - List<Task> findByTopicIgnoreCase (String topic);
    //2) ...
@GetMapping("/by-topic")
public List<Task> getTasksByTopic (@RequestParam String topic){
        return taskRepository.findByTopicIgnoreCase(topic);
}

@GetMapping("/filter")
public List<Task> filterTasks (@RequestParam String topic, @RequestParam Status status){
     return taskRepository.findByTopicIgnoreCaseAndStatus(topic, status);
}
@GetMapping("by-status-sorted")
public List<Task> getByStatusSorted (@RequestParam Status status){
        return taskRepository.findByStatusOrderByLevelDesc(status);
}


@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerValidationException(ConstraintViolationException e){
        // Джарвис вежливо сообщает об ошибке вместо того, чтобы падать
    return ResponseEntity.badRequest().body("Сэр, данные некоректны: " + e.getMessage());
}
}
