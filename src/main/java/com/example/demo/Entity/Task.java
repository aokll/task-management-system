package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity// Говорим Spring, что это таблица в БД
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor

//Трекинг задач (Хранение всех задач)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String topic;
    private Integer level;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEEDS_TO_BE_DECIDED;
        // NEEDS_TO_BE_DECIDED(нужно решить),
        // DONE(сделанный),
        // NEEDS_TO_BE_REPEATED_IN_A_WEEK(Нужно повторить через неделю),
        // IN_PROGRESS(в процессе)

    @Enumerated(EnumType.STRING)
        // В базе сохранится как текст "EASY", а не число 0
    private Difficulty difficulty = Difficulty.MEDIUM;
        // EASY, MEDIUM, HARD

    @CreationTimestamp // Магия: Spring сам подставит текущее время при сохранении
    @Column (updatable = false) // Дату создания нельзя будет изменить потом
    private LocalDateTime createdAt;
}
