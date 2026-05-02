package com.example.demo.service;

import com.example.demo.Entity.Task;
import com.example.demo.dto.TaskDto;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto toDto (Task task){
    TaskDto dto = new TaskDto();
    dto.setId(task.getId());
    dto.setTitle(task.getTitle());
    dto.setLevel(task.getLevel());
    dto.setTopic(task.getTopic());
    dto.setStatus(task.getStatus().name());
    dto.setDifficulty(task.getDifficulty().name());
        // Достаем только имя владельца! Безопасно и легко.
    dto.setOwnerName(task.getOwner().getUsername());
    return dto;
    }

}
