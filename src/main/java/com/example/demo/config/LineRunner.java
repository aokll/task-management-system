package com.example.demo.config;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.repository.TaskRepository;
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
    public CommandLineRunner initData(TaskRepository taskRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder encoder){
        return args -> {
            System.out.println("==================================================");
            System.out.println("Запуск ручной проверки логики...");

            // 1. СНАЧАЛА создаем пользователя
            User admin = userRepository.findByUsername("admin");
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("12345"));
                admin.setActive(true);
                admin.setRoles(Set.of(Role.ADMIN));
                admin = userRepository.save(admin); // Сохраняем и получаем объект с ID
                System.out.println(">>> Пользователь 'admin' создан!");
            }

            // 2. ПОТОМ добавляем задачу (напрямую через репозиторий, чтобы обойти SecurityContext)
            if (taskRepository.count() == 0) {
                Task testTask = new Task();
                testTask.setTitle("Изучить Multi-user Logic");
                testTask.setLevel(34);
                testTask.setTopic("Spring Security");
                testTask.setOwner(admin); // ЯВНО привязываем админа
                testTask.setStatus(Status.NEEDS_TO_BE_DECIDED);

                taskRepository.save(testTask);
                System.out.println(">>> Тестовая задача для 'admin' добавлена!");
            }


            // 2. ДОБАВЛЯЕМ AMIGO (Второй пользователь)
            User amigo = userRepository.findByUsername("amigo");
            if (amigo == null) {
                amigo = new User();
                amigo.setUsername("amigo");
                amigo.setPassword(encoder.encode("java")); // Пароль для Амиго
                amigo.setActive(true);
                amigo.setRoles(Set.of(Role.USER));
                amigo = userRepository.save(amigo);
                System.out.println(">>> Пользователь 'amigo' создан (пароль: java)!");
            }

            // 3. Добавляем секретную задачу ТОЛЬКО для Amigo
            if (taskRepository.findByOwnerUsername("amigo").isEmpty()) {
                Task amigoTask = new Task();
                amigoTask.setTitle("Секретный план захвата JavaRush");
                amigoTask.setLevel(40);
                amigoTask.setTopic("Top Secret");
                amigoTask.setOwner(amigo); // Владелец - Амиго!
                amigoTask.setStatus(Status.IN_PROGRESS);

                taskRepository.save(amigoTask);
                System.out.println(">>> Добавлена секретная задача для 'amigo'!");
            }

            System.out.println("Проверка логики: УСПЕШНО!");
            System.out.println("==================================================");
        };
}
}
