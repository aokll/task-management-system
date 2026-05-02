package com.example.demo.dto;

import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private Integer level;
    private String topic;
    private String status;// Передаем как строку
    private String difficulty; // Передаем как строку
    private String ownerName; // Вот тут магия: вместо всего User даем только имя
}
