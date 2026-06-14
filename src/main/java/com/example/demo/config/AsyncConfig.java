package com.example.demo.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync // САМОЕ ГЛАВНОЕ: включаем поддержку асинхронности
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 2 потока работают всегда
        executor.setMaxPoolSize(5); // До 5 при большой нагрузке
        executor.setQueueCapacity(10); // Очередь на 10 задач
        executor.setThreadNamePrefix("JavaTracker-Async-");
        executor.initialize();
        return executor;
    }
}