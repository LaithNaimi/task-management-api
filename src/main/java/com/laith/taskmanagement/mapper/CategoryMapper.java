package com.laith.taskmanagement.mapper;

import com.laith.taskmanagement.dto.CategoryResponseDTO;
import com.laith.taskmanagement.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO mapEntityToDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
