package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public RegistrationController(UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    // Показываем страницу регистрации (localhost:8080/register)
    @GetMapping("/register")
    public String showForm(){
        return "register"; // Ищет файл register.html в templates
    }

    // Обрабатываем нажатие кнопки "Зарегистрироваться"
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model){
        // 1. Проверяем, есть ли уже такой пользователь в базе
        if (userRepository.findByUsername(username) != null){
        // Если нашли — не сохраняем, а возвращаем на страницу регистрации
            model.addAttribute("error", "Логин '" + username + " уже занят. Придумайте другой");
            return "register";
        }
        // 2. Если всё чисто — сохраняем
        userDetailsService.saveNewUser(username, password);// Наш метод из сервиса
        return "redirect:/login?success"; // После успеха кидаем на вход
    }
}
