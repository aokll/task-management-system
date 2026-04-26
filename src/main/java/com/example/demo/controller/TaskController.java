package com.example.demo.controller;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
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
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Привет! Наш трекер Java скоро оживет!";
    }

    @GetMapping("/add")
    public String addTask(@RequestParam String title, @RequestParam Integer level, @RequestParam String topic) {
        // 3. Делегируем задачу сервису
        taskService.addTask(title, level, topic);
        return "Задача сохранена через Service Layer!";
    }

    @GetMapping("/all")
    public List<Task> getAllTask(){
        return taskService.allTask();
    }

    @GetMapping("/delete")
    public String deleteTask (@RequestParam Long id){
        return taskService.deleteTask(id);
    }

    @GetMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam Status newStatus) {
        return taskService.UpdateStatus(id, newStatus);
    }
    @GetMapping("/update-difficulty")
    public String updateDifficulty(@RequestParam Long id, @RequestParam Difficulty newDifficulty){
        return taskService.UpdateDifficulty(id, newDifficulty);
    }

    //поиск задачи по введенной title
    //1) добавили в TaskRepository - List<Task> findByTitleContainingIgnoreCase(String title);
    //2) ...
    @GetMapping("/search-by-title")
    public List<Task> getTasksByTitle (@RequestParam String title){
        return taskService.searchTasksByTitle(title);
    }
    //поиск задачи по введенной topic
    //1) добавили в TaskRepository - List<Task> findByTopicIgnoreCase (String topic);
    //2) ...
    @GetMapping("/search-by-topic")
    public List<Task> getTasksByTopic (@RequestParam String topic){
        return taskService.searchTasksByTopic(topic);
    }

    //Найти по теме (игнорируя регистр) и по конкретному статусу
    @GetMapping("/sort-by-topic-and-status")
    public List<Task> filterTasks (@RequestParam String topic, @RequestParam Status status){
        return taskService.sortByTopicAndStatus(topic, status);
    }
    @GetMapping("by-status-sorted")
    public List<Task> getByStatusSorted (@RequestParam Status status){
        return taskService.byStatusSorted(status);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerValidationException(ConstraintViolationException e){
        // Джарвис вежливо сообщает об ошибке вместо того, чтобы падать
        return ResponseEntity.badRequest().body("Сэр, данные некоректны: " + e.getMessage());
    }
}
