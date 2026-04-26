package com.example.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity// Говорим Spring, что это таблица в БД
@Table(name = "tasks")
@Data // Lombok сам создаст геттеры и сеттеры

//Трекинг задач (Хранение всех задач)
public class Task {
    @CreationTimestamp // Магия: Spring сам подставит текущее время при сохранении
    @Column (updatable = false) // Дату создания нельзя будет изменить потом
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
        // В базе сохранится как текст "EASY", а не число 0
    private Difficulty difficulty = Difficulty.MEDIUM;
        // EASY, MEDIUM, HARD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Сэр, название задачи не может быть пустым")
    @Size(min = 3, max = 50, message = "Название должно быть от 3 до 50 символов")
    private String title;
        // Название, например "Амиго не справляется"
    @Min(value = 0, message = "Минимальный уровень  на JavaRush - 0")
    @Max(value = 40, message = "Максимальный уровень на JavaRush - 40")
    private Integer level;
        // Уровень (например, 34)

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEEDS_TO_BE_DECIDED;
        // NEEDS_TO_BE_DECIDED(нужно решить),
        // DONE(сделанный),
        // NEEDS_TO_BE_REPEATED_IN_A_WEEK(Нужно повторить через неделю),
        // IN_PROGRESS(в процессе)

    private String topic;
        // Например: "Multithreading"


}
