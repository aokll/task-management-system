package com.example.demo.config;

import com.example.demo.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLOutput;
@Configuration
public class LineRunner {
    @Bean
    public CommandLineRunner initData(TaskService taskService){
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
            System.out.println("Проверка логики: УСПЕШНО!");
            System.out.println("==================================================");
        };
    }
}
