package com.example.demo.controller;

import ch.qos.logback.core.model.Model;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    private final UserDetailsServiceImpl userDetailsService;

    public RegistrationController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
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
        // Проверяем, не занято ли имя
        if (userDetailsService.userExists(username)){
            return "redirect:/register?error=exists";
        }
        userDetailsService.saveNewUser(username, password);// Наш метод из сервиса
        return "redirect:/login?success"; // После успеха кидаем на вход
    }
}
