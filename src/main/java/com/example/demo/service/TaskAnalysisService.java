package com.example.demo.service;

import com.example.demo.Entity.Status;
import com.example.demo.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskAnalysisService {

    private final TaskRepository taskRepository;

    public TaskAnalysisService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async("taskExecutor") // Говорим Spring выполнять это в отдельном потоке
    public void analyzeTask(Long taskId){
        // INFO: Фиксируем, что фоновый поток успешно перехватил задачу в обработку
        log.info("Фоновый поток запущен. Начало тяжелого анализа для задачи ID #{}", taskId);
        try {
            // DEBUG: Записываем техническую деталь о начале имитации долгих расчетов
            log.debug("Поток уходит в режим ожидания (sleep 5 сек) для симуляции проверки кода задачи #{}", taskId);

            // Имитируем тяжелые расчеты (например, проверку кода на сервере)
            Thread.sleep(5000);

            taskRepository.findById(taskId).ifPresentOrElse(task -> {
                task.setStatus(Status.DONE);
                taskRepository.save(task);
                // INFO: Главная победа фонового движка — статус успешно обновлен!
                log.info("Фоновый анализ завершен УСПЕШНО. Статус задачи #{} изменен на DONE", taskId);
            }, () -> {
                // ERROR: Пограничная ситуация — пока шел анализ, задачу кто-то удалил из базы!
                log.error("КРИТИЧЕСКАЯ ОШИБКА: Задача #{} не найдена в БД по окончании фонового анализа!", taskId);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
