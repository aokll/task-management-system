package com.example.demo.config;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
         if (userRepository.findByUsername("superadmin") == null){
            User admin = new User();
            admin.setUsername("superadmin");
            admin.setPassword(passwordEncoder.encode("12345"));
            admin.setActive(true);
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));// Даем сразу обе роли
            userRepository.save(admin);
            System.out.println("--- СУПЕР-АДМИН СОЗДАН (логин: admin, пароль: 12345) ---");
        }else {
             System.out.println("--- СУПЕР-АДМИН УЖЕ ЕСТЬ В БАЗЕ, ПРОПУСКАЕМ ---");
         }
    }
}
