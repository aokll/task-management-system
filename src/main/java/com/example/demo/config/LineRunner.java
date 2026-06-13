package com.example.demo.config;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Slf4j
public class LineRunner {
    @Bean
    public CommandLineRunner initData(TaskRepository taskRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder encoder){
        return args -> {
            log.info("==================================================");
            log.info("Запуск ручной проверки логики...");


            // 2. ДОБАВЛЯЕМ AMIGO (Второй пользователь)
            User amigo = userRepository.findByUsername("amigo");
            if (amigo == null) {
                amigo = new User();
                amigo.setUsername("amigo");
                amigo.setPassword(encoder.encode("java")); // Пароль для Амиго
                amigo.setActive(true);
                amigo.setRoles(Set.of(Role.USER));
                amigo = userRepository.save(amigo);
                log.info(">>> Пользователь 'amigo' создан (пароль: java)!");
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
                log.info(">>> Добавлена секретная задача для 'amigo'!");
            }

            log.debug("Проверка логики: УСПЕШНО!");
            log.debug("==================================================");
        };
}
}
