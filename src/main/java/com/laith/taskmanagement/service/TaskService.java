package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.CreateTaskRequestDTO;
import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.dto.UpdateTaskRequestDTO;
import com.laith.taskmanagement.exception.AppUserNotFoundException;
import com.laith.taskmanagement.exception.CategoryNotFoundException;
import com.laith.taskmanagement.exception.TaskNotFoundException;
import com.laith.taskmanagement.model.Category;
import com.laith.taskmanagement.model.Task;
import com.laith.taskmanagement.mapper.TaskMapper;
import com.laith.taskmanagement.model.TaskPriority;
import com.laith.taskmanagement.model.TaskStatus;
import com.laith.taskmanagement.repository.AppUserRepository;
import com.laith.taskmanagement.repository.CategoryRepository;
import com.laith.taskmanagement.repository.TaskRepository;
import com.laith.taskmanagement.security.CurrentUserService;
import com.laith.taskmanagement.specification.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class TaskService {
    private final TaskRepository taskRepo;
    private final TaskMapper taskMapper;
    private final CategoryRepository categoryRepo;
    private final AppUserRepository appUserRepo;
    private final CurrentUserService currentUserService;


    private Long currentUserId(){
        return currentUserService.getCurrentUserId();
    }

//    private Specification<Task> ownerSpec(Long ownerId){
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
//    }

//    private Task getTaskForCurrentUserOrThrow(Long taskId){
//        Task task = taskRepo.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
//
//        if(task.getOwner() == null || !task.getOwner().equals(currentUserId())){
//            throw new TaskNotFoundException(taskId);// edit it later
//        }
//
//        return task;
//
//    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAll(
            TaskStatus status,
            TaskPriority priority,
            Long categoryId,
            String q,
            Pageable pageable
    ) {

        var specification = TaskSpecifications.build(currentUserId(), status, priority, categoryId, q);

        Page<Task> tasks = taskRepo.findAll(specification, pageable);

        return tasks.map(taskMapper::mapEntityToDTO);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getById(Long id) {
        var specification = TaskSpecifications.hasId(id).and(TaskSpecifications.hasOwnerId(currentUserId()));
        Task task = taskRepo.findOne(specification).orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.mapEntityToDTO(task);
    }

    @Transactional()
    public TaskResponseDTO addTask(CreateTaskRequestDTO newTask) {
        Task task = new Task();
        task.setTitle(newTask.getTitle().trim());
        task.setDescription(newTask.getDescription());
        task.setStatus(newTask.getStatus());
        task.setPriority(newTask.getPriority());
        task.setDueDate(newTask.getDueDate());

        var owner = appUserRepo.findById(currentUserId())
                .orElseThrow(() ->new AppUserNotFoundException(currentUserId()));
        task.setOwner(owner);

        if(newTask.getCategoryId() != null) {
            Category category = categoryRepo.findById(newTask.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(newTask.getCategoryId()));
            task.setCategory(category);
        }

        return taskMapper.mapEntityToDTO(taskRepo.save(task));
    }

    @Transactional()
    public TaskResponseDTO updateTask(Long id, UpdateTaskRequestDTO updateReq) {

        var specification = TaskSpecifications.hasId(id).and(TaskSpecifications.hasOwnerId(currentUserId()));
        Task task = taskRepo.findOne(specification).orElseThrow(() -> new TaskNotFoundException(id));

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
        var specification = TaskSpecifications.hasId(id).and(TaskSpecifications.hasOwnerId(currentUserId()));
        Task task = taskRepo.findOne(specification).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepo.delete(task);
    }
}
