package com.example.demo.service;

import com.example.demo.Entity.Status;
import com.example.demo.repository.TaskRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskAnalysisService {

    private final TaskRepository taskRepository;

    public TaskAnalysisService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async("taskExecutor") // Говорим Spring выполнять это в отдельном потоке
    public void analyzeTask(Long taskId){
        try {
            // Имитируем тяжелые расчеты (например, проверку кода на сервере)
            Thread.sleep(5000);

            taskRepository.findById(taskId).ifPresent(task -> {
                task.setStatus(Status.DONE);
                taskRepository.save(task);
                System.out.println(">>> Статус задачи #" + taskId + " обновлен на DONE в фоновом потоке!");
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
