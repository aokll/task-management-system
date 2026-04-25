package com.example.demo.controller;

import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class TaskViewController {
    private final TaskRepository taskRepository;


    public TaskViewController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard (Model model){
        // 1. Берем все задачи из базы
        List<Task> allTasks = taskRepository.findAll();
        // Считаем статистику
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .count();
        int percent = (totalTasks > 0) ? (int) ((completedTasks * 100)/totalTasks) : 0;
        // Сортируем список по уровню (от меньшего к большему)
        allTasks.sort(Comparator.comparing(Task::getLevel));

        // 2. Кладем их в "коробку" (Model), чтобы HTML их увидел под именем "tasks"
        model.addAttribute("tasks", allTasks);
        model.addAttribute("percent", percent);
        model.addAttribute("completedCount", completedTasks);
        model.addAttribute("totalCount", totalTasks);

        // 3. Возвращаем имя HTML-файла (без расширения .html)
        return "tasks";
    }
    @PostMapping("/dashboard/add")
    public String addTaskFromForm(
            @RequestParam String title,
            @RequestParam Integer level,
            @RequestParam String topic
    ) {
        // Создаем новую задачу
        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setLevel(level);
        newTask.setTopic(topic);
        newTask.setStatus(com.example.demo.Entity.Status.NEEDS_TO_BE_DECIDED); // по умолчанию
        newTask.setDifficulty(com.example.demo.Entity.Difficulty.MEDIUM); // по умолчанию

        // Сохраняем
        taskRepository.save(newTask);

        // МАГИЯ: Перенаправляем пользователя обратно на главную страницу, чтобы он увидел обновленный список
        return "redirect:/dashboard";
    }
    // 1. Метод для смены статуса на DONE
    @PostMapping("/dashboard/done/{id}")
    public String markAsDone(@PathVariable Long id){
        // Находим задачу по ID (используем Optional, чтобы не упасть)
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(Status.DONE);
            taskRepository.save(task);
        });
        return "redirect:/dashboard";// Возвращаемся на страницу
    }
    // 2. Метод для удаления задачи
    @PostMapping("/dashboard/delete/{id}")
    public String deleteTask(@PathVariable Long id){
        taskRepository.deleteById(id);
        return "redirect:/dashboard";
    }
}