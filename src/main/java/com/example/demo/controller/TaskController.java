package com.example.demo.controller;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.dto.TaskDto;
import com.example.demo.service.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Управление задачами", description = "Операции для поиска, добавления и удаления задач трекера")
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    // Сюда мы скоро добавим методы для создания и просмотра твоих задач

    @GetMapping("/hello")
    @Operation(summary = "Приветственное сообщение"
            , description = "Проверка работоспособности")
    public String sayHello() {
        return "Привет! Наш трекер JavaRush скоро оживет!";
    }

    @GetMapping("/add")
    @Operation(summary = "Создать новую задачу"
            , description = "Принимает параметры задачи и сохраняет ее в базе с привязкой к текущему пользователю")
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
    @Operation(summary = "Удалить задачу по ID"
            , description = "Удаляет задачу, если она существует и принадлежит текущему авторизованному пользователю")
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
@Operation(summary = "Поиск задач по заголовку"
        , description = "Возвращает список DTO задач, в названии которых содержится поисковая строка (без учета регистра)")
    public List<TaskDto> getTasksByTitle (@RequestParam String title){
        //Просто вызываем наш новый метод из репозитория
    return taskService.getTasksByTitle(title);
}
    //поиск задачи по введенной topic
    //1) добавили в TaskRepository - List<Task> findByTopicIgnoreCase (String topic);
    //2) ...

@GetMapping("/by-topic")
@Operation(summary = "Поиск задач по теме"
        , description = "Возвращает список DTO задач по конкретной теме (без учета регистра)")
public List<TaskDto> getTasksByTopic (@RequestParam String topic){
        return taskService.getTasksByTopic(topic);
}

@GetMapping("by-status-sorted")
@Operation(summary = "Фильтрация по статусу с сортировкой"
        , description = "Возвращает сущности задач, отсортированные от сложных уровней к легким")
public List<Task> getByStatusSorted (@RequestParam Status status){
        return taskService.getByStatusSorted(status);
}


@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerValidationException(ConstraintViolationException e){
        // программа вежливо сообщает об ошибке вместо того, чтобы падать.
    return ResponseEntity.badRequest().body("Сэр, данные некоректны: " + e.getMessage());
}
}
