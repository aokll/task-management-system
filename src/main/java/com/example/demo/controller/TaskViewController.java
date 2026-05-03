package com.example.demo.controller;

import com.example.demo.Entity.*;
import com.example.demo.dto.TaskDto;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
public class TaskViewController {
    private final TaskService taskService;
    private final UserRepository userRepository;


    public TaskViewController(TaskService taskService, UserRepository userRepository) {

        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard (Model model, Principal principal){
        String username = principal.getName();// Spring сам подставит имя вошедшего юзера

        //Находим юзера и проверяем роль вручную
        User user = userRepository.findByUsername(username);
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);

        // 1. Берем все задачи из базы
        List<TaskDto> allTasks = taskService.getAllSortedByLevel();

        model.addAttribute("tasks", allTasks);
        model.addAttribute("username", username);// Передаем имя в шаблон
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("percent", taskService.calculateProgress(allTasks));
        model.addAttribute("completedCount", allTasks.stream().filter(t -> "DONE".equals(t.getStatus())).count());
        model.addAttribute("totalCount", (long) allTasks.size());

        // 3. Возвращаем имя HTML-файла (без расширения .html)
        return "tasks";
    }
    @PostMapping("/dashboard/add")
    public String addTaskFromForm(
            @RequestParam String title,
            @RequestParam Integer level,
            @RequestParam String topic
    ) {
        taskService.addTask(title, level, topic);

        // МАГИЯ: Перенаправляем пользователя обратно на главную страницу, чтобы он увидел обновленный список
        return "redirect:/dashboard";
    }
    // 1. Метод для смены статуса на DONE
    @PostMapping("/dashboard/done/{id}")
    public String markAsDone(@PathVariable Long id){
        // Находим задачу по ID (используем Optional, чтобы не упасть)
        taskService.markAsDone(id);
        return "redirect:/dashboard";// Возвращаемся на страницу
    }
    // 2. Метод для удаления задачи
    @PostMapping("/dashboard/delete/{id}")
    public String deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return "redirect:/dashboard";
    }

    // 3. Открытие формы
    @GetMapping("/dashboard/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        model.addAttribute("task", taskService.getTaskById(id));
        return "edit_task";
    }
    // 4. Принятие данных
    @PostMapping("/dashboard/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam Integer level,
                             @RequestParam String topic,
                             @RequestParam Difficulty difficulty){
        taskService.updateTask(id, title, level, topic, difficulty);
        return "redirect:/dashboard";
    }
}