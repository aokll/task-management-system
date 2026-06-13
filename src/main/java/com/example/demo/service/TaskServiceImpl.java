package com.example.demo.service;

import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.dto.TaskDto;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Включаем логирование
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskAnalysisService analysisService;


    // 1. Безопасность (DRY): Единая точка получения имени пользователя
    private String getCurrentUsername() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        // Записываем имя текущего юзера в контекст логов для текущего потока
        MDC.put("username", username);
        return username;
    }

    // 2. Этот метод возвращает имя только одного, конкретного пользователя — того, кто прямо сейчас залогинен в приложении
    private List<TaskDto> mapListToDto(List<Task> tasks) {
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод: кто сейчас залогинен?
    private User getCurrentUser() {

        return userRepository.findByUsername(getCurrentUsername());
    }

    @Override
    public void deleteTask(Long id) {
        String username = getCurrentUsername();
        // WARN: Фиксируем сам факт намерения удалить данные
        log.warn("Пользователь [{}] инициировал удаление задачи с ID #{}", username, id);

        taskRepository.findById(id).ifPresentOrElse(task -> {
            if (task.getOwner().getUsername().equals(username)) {
                taskRepository.delete(task);
                // INFO: Успешное штатное событие
                log.info("Задача #{} успешно удалена пользователем [{}]", id, username);
            } else {
                // ERROR: Попытка несанкционированного доступа (взлома)
                log.error("НАРУШЕНИЕ БЕЗОПАСНОСТИ! Пользователь [{}] пытается удалить чужую задачу #{}", username, id);
                throw new RuntimeException("Доступ запрещен: это не ваша задача");
            }
        }, () -> {
            // ERROR: Бизнес-ошибка (запрашиваемый ресурс отсутствует)
            log.error("Ошибка удаления: Задача с ID #{} не найдена в системе", id);
            throw new RuntimeException("Задача с ID " + id + " не найдена");
        });
    }

    @Override
    public List<TaskDto> getTasksByTitle(String title) {
        String username = getCurrentUsername();
        log.debug("Поиск задач по заголовку '{}' для пользователя [{}]", title, username);
        return mapListToDto(taskRepository.findByOwnerUsernameAndTitleContainingIgnoreCase(getCurrentUsername(), title));
    }

    @Override
    public List<TaskDto> getTasksByTopic(String topic) {
        String username = getCurrentUsername();
        log.debug("Поиск задач по теме '{}' для пользователя [{}]", topic, username);
        return mapListToDto(taskRepository.findByOwnerUsernameAndTopicIgnoreCase(getCurrentUsername(), topic));
    }

    @Override
    public List<Task> getByStatusSorted(Status status) {
        return taskRepository.findByOwnerUsernameAndStatusOrderByLevelDesc(getCurrentUser().getUsername(), status);
    }

    @Override
    public List<TaskDto> getAllSortedByLevel() {
        String username = getCurrentUsername();
        log.debug("Загрузка главного экрана (Dashboard). Сортировка всех задач для [{}]", username);
        List<Task> tasks = taskRepository.findByOwnerUsername(getCurrentUsername());
        tasks.sort(Comparator.comparing(Task::getLevel));
        return mapListToDto(tasks);
    }

    @Override
    public void addTask(String title, Integer level, String topic) {
        String username = getCurrentUsername();
        // DEBUG: Записываем технические детали для отладки
        log.debug(" addTask -> Пользователь: [{}], Данные: title='{}', level={}, topic='{}'", username, title, level, topic);
        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setLevel(level);
        newTask.setTopic(topic);
        newTask.setStatus(Status.NEEDS_TO_BE_DECIDED);
        newTask.setOwner(getCurrentUser());
        taskRepository.save(newTask);

        // INFO: Главная бизнес-веха пройдена успешно
        log.info("Пользователь [{}] успешно создал задачу: '{}'", username, title);
    }

    @Override
    public int calculateProgress(List<TaskDto> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0;
        long completed = tasks.stream()
                .filter(t -> "DONE".equals(t.getStatus()))
                .count();
        return (int) ((completed * 100) / tasks.size());
    }

    @Override
    public synchronized void markAsDone(Long id) {
        String username = getCurrentUsername();
        log.debug("Запрос markAsDone для задачи #{} от пользователя [{}]", id, username);
        taskRepository.findById(id).ifPresent(task -> {
            // Проверка безопасности через наш метод
            if (task.getOwner().getUsername().equals(getCurrentUsername())
                    && task.getStatus() != Status.IN_PROGRESS) {

                task.setStatus(Status.ANALYZING);
                taskRepository.saveAndFlush(task);

                // INFO: Фиксируем критически важную смену статуса и уход в асинхронность
                log.info("Задача #{} переведена в статус ANALYZING. Передача в асинхронный движок анализа.", id);
                analysisService.analyzeTask(task.getId());
            } else {
                // WARN: Пограничное состояние (например, Race Condition при повторном клике)
                log.warn("Смена статуса для задачи #{} отклонена: неверный владелец или некорректный текущий статус", id);
            }
        });
    }


    @Override
    public TaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .filter(task -> task.getOwner().getUsername().equals(getCurrentUser().getUsername()))
                .map(taskMapper::toDto).orElseThrow(() -> new RuntimeException("Задача не найдена или доступ запрещен"));
    }

    @Override
    public void updateTask(Long id, String title, Integer level, String topic, Difficulty difficulty) {
        String username = getCurrentUsername();
        taskRepository.findById(id).ifPresentOrElse(task -> {
            if (task.getOwner().getUsername().equals(username)) {
                task.setTitle(title);
                task.setLevel(level);
                task.setTopic(topic);
                task.setDifficulty(difficulty);
                taskRepository.save(task);

                log.info("Пользователь [{}] успешно обновил параметры задачи #{}", username, id);
            } else {
                log.warn("Отказ в обновлении: Пользователь [{}] не является владельцем задачи #{}", username, id);
            }
        }, () -> log.error("Ошибка обновления: Задача #{} не найдена в базе данных", id));
    }
}