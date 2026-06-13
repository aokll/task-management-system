package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final UserRepository userRepository;


    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Показать список всех пользователей
    @GetMapping("/users")
    public String showAllUsers(Model model, Principal principal){
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentAdminName", principal.getName()); // Передаем имя админа
        return "admin_users";
    }

    // Удалить пользователя по ID
    @PostMapping("/users/delete/{id}")
    public String deleteUser (@PathVariable Long id, Principal principal){
        String adminName = principal.getName();

        // 1. Инициализируем MDC прямо здесь, чтобы ожили квадратные скобки []
        MDC.put("username", adminName);

        // 2. Находим имя удаляемого пользователя по ID для детального лога
        String targetUsername = userRepository.findById(id)
                .map(user -> user.getUsername())
                .orElse("НЕИЗВЕСТНЫЙ ПОЛЬЗОВАТЕЛЬ");

        // 4. Сам процесс удаления в БД (с каскадами Hibernate)
        userRepository.deleteById(id);

        log.info("АДМИН ПАНЕЛЬ: Пользователь [{}] (ID #{}) успешно удален из системы администратором [{}]",
                targetUsername, id, adminName);

        // 5. Обязательно очищаем MDC после окончания работы потока запроса
        MDC.clear();

        return "redirect:/admin/users";
    }
}
