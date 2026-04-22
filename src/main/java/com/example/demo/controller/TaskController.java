package com.example.demo.controller;

import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor // Автоматически внедрит Repository через конструктор
public class TaskController {

    private final TaskRepository taskRepository;

    @PostMapping
    public Task addTask(@RequestBody Task task){
        return taskRepository.save(task);
    }

    @GetMapping
    public List<Task> getAllTask(){
        // Используем встроенную сортировку Spring Data JPA
        return taskRepository.findAll();
    }

    @DeleteMapping("/{id}")// Удаление через /api/tasks/1
    public ResponseEntity<Void> deleteTask (@PathVariable Long id){
            if (taskRepository.existsById(id)){
                taskRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }else {
                return ResponseEntity.notFound().build();
            }
    }

@PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus (
            @PathVariable Long id,
            @RequestParam Status newStatus){
        return taskRepository.findById(id).map(task -> {
            task.setStatus(newStatus);
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }
}
