package com.example.demo.controller;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.dto.TaskDto;
import com.example.demo.service.TaskServiceImpl;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@Validated // ЭТА СТРОЧКА ВКЛЮЧАЕТ ПРОВЕРКУ @RequestParam
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

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
        taskService.addTask(title, level, topic);

        return "Задача" + title + " для" + level + " уровня сохранена";
    }

    @GetMapping("/delete")
    public String deleteTask(@RequestParam Long id) {
        try {
            taskService.deleteTask(id); // Сервис сам проверит и наличие, и владельца
            return "Успех! Задача #" + id + " удалена.";
        } catch (Exception e) {
            // Здесь мы получим сообщение из RuntimeException, которое написали в сервисе
            return "Ошибка: " + e.getMessage();
        }
    }

@GetMapping("/by-title")
    public List<TaskDto> getTasksByTitle (@RequestParam String title){
        //Просто вызываем наш новый метод из репозитория
    return taskService.getTasksByTitle(title);
}
    //поиск задачи по введенной topic
    //1) добавили в TaskRepository - List<Task> findByTopicIgnoreCase (String topic);
    //2) ...

@GetMapping("/by-topic")
public List<TaskDto> getTasksByTopic (@RequestParam String topic){
        return taskService.getTasksByTopic(topic);
}

@GetMapping("by-status-sorted")
public List<Task> getByStatusSorted (@RequestParam Status status){
        return taskService.getByStatusSorted(status);
}


@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerValidationException(ConstraintViolationException e){
        // программа вежливо сообщает об ошибке вместо того, чтобы падать.
    return ResponseEntity.badRequest().body("Сэр, данные некоректны: " + e.getMessage());
}
}
