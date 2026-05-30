package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
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
    public String deleteUser (@PathVariable Long id){
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

}
