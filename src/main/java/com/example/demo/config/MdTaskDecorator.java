package com.example.demo.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

// Вспомогательный класс-декоратор
public class MdTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Захватываем MDC-контекст из текущего родительского потока (Web-потока)
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                // Если контекст был, устанавливаем его в новом асинхронном потоке
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // Обязательно очищаем за собой поток после выполнения задачи!
                MDC.clear();
            }
        };
    }
}