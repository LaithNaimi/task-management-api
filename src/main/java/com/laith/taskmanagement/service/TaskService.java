package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.CreateTaskRequestDTO;
import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.dto.UpdateTaskRequestDTO;
import com.laith.taskmanagement.exception.CategoryNotFoundException;
import com.laith.taskmanagement.exception.TaskNotFoundException;
import com.laith.taskmanagement.model.Category;
import com.laith.taskmanagement.model.Task;
import com.laith.taskmanagement.mapper.TaskMapper;
import com.laith.taskmanagement.model.TaskPriority;
import com.laith.taskmanagement.model.TaskStatus;
import com.laith.taskmanagement.repository.CategoryRepository;
import com.laith.taskmanagement.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final TaskMapper taskMapper;
    private final CategoryRepository categoryRepo;
    public TaskService(TaskRepository taskRepo, TaskMapper taskMapper,  CategoryRepository categoryRepo) {
        this.taskRepo = taskRepo;
        this.taskMapper = taskMapper;
        this.categoryRepo = categoryRepo;
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAll(TaskStatus status, TaskPriority priority, Long categoryId, String q, Pageable pageable) {

        Page<Task> page;
        Boolean hasStatus = status != null;
        Boolean hasPriority = priority != null;
        Boolean hasCategory = categoryId != null;
        boolean hasQ = q != null && !q.trim().isEmpty();

        String keyword = hasQ ? q.trim() : null;

        if (!hasQ) {
            if (hasStatus && hasPriority && hasCategory) {
                page = taskRepo.findByStatusAndPriorityAndCategoryId(status, priority, categoryId, pageable);
            } else if (hasStatus && hasPriority) {
                page = taskRepo.findByStatusAndPriority(status, priority, pageable);
            } else if (hasStatus && hasCategory) {
                page = taskRepo.findByStatusAndCategoryId(status, categoryId, pageable);
            } else if (hasPriority && hasCategory) {
                page = taskRepo.findByPriorityAndCategoryId(priority, categoryId, pageable);
            } else if (hasStatus) {
                page = taskRepo.findByStatus(status, pageable);
            } else if (hasPriority) {
                page = taskRepo.findByPriority(priority, pageable);
            } else if (hasCategory) {
                page = taskRepo.findByCategoryId(categoryId, pageable);
            } else {
                page = taskRepo.findAll(pageable);
            }

        }
        else {
            if (hasStatus && hasPriority && hasCategory) {
                page = taskRepo.findByStatusAndPriorityAndCategoryIdAndTitleContainingIgnoreCaseOrStatusAndPriorityAndCategoryIdAndDescriptionContainingIgnoreCase(
                        status, priority, categoryId, keyword,
                        status, priority, categoryId, keyword,
                        pageable
                );
            } else if (hasStatus && hasPriority) {
                page = taskRepo.findByStatusAndPriorityAndTitleContainingIgnoreCaseOrStatusAndPriorityAndDescriptionContainingIgnoreCase(
                        status, priority, keyword,
                        status, priority, keyword,
                        pageable
                );
            } else if (hasStatus && hasCategory) {
                page = taskRepo.findByStatusAndCategoryIdAndTitleContainingIgnoreCaseOrStatusAndCategoryIdAndDescriptionContainingIgnoreCase(
                        status, categoryId, keyword,
                        status, categoryId, keyword,
                        pageable
                );
            } else if (hasPriority && hasCategory) {
                page = taskRepo.findByPriorityAndCategoryIdAndTitleContainingIgnoreCaseOrPriorityAndCategoryIdAndDescriptionContainingIgnoreCase(
                        priority, categoryId, keyword,
                        priority, categoryId, keyword,
                        pageable
                );
            } else if (hasStatus) {
                page = taskRepo.findByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCase(
                        status, keyword,
                        status, keyword,
                        pageable
                );
            } else if (hasPriority) {
                page = taskRepo.findByPriorityAndTitleContainingIgnoreCaseOrPriorityAndDescriptionContainingIgnoreCase(
                        priority, keyword,
                        priority, keyword,
                        pageable
                );
            } else if (hasCategory) {
                page = taskRepo.findByCategoryIdAndTitleContainingIgnoreCaseOrCategoryIdAndDescriptionContainingIgnoreCase(
                        categoryId, keyword,
                        categoryId, keyword,
                        pageable
                );
            } else {
                page = taskRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
            }
        }

        return page.map(taskMapper::mapEntityToDTO);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getById(Long id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.mapEntityToDTO(task);
    }

    @Transactional
    public TaskResponseDTO addTask(CreateTaskRequestDTO newTask) {
        Task task = new Task();
        task.setTitle(newTask.getTitle().trim());
        task.setDescription(newTask.getDescription());
        task.setStatus(newTask.getStatus());
        task.setPriority(newTask.getPriority());
        task.setDueDate(newTask.getDueDate());


        if(newTask.getCategoryId() != null) {
            Category category = categoryRepo.findById(newTask.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(newTask.getCategoryId()));
            task.setCategory(category);
        }

        return taskMapper.mapEntityToDTO(taskRepo.save(task));
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, @Valid UpdateTaskRequestDTO updateReq) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (updateReq.getTitle() != null) {
            String title = updateReq.getTitle().trim();
            if (title.isEmpty()) {
                throw new IllegalArgumentException("title must not be blank");
            }
            task.setTitle(title);
        }

        if (updateReq.getDescription() != null) {
            task.setDescription(updateReq.getDescription());
        }
        if (updateReq.getStatus() != null) {
            task.setStatus(updateReq.getStatus());
        }
        if (updateReq.getPriority() != null) {
            task.setPriority(updateReq.getPriority());
        }
        if (updateReq.getDueDate() != null) {
            task.setDueDate(updateReq.getDueDate());
        }

        if (updateReq.getCategoryId() != null) {
            Category category = categoryRepo.findById(updateReq.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(updateReq.getCategoryId()));
            task.setCategory(category);
        }

        return taskMapper.mapEntityToDTO(taskRepo.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepo.delete(task);
    }
}
