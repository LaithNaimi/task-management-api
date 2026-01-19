package com.laith.taskmanagement.mapper;

import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.model.Category;
import com.laith.taskmanagement.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponseDTO mapEntityToDTO(Task task) {
        if (task == null) {
            return null;
        }
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());

        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());

        Category category = task.getCategory();
        if(category != null) {
            dto.setCategoryId(category.getId());
            dto.setCategoryName(category.getName());
        }

        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}
