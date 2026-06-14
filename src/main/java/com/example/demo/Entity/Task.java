package com.example.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDateTime;

@Entity// Говорим Spring, что это таблица в БД
@Table(name = "tasks")
@Data // Lombok сам создаст геттеры и сеттеры

//Трекинг задач (Хранение всех задач)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название, например "Амиго не справляется"
    @Column(nullable = false)
    @NotBlank(message = "Сэр, название задачи не может быть пустым")
    @Size(min = 3, max = 50, message = "Название должно быть от 3 до 50 символов")
    private String title;

    // Уровень (например, 34)
    @Min(value = 0, message = "Минимальный уровень  на JavaRush - 0")
    @Max(value = 40, message = "Максимальный уровень на JavaRush - 40")
    private Integer level;

    // Например: "Multithreading"
    private String topic;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEEDS_TO_BE_DECIDED;
        // NEEDS_TO_BE_DECIDED(нужно решить),
        // DONE(сделанный),
        // NEEDS_TO_BE_REPEATED_IN_A_WEEK(Нужно повторить через неделю),
        // IN_PROGRESS(в процессе)

    @Enumerated(EnumType.STRING)
        // В базе сохранится как текст "EASY", а не число 0
    private Difficulty difficulty = Difficulty.MEDIUM;// EASY, MEDIUM, HARD


    @CreationTimestamp // Магия: Spring сам подставит текущее время при сохранении
    @Column (updatable = false) // Дату создания нельзя будет изменить потом
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)//Указали, что много задач могут принадлежать одному пользователю.
    //fetch = FetchType.LAZY: Это критически важно для производительности.
    //Данные пользователя не будут подгружаться из базы до тех пор, пока ты явно не вызовешь task.getOwner().

    @JoinColumn(name = "user_id", nullable = false)// В таблице tasks появится колонка user_id,
    // где будет храниться ID владельца.
    private User owner;




}
