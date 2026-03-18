package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.CategoryResponseDTO;
import com.laith.taskmanagement.dto.CreateCategoryRequestDTO;
import com.laith.taskmanagement.dto.UpdateCategoryRequestDTO;
import com.laith.taskmanagement.exception.AppUserNotFoundException;
import com.laith.taskmanagement.exception.CategoryAlreadyExistsException;
import com.laith.taskmanagement.exception.CategoryInUseException;
import com.laith.taskmanagement.exception.CategoryNotFoundException;
import com.laith.taskmanagement.mapper.CategoryMapper;
import com.laith.taskmanagement.mapper.TaskMapper;
import com.laith.taskmanagement.model.AppUser;
import com.laith.taskmanagement.model.Category;
import com.laith.taskmanagement.repository.AppUserRepository;
import com.laith.taskmanagement.repository.CategoryRepository;
import com.laith.taskmanagement.repository.TaskRepository;
import com.laith.taskmanagement.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;
    private final TaskRepository taskRepo;
    private final CurrentUserService currentUserService;
    private final AppUserRepository appUserRepo;

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        Long userId = currentUserService.getCurrentUserId();

        List<Category> categories = categoryRepo.findAllByOwnerIdOrderByNameAsc(userId);

        return categories.stream()
                .map(categoryMapper::mapEntityToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Long userId = currentUserService.getCurrentUserId();

        Category category = categoryRepo.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.mapEntityToDTO(category);
    }

    @Transactional
    public CategoryResponseDTO addCategory(CreateCategoryRequestDTO newCategory) {

        Long userId = currentUserService.getCurrentUserId();
        String name = newCategory.getName().trim();

        AppUser owner = appUserRepo.findById(userId)
                .orElseThrow(() -> new AppUserNotFoundException(userId));

        if (categoryRepo.existsByOwnerIdAndNameIgnoreCase(userId, name)) {
            throw new CategoryAlreadyExistsException(name);
        }


        Category category = new Category();
        category.setName(name);
        category.setOwner(owner);
        return categoryMapper.mapEntityToDTO(categoryRepo.save(category));
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, UpdateCategoryRequestDTO updateCategory) {
        Long userId = currentUserService.getCurrentUserId();
        String name = updateCategory.getName().trim();

        Category category = categoryRepo.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new CategoryNotFoundException(id));


        if (categoryRepo.existsByOwnerIdAndNameIgnoreCase(userId, name)
                && !category.getName().equalsIgnoreCase(name)) {
            throw new CategoryAlreadyExistsException(name);
        }

        category.setName(name);
        Category saved = categoryRepo.save(category);

        return categoryMapper.mapEntityToDTO(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Long userId = currentUserService.getCurrentUserId();

        Category category = categoryRepo.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (taskRepo.existsByOwnerIdAndCategoryId(userId, id)) {
            throw new CategoryInUseException(id);
        }
        categoryRepo.delete(category);
    }
}
