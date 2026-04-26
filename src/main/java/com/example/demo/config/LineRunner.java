package com.example.demo.config;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class LineRunner {
    @Bean
    public CommandLineRunner initData(TaskService taskService,
                                      UserRepository userRepository,
                                      PasswordEncoder encoder){
        return args -> {
            System.out.println("==================================================");
            System.out.println("Запуск ручной проверки логики...");
            // 1. Проверяем расчет прогресса
            var tasks = taskService.getAllSortedByLevel();
            int progress = taskService.calculateProgress(tasks);

            System.out.println("Найдено задач в базе: " + tasks.size());
            System.out.println("Текущий прогресс: " + progress + "%");

            // 2. Если задач нет, можем добавить тестовую
            if (tasks.isEmpty()){
                taskService.addTask("Изучить Service Layer", 1, "Java Architecture");
                System.out.println("База была пуста, добавлена тестовая задача!");
            }
            if (userRepository.findByUsername("admin") == null){
                User admin = new User();
                admin.setUsername("admin");

                // ШИФРУЕМ ПАРОЛЬ ПЕРЕД СОХРАНЕНИЕМ!
                admin.setPassword(encoder.encode("12345"));//пароль шифруется через encoder.encode("12345")
                admin.setActive(true);
                admin.setRoles(Set.of(Role.ADMIN));
                userRepository.save(admin);
                System.out.println("Пользователь 'admin' создан (пароль: 12345)");
            }
            System.out.println("Проверка логики: УСПЕШНО!");
            System.out.println("==================================================");
        };
    }
}
