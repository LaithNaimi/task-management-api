package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.CategoryResponseDTO;
import com.laith.taskmanagement.dto.CreateCategoryRequestDTO;
import com.laith.taskmanagement.dto.UpdateCategoryRequestDTO;
import com.laith.taskmanagement.exception.CategoryAlreadyExistsException;
import com.laith.taskmanagement.exception.CategoryInUseException;
import com.laith.taskmanagement.exception.CategoryNotFoundException;
import com.laith.taskmanagement.mapper.CategoryMapper;
import com.laith.taskmanagement.model.Category;
import com.laith.taskmanagement.repository.CategoryRepository;
import com.laith.taskmanagement.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;
    private final TaskRepository taskRepo;
    public CategoryService(CategoryRepository categoryRepo,TaskRepository taskRepo, CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;
        this.categoryMapper = categoryMapper;
        this.taskRepo = taskRepo;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return categories.stream()
                .map(c -> categoryMapper.mapEntityToDTO(c))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.mapEntityToDTO(category);
    }

    @Transactional
    public CategoryResponseDTO addCategory(CreateCategoryRequestDTO newCategory) {
        String name = newCategory.getName().trim();
        categoryRepo.findByNameIgnoreCase(name).ifPresent(c -> {
            throw new CategoryAlreadyExistsException(name);
        });

        Category category = new Category();
        category.setName(newCategory.getName().trim());
        return categoryMapper.mapEntityToDTO(categoryRepo.save(category));
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, UpdateCategoryRequestDTO updateCategory) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        String name = updateCategory.getName().trim();

        categoryRepo.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new CategoryAlreadyExistsException(name);
            }
        });

        category.setName(name);
        Category saved = categoryRepo.save(category);

        return categoryMapper.mapEntityToDTO(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        if (taskRepo.existsByCategoryId(id)) {
            throw new CategoryInUseException(id);
        }

        categoryRepo.delete(category);
    }
}
